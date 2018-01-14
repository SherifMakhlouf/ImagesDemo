package com.example.pipe;


import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Reactive stream of values which are being pushed on the one end and can be observed on another
 * end.
 */
public class Pipe<T> {

    private final Set<Action1<T>> consumers = new LinkedHashSet<>();

    private Pipe() {
    }

    /**
     * @return new pipe using given source of values.
     */
    public static <T> Pipe<T> fromSource(Source<T> source) {
        final Pipe<T> pipe = new Pipe<>();

        source.registerConsumer(new Action1<T>() {
            @Override
            public void call(T value) {
                synchronized (pipe.consumers) {
                    for (Action1<T> consumer : pipe.consumers) {
                        consumer.call(value);
                    }
                }
            }
        });

        return pipe;
    }

    /**
     * Subscribes to the pipe.
     * <p>
     * For people familiar with RxJava, there are some key differences:
     * <p>
     * - There is no concept of pipe completion.
     * - Subscribing to the pipe does not invoke any actions on the producer side.
     * - Errors are never emitted.
     *
     * @param onNext callback which is notified each time new item is being pushed out of the pipe.
     * @return {@link Subscription} object which represents connection to the pipe.
     */
    public Subscription subscribe(Action1<T> onNext) {
        synchronized (consumers) {
            consumers.add(onNext);
        }

        return new Subscription(onNext);
    }

    /**
     * Subscription to {@link Pipe}.
     */
    public class Subscription {

        private final Action1<T> consumer;

        public Subscription(Action1<T> consumer) {
            this.consumer = consumer;
        }

        /**
         * Unsubscribes from the {@link Pipe} so that no new values are being accepted.
         */
        public void unsubscribe() {
            synchronized (consumers) {
                consumers.remove(consumer);
            }
        }

    }

}
