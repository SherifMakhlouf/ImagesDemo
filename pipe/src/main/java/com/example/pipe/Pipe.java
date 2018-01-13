package com.example.pipe;


/**
 * Produces values as they are being fed into the pipe.
 */
public interface Pipe<T> {

    /**
     * Subscribes to the pipe.
     * <p>
     * For people familiar with RxJava, there are some key differences:
     * <p>
     * - There is no concept of pipe completion.
     * - Subscribing to the pipe does not invoke any actions on the producer side.
     * - Error emission does not provoke automatic unsubscription.
     *
     * @param onNext  callback which is notified each time new item is being pushed out of the pipe.
     * @param onError callback which is notified each time error occurs in the pipe.
     * @return {@link Subscription} object which represents connection to the pipe.
     */
    Subscription subscribe(
            Action1<T> onNext,
            Action1<Exception> onError
    );

}
