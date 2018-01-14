package com.example.pipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subscribes to pipe and allows to verify that given set of values was emitted.
 * <p>
 * Not thread safe.
 */
public class Tester<T> {

    /**
     * @return new tester which observes the given pipe.
     */
    public static <T> Tester<T> test(Pipe<T> pipe) {
        final Tester<T> tester = new Tester<>();

        pipe.subscribe(tester::onNext);

        return tester;
    }

    private final List<T> values = new ArrayList<>();

    /**
     * Asserts that this and only this value was emitted.
     */
    @SuppressWarnings("unchecked")
    public final void assertValue(T value) {
        assertValues(value);
    }

    /**
     * Asserts that only given list of values was emitted in the same order.
     */
    @SafeVarargs
    public final void assertValues(T... input) {
        List<T> inputList = Arrays.asList(input);

        if (!values.equals(inputList)) {
            throw new AssertionError("Expected: " + inputList + "\nReceived: " + values);
        }
    }

    /**
     * Asserts that no values were emitted.
     */
    public final void assertEmpty() {
        if (!values.isEmpty()) {
            throw new AssertionError("Expected no values.\nReceived: " + values);
        }
    }

    private void onNext(T value) {
        values.add(value);
    }

}
