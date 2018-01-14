package com.example.pipe;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entry point into the pipe. Consumes values so that they can be handled by the pipe in a reactive
 * way.
 */
public class Source<T> {

    private final Set<Action1<T>> consumers = new LinkedHashSet<>();
    private final T defaultValue;

    public Source() {
        this(null);
    }

    public Source(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Pushes value into the source so that pipe will receive it.
     */
    public void push(T value) {
        if (value == null) {
            throw new NullPointerException("Null values are not permitted");
        }

        synchronized (consumers) {
            for (Action1<T> consumer : consumers) {
                consumer.call(value);
            }
        }
    }

    /**
     * Registers new consumer which would be notified about new values.
     */
    void registerConsumer(Action1<T> consumer) {
        if (defaultValue != null) {
            consumer.call(defaultValue);
        }

        synchronized (consumers) {
            consumers.add(consumer);
        }
    }

}
