package com.example.images.util.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Executors which after waits for given time and then executes only the last request which was
 * submitted during that time window.
 */
public class ThrottlingExecutor implements Executor {

    private final Executor wrappedExecutor;
    private final Scheduler scheduler;
    private final long windowSizeMillis;

    /**
     * @return new throttling executor which delegates operations to given executor.
     */
    public static ThrottlingExecutor fromExecutor(
            Executor original,
            long windowSizeMillis
    ) {
        return new ThrottlingExecutor(
                original,
                new Scheduler(),
                windowSizeMillis
        );
    }

    ThrottlingExecutor(Executor wrappedExecutor,
                       Scheduler scheduler,
                       long windowSizeMillis) {
        this.wrappedExecutor = wrappedExecutor;
        this.scheduler = scheduler;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        scheduler.cancelAll();
        scheduler.scheduleIn(
                () -> wrappedExecutor.execute(runnable),
                windowSizeMillis
        );
    }

}
