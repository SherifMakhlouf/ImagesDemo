package com.example.images.util.concurrent;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Collections.synchronizedSet;

/**
 * Schedules the tasks at the specified time.
 */
public class Scheduler {

    private final Timer timer = new Timer();
    private final Set<TimerTask> scheduledTasks = synchronizedSet(new HashSet<>());

    /**
     * Schedules task to be executed after given delay.
     */
    public void scheduleIn(@NonNull Runnable task, long delayMs) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();

                scheduledTasks.remove(this);
            }
        };

        scheduledTasks.add(timerTask);

        timer.schedule(
                timerTask,
                delayMs
        );
    }

    /**
     * Cancels all scheduled tasks.
     */
    public void cancelAll() {
        synchronized (scheduledTasks) {
            for (TimerTask task : scheduledTasks) {
                task.cancel();
            }

            scheduledTasks.clear();
        }
    }

}
