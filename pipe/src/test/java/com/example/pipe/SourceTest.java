package com.example.pipe;


import org.junit.Test;

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

}