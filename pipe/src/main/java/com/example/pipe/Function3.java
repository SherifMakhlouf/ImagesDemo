package com.example.pipe;

/**
 * Function which takes 3 arguments and returns a result.
 */
public interface Function3<T1, T2, T3, R> {

    /**
     * Invokes the function.
     */
    R call(T1 a, T2 b, T3 c);

}
