package com.example.images.features.search.data.repository.flickr;

import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.repository.ImagesRepository;

import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

public class ResponseDeserializerTest {

    ResponseDeserializer testee = new ResponseDeserializer();

    @Test
    public void deserializeResponse() throws Exception {
        // Given
        String response = "{\n" +
                "  \"photos\":{\n" +
                "    \"page\":1,\n" +
                "    \"pages\":1986,\n" +
                "    \"photo\":[\n" +
                "      {\"id\":\"id\",\"secret\":\"secret\",\"server\":\"server\",\"farm\":1}\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        Paginated<List<ImagesRepository.Image>> expected = new Paginated<>(
                1,
                1986,
                singletonList(
                        new ImagesRepository.Image(
                                "http://farm1.static.flickr.com/server/id_secret.jpg"
                        )
                )
        );

        // When
        Paginated<List<ImagesRepository.Image>> result = testee.parseResult(response);

        // Then
        assertEquals(
                expected,
                result
        );
    }

}