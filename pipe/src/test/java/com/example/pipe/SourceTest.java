package com.example.pipe;


import org.junit.Test;

import static com.example.pipe.Tester.test;

public class SourceTest {

    @Test(expected = NullPointerException.class)
    public void nullValuesAreNotPermitted() throws Exception {
        // Given
        Source<String> source = new Source<>();

        // When
        source.push(null);

        // Then
        // Expect exception
    }

    @Test
    public void replayLastValue() throws Exception {
        // Given
        Source<String> source = new Source<>();

        // When
        source.push("value");
        Pipe<String> pipe = Pipe.fromSource(source);
        Tester<String> tester = test(pipe);

        // Then
        tester.assertValue("value");
    }

}