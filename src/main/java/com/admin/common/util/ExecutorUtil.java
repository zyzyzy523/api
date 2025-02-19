package com.admin.common.util;



import cn.hutool.core.collection.CollUtil;
import com.admin.async.AsyncTask;
import com.admin.async.CuxAsyncTaskExecutor;
import com.admin.exception.BizException;
import com.admin.exception.Msg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * executor 线程池执行工具类
 * </p>
 *
 * @author bin.xie
 * @date 2019/9/5
 */
@Slf4j
public class ExecutorUtil extends ExecutorManager {

    private static Executor BASE_EXECUTOR = null;

    private ExecutorUtil() {

    }

    /**
     * 获取系统默认的线程池 {@link CuxAsyncTaskExecutor} 如果没有配置则 创建一个新的线程池
     *
     * @return ThreadPoolExecutor
     */
    public static Executor getDefaultExecutor() {
        if (BASE_EXECUTOR != null) {
            return BASE_EXECUTOR;
        }
        Object taskExecutor = null;
        try {
            taskExecutor = SpringContextUtils.getApplicationContext().getBean("taskExecutor");
        } catch (Exception e) {
            log.warn("获取系统配置的bean: [{}] 出现异常！", "taskExecutor", e);
        }
        if (null != taskExecutor) {
            BASE_EXECUTOR = (Executor) taskExecutor;
        } else {
            BASE_EXECUTOR = buildThreadFirstExecutor("BasicExecutor");
        }
        return BASE_EXECUTOR;
    }


    public static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
        Executor defaultExecutor = getDefaultExecutor();
        if (defaultExecutor instanceof CuxAsyncTaskExecutor) {
            return ((CuxAsyncTaskExecutor) defaultExecutor).getExecutor().getThreadPoolExecutor();
        }
        return (ThreadPoolExecutor) defaultExecutor;
    }


    /**
     * 批量提交异步任务，使用默认的线程池
     *
     * @param tasks 将任务转化为 AsyncTask 批量提交
     */
    public static <T> List<T> batchExecuteAsync(List<AsyncTask<T>> tasks, @Nonnull String taskName) {
        return batchExecuteAsync(tasks, getDefaultExecutor(), taskName);
    }

    public static <T> void batchSubmit(List<AsyncTask<T>> tasks, @Nonnull String taskName) {
        batchSubmit(tasks, getDefaultExecutor(), taskName);
    }

    public static <T> void submit(AsyncTask<T> task, @Nonnull String taskName) {
        batchSubmit(Collections.singletonList(task), getDefaultExecutor(), taskName);
    }

    public static <T> void batchSubmit(List<AsyncTask<T>> tasks, @Nonnull Executor executor, @Nonnull String taskName) {
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }
        boolean debugEnabled = log.isDebugEnabled();
        int size = tasks.size();
        List<Runnable> callables = tasks.stream().map(t -> (Runnable) () -> {
            try {
                t.doExecute();
                if (debugEnabled) {
                    log.debug("[>>Executor<<] Async task execute success. ThreadName: [{}], BatchTaskName: [{}], " +
                              "SubTaskName: [{}]",
                            Thread.currentThread().getName(), taskName, t.taskName());
                }
            } catch (Throwable e) {
                log.warn("[>>Executor<<] Async task execute error. ThreadName: [{}], BatchTaskName: [{}], " +
                         "SubTaskName: [{}], exception: {}",
                        Thread.currentThread().getName(), taskName, t.taskName(), e.getMessage());
                throw e;
            }
        }).collect(Collectors.toList());

        for (Runnable task : callables) {
            task = wrapRunnable(task);
            executor.execute(task);
        }
    }

    public static <T> T executeAsync(AsyncTask<T> task, @Nonnull String taskName) {
        List<T> ts = batchExecuteAsync(Collections.singletonList(task), getDefaultExecutor(), taskName);
        return CollUtil.isNotEmpty(ts) ? ts.get(0) : null;
    }

    public static <T> List<T> batchExecuteAsync(List<AsyncTask<T>> tasks, @Nonnull String taskName, int timeout) {
        return batchExecuteAsync(tasks, getDefaultExecutor(), taskName, timeout);
    }

    public static <T> List<T> batchExecuteAsync(@Nonnull List<AsyncTask<T>> tasks, @Nonnull Executor executor,
                                                @Nonnull String taskName) {
        return batchExecuteAsync(tasks, executor, taskName, 6);
    }

    /**
     * 批量提交异步任务，执行失败可抛出异常或返回异常编码即可 <br>
     * <p>
     * 需注意提交的异步任务无法控制事务，一般需容忍产生一些垃圾数据的情况下才能使用异步任务，异步任务执行失败将抛出异常，主线程可回滚事务.
     * <p>
     * 异步任务失败后，将取消剩余的任务执行.
     *
     * @param tasks    将任务转化为 AsyncTask 批量提交
     * @param executor 线程池，需自行根据业务场景创建相应的线程池
     * @return 返回执行结果
     */
    public static <T> List<T> batchExecuteAsync(@Nonnull List<AsyncTask<T>> tasks, @Nonnull Executor executor,
                                                @Nonnull String taskName, int timeout) {
        return batchExecuteAsync(tasks, executor, taskName, timeout, TimeUnit.MINUTES);
    }


    public static <T> List<T> batchExecuteAsync(@Nonnull List<AsyncTask<T>> tasks, @Nonnull Executor executor,
                                                @Nonnull String taskName, long timeout, TimeUnit unit) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        boolean debugEnabled = log.isDebugEnabled();
        //boolean flag = executor instanceof FabricAsyncTaskExecutor;
        int size = tasks.size();

        List<Callable<T>> callables = tasks.stream().map(t -> (Callable<T>) () -> {
            try {
                T r = t.doExecute();
                if (debugEnabled) {
                    log.debug("[>>Executor<<] Async task execute success. ThreadName: [{}], BatchTaskName: [{}], " +
                              "SubTaskName: [{}]",
                            Thread.currentThread().getName(), taskName, t.taskName());
                }
                return r;
            } catch (Throwable e) {
                log.warn("[>>Executor<<] Async task execute error. ThreadName: [{}], BatchTaskName: [{}], " +
                         "SubTaskName: [{}], exception: {}",
                        Thread.currentThread().getName(), taskName, t.taskName(), e.getMessage());
                throw e;
            }
        }).collect(Collectors.toList());

        CompletionService<T> cs = new ExecutorCompletionService<>(executor, new LinkedBlockingQueue<>(size));
        List<Future<T>> futures = new ArrayList<>(size);
        if (log.isDebugEnabled()) {
            log.debug("[>>Executor<<] Start async tasks, BatchTaskName: [{}], TaskSize: [{}]", taskName, size);
        }
        for (Callable<T> task : callables) {
            task = wrapCallable(task);
            futures.add(cs.submit(task));
        }

        List<T> resultList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            try {
                Future<T> future = cs.poll(timeout, unit);
                if (future != null) {
                    T result = future.get();
                    resultList.add(result);

                    Object logger;
                    if (result instanceof Collection) {
                        logger = ((Collection<?>) result).size();
                    } else {
                        logger = result;
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("[>>Executor<<] Async task [{}] - [{}] execute success, result: {}", taskName, i,
                                logger == null ? "null" : logger);
                    }
                } else {
                    cancelTask(futures);
                    log.error("[>>Executor<<] Async task [{}] - [{}] execute timeout, then cancel other tasks.",
                            taskName, i);
                    throw new BizException(Msg.SYSTEM_EXCEPTION);
                }
            } catch (ExecutionException e) {
                log.warn("[>>Executor<<] Async task [{}] - [{}] execute error, then cancel other tasks.", taskName, i
                        , e);
                cancelTask(futures);
                Throwable throwable = e.getCause();
                if (throwable instanceof BizException) {
                    throw (BizException) throwable;
                } else if (throwable instanceof DuplicateKeyException) {
                    throw (DuplicateKeyException) throwable;
                } else {
                    throw new BizException(Msg.SYSTEM_EXCEPTION, e);
                }
            } catch (InterruptedException e) {
                cancelTask(futures);
                Thread.currentThread().interrupt(); // 重置中断标识
                log.error("[>>Executor<<] Async task [{}] - [{}] were interrupted.", taskName, i);
                throw new BizException(Msg.SYSTEM_EXCEPTION, e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("[>>Executor<<] Finish async tasks , BatchTaskName: [{}], TaskSize: [{}]", taskName, size);
        }
        return resultList;
    }


    public static Object getRunnable(FutureTask<?> futureTask) throws Exception {
        Field callableField = futureTask.getClass().getDeclaredField("callable");
        ReflectionUtils.makeAccessible(callableField);
        Callable callable = (Callable) callableField.get(futureTask);
        Field taskField = callable.getClass().getDeclaredField("task");
        ReflectionUtils.makeAccessible(taskField);
        return taskField.get(callable);
    }


    public static Runnable wrapRunnable(Runnable task) {
        return createRunnable(task);
    }

    public static <T> Callable<T> wrapCallable(Callable<T> task) {
        return createCallable(task);
    }


}
