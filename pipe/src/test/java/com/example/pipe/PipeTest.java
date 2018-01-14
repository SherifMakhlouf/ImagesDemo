package com.example.pipe;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class PipeTest {

    Source<String> source = new Source<>();

    @Test
    public void valuesShouldBePropagatedToSubscribers() throws Exception {
        // Given
        Pipe<String> pipe = Pipe.fromSource(source);

        final AtomicReference<String> receivedValue = new AtomicReference<>();

        // When
        pipe.subscribe(new Action1<String>() {
            @Override
            public void call(String value) {
                receivedValue.set(value);
            }
        });

        source.push("value");

        // Then
        assertEquals(
                "value",
                receivedValue.get()
        );
    }

    @Test
    public void unsubscribedSubscriptionsShouldNotReceiveValues() throws Exception {
        // Given
        Pipe<String> pipe = Pipe.fromSource(source);

        final AtomicReference<String> receivedValue = new AtomicReference<>();

        // When
        pipe
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String value) {
                        receivedValue.set(value);
                    }
                })
                .unsubscribe();

        source.push("value");

        // Then
        assertNull(
                receivedValue.get()
        );
    }
}