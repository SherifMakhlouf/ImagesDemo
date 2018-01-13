package com.example.pipe;

/**
 * Function which takes 1 parameter and returns nothing.
 */
public interface Action1<T> {

    /**
     * Executes the function.
     */
    void call(T value);

}
