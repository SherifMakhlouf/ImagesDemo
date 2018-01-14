package com.example.pipe;

import org.junit.Test;

import static com.example.pipe.Tester.test;


public class PipesTest {

    Source<Integer> sourceA = new Source<>();
    Source<Integer> sourceB = new Source<>();
    Source<Integer> sourceC = new Source<>();

    @Test
    public void combine_EmitNothing_OneOfThePipesIsEmpty() throws Exception {
        // Given
        Pipe<String> combined = Pipes.combine(
                Pipe.fromSource(sourceA),
                Pipe.fromSource(sourceB),
                Pipe.fromSource(sourceC),
                (a, b, c) -> String.valueOf(a + b + c)
        );

        Tester<String> tester = test(combined);

        // When
        sourceA.push(1);
        sourceB.push(2);

        // Then
        tester.assertEmpty();
    }

    @Test
    public void combine_Combine() throws Exception {
        // Given
        Pipe<String> combined = Pipes.combine(
                Pipe.fromSource(sourceA),
                Pipe.fromSource(sourceB),
                Pipe.fromSource(sourceC),
                (a, b, c) -> String.valueOf(a + b + c)
        );

        Tester<String> tester = test(combined);

        // When
        sourceA.push(1);
        sourceB.push(2);
        sourceC.push(3);

        // Then
        tester.assertValue("6");
    }

    @Test
    public void combine_Combine_ReactOnUpdates() throws Exception {
        // Given
        Pipe<String> combined = Pipes.combine(
                Pipe.fromSource(sourceA),
                Pipe.fromSource(sourceB),
                Pipe.fromSource(sourceC),
                (a, b, c) -> String.valueOf(a + b + c)
        );

        Tester<String> tester = test(combined);

        // When
        sourceA.push(1);
        sourceB.push(2);
        sourceC.push(3);

        sourceC.push(0);
        sourceB.push(0);
        sourceA.push(0);

        // Then
        tester.assertValues(
                "6",
                "3",
                "1",
                "0"
        );
    }


}