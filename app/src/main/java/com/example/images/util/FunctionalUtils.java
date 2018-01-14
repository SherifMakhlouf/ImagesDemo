package com.example.images.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to simplify functional programming.
 */
public class FunctionalUtils {

    /**
     * @return new list with a mapper function applied to every element.
     */
    public static <T, R> List<R> map(List<T> input, Function1<T, R> mapper) {
        ArrayList<R> result = new ArrayList<>(input.size());

        for (T item : input) {
            result.add(
                    mapper.call(item)
            );
        }

        return result;
    }

    /**
     * Function which takes 1 argument and returns a value.
     *
     * @param <T> argument type.
     * @param <R> return type.
     */
    public interface Function1<T, R> {

        R call(T input);

    }

}
