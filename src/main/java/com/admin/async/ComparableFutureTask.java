package com.admin.async;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2020/8/19
 */
public class ComparableFutureTask extends FutureTask implements Comparable<ComparableFutureTask> {

    private final Long priority;

    public ComparableFutureTask(Callable callable, Long priority) {
        super(callable);
        this.priority = priority;
    }

    public Long getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ComparableFutureTask task) {
        return this.getPriority().compareTo(task.getPriority());
    }

}