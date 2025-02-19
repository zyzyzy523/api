package com.admin.async;

import com.admin.common.util.ExecutorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.wildfly.common.annotation.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Slf4j
public class CuxAsyncTaskExecutor implements AsyncTaskExecutor,
        InitializingBean, DisposableBean {

    private ThreadPoolTaskExecutor executor;

    public CuxAsyncTaskExecutor(ThreadPoolTaskExecutor executor) {

        this.executor = executor;
        log.info("fabric-executor-pool,PoolSize:" + executor.getPoolSize()
                + " CorePoolSize:" + executor.getCorePoolSize()
                + " ActiveCount:" + executor.getActiveCount()
                + " MaxPoolSize:" + executor.getMaxPoolSize()
                + " KeepAliveSeconds:" + executor.getKeepAliveSeconds());

    }

    public ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(ExecutorUtil.wrapRunnable(task));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.execute(ExecutorUtil.wrapRunnable(task), startTimeout);
    }


    @Override
    public Future<?> submit(@NotNull Runnable task) {
        return executor.submit(ExecutorUtil.wrapRunnable(task));
    }

    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        return executor.submit(ExecutorUtil.wrapCallable(task));
    }

    @Override
    public void destroy() throws Exception {

        if (executor != null) {
            executor.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor != null) {
            executor.afterPropertiesSet();
        }
    }
}
