package com.example.images.util;


import org.junit.Test;

import java.io.ByteArrayInputStream;

import static junit.framework.Assert.assertEquals;

public class IoUtilsTest {

    @Test
    public void readToString() throws Exception {
        // Given
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());

        // When
        String result = IoUtils.readToString(inputStream);

        // Then
        assertEquals(
                "test",
                result
        );
    }

}