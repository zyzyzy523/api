package com.admin.common.util;


import com.admin.async.ContextProcesser;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@Slf4j
public class ExecutorManager {

    private static ContextProcesser contextProcesser;

    public static void setContextProcesser(ContextProcesser contextProcesser) {
        ExecutorManager.contextProcesser = contextProcesser;
    }

    public static ContextProcesser getContextProcesser() {
        return contextProcesser;
    }

    public ExecutorManager() {

    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * <p>
     * 此方法默认设置核心线程数为 CPU 核数，最大线程数为 8倍 CPU 核数，空闲线程超过 5 分钟销毁，工作队列大小为 65536。
     *
     * @param poolName 线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(String poolName) {
        int coreSize = getCpuProcessors();
        // 容器部署的时候 获取CPU核数很小，可能就是1，如果是小于2就默认为8
        coreSize = coreSize <= 2 ? 1 << 3 : coreSize;
        int maxSize = coreSize * 8;
        return buildThreadFirstExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, 1 << 16, poolName);
    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * <p>
     * 此方法默认设置核心线程数为用户定义的，最大线程数为 8倍 核心线程数 核数，空闲线程超过 5 分钟销毁，工作队列大小为 65536。
     *
     * @param poolName 线程池名称
     * @param coreSize 核心线程数
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadExecutor(String poolName, int coreSize) {
        // 容器部署的时候 获取CPU核数很小，可能就是1，如果是小于2就默认为8
        coreSize = coreSize <= 2 ? 1 << 3 : coreSize;
        int maxSize = coreSize * 8;
        return buildThreadFirstExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, 1 << 16, poolName);
    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   空闲线程的空闲时间
     * @param unit            时间单位
     * @param workQueueSize   工作队列容量大小
     * @param poolName        线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(int corePoolSize,
                                                              int maximumPoolSize,
                                                              long keepAliveTime,
                                                              TimeUnit unit,
                                                              int workQueueSize,
                                                              String poolName) {
        // 自定义队列，优先开启更多线程，而不是放入队列
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(workQueueSize) {
            private static final long serialVersionUID = 5075561696269543041L;

            @Override
            public boolean offer(Runnable o) {
                return false; // 造成队列已满的假象
            }
        };

        // 当线程达到 maximumPoolSize 时会触发拒绝策略，此时将任务 put 到队列中
        RejectedExecutionHandler rejectedExecutionHandler = getRejectedExecutionHandler(queue);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                queue,
                new ThreadFactoryBuilder()
                        .setNameFormat(poolName + "-%d")
                        .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                            log.error("{} catching the uncaught exception, ThreadName: [{}]", poolName, thread.toString(), throwable);
                        })
                        .build(),
                rejectedExecutionHandler
        );
        executor.allowCoreThreadTimeOut(true);
        hookShutdownThreadPool(executor, poolName);
        return executor;
    }


    /**
     * 添加Hook在Jvm关闭时优雅的关闭线程池
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     */
    public static void hookShutdownThreadPool(ExecutorService threadPool, String threadPoolName) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[>>ExecutorShutdown<<] Start to shutdown the thead pool: [{}]", threadPoolName);
            // 使新任务无法提交
            threadPool.shutdown();
            try {
                // 等待未完成任务结束
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow(); // 取消当前执行的任务
                    log.warn("[>>ExecutorShutdown<<] Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                    // 等待任务取消的响应
                    if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        log.error("[>>ExecutorShutdown<<] Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
                    }
                }
            } catch (InterruptedException ie) {
                // 重新取消当前线程进行中断
                threadPool.shutdownNow();
                log.error("[>>ExecutorShutdown<<] The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the biz logs.");

                // 保留中断状态
                Thread.currentThread().interrupt();
            }

            log.info("[>>ExecutorShutdown<<] Finally shutdown the thead pool: [{}]", threadPoolName);
        }));
    }

    /**
     * 获取返回CPU核数
     *
     * @return 返回CPU核数，默认为8
     */
    public static int getCpuProcessors() {
        int i = 0;
        try {
            i = Runtime.getRuntime() != null && Runtime.getRuntime().availableProcessors() > 0 ?
                    Runtime.getRuntime().availableProcessors() : 8;
        } catch (Exception e) {
            i = 8;
        }
        return i;
    }

    /**
     * 构建RejectedExecutionHandler
     */
    public static RejectedExecutionHandler getRejectedExecutionHandler(BlockingQueue<Runnable> queue) {
        return (runnable, executor) -> {
            try {
                // 任务拒绝时，通过 offer 放入队列
                queue.put(runnable);
            } catch (InterruptedException e) {
                log.warn("Queue offer interrupted. ", e);
                Thread.currentThread().interrupt();
            }
        };
    }

    public static <T> Callable<T> createCallable(final Callable<T> task) {
        Map<String, Object> context = contextProcesser.getContext();
        return () -> {
            try {
                contextProcesser.setContext(context);
                return task.call();
            } finally {
                contextProcesser.clearContext();
            }
        };
    }

    public static Runnable createRunnable(final Runnable task) {
        Map<String, Object> context = contextProcesser.getContext();
        return () -> {
            try {
                contextProcesser.setContext(context);
                task.run();
            } finally {
                contextProcesser.clearContext();
            }
        };
    }


    public static <T> void cancelTask(List<Future<T>> futures) {
        for (Future<T> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }
}
