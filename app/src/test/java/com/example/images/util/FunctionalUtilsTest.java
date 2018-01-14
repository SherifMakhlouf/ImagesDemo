package com.example.images.util;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;


public class FunctionalUtilsTest {

    @Test
    public void map() throws Exception {
        // Given
        List<String> input = asList("1", "2", "3");
        List<Integer> expected = asList(1, 2, 3);

        // When
        List<Integer> result = FunctionalUtils.map(
                input,
                Integer::parseInt
        );

        // Then
        assertEquals(
                expected,
                result
        );
    }
}