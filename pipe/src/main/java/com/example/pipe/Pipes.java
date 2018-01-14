package com.example.pipe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility functions for pipes.
 */
public class Pipes {

    /**
     * Combines pipes together and returns a new pipe which produces a new value each time one of
     * the pipes is updated.
     */
    public static <T1, T2, T3, R> Pipe<R> combine(
            Pipe<T1> pipeA,
            Pipe<T2> pipeB,
            Pipe<T3> pipeC,
            Function3<T1, T2, T3, R> mapper
    ) {
        Source<R> resultSource = new Source<>();

        AtomicReference<T1> valueA = new AtomicReference<>();
        AtomicReference<T2> valueB = new AtomicReference<>();
        AtomicReference<T3> valueC = new AtomicReference<>();


        pipeA.subscribe(a -> {
            valueA.set(a);

            pushCombinedResultIfNeeded(
                    valueA.get(),
                    valueB.get(),
                    valueC.get(),
                    mapper,
                    resultSource
            );
        });

        pipeB.subscribe(b -> {
            valueB.set(b);

            pushCombinedResultIfNeeded(
                    valueA.get(),
                    valueB.get(),
                    valueC.get(),
                    mapper,
                    resultSource
            );
        });

        pipeC.subscribe(c -> {
            valueC.set(c);

            pushCombinedResultIfNeeded(
                    valueA.get(),
                    valueB.get(),
                    valueC.get(),
                    mapper,
                    resultSource
            );
        });

        return Pipe.fromSource(resultSource);
    }

    private static <T1, T2, T3, R> void pushCombinedResultIfNeeded(
            T1 a,
            T2 b,
            T3 c,
            Function3<T1, T2, T3, R> mapper,
            Source<R> resultSource
    ) {
        if (a != null && b != null && c != null) {
            resultSource.push(
                    mapper.call(a, b, c)
            );
        }
    }

}
