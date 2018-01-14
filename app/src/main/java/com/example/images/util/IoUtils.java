package com.example.images.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility functions for working with I/O.
 */
public class IoUtils {

    private IoUtils() {
    }

    /**
     * Fully reads contents of the stream and converts them into string.
     */
    public static String readToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }

}
