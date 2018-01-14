package com.example.images.features.search.domain;


import com.example.images.features.search.data.repository.ImagesRepository;
import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.Result;
import com.example.pipe.Pipe;
import com.example.pipe.Source;
import com.example.pipe.Tester;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.example.images.features.search.data.repository.ImagesRepository.Image;
import static com.example.images.features.search.data.Result.error;
import static com.example.images.features.search.data.Result.success;
import static com.example.pipe.Tester.test;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ImageSearchInteractorTest {

    static final Image IMAGE_A = new Image("http://example.com/a");
    static final Paginated<List<Image>> FIRST_PAGE = new Paginated<>(
            1,
            2,
            singletonList(IMAGE_A)
    );

    static final Image IMAGE_B = new Image("http://example.com/b");
    static final Paginated<List<Image>> SECOND_PAGE = new Paginated<>(
            2,
            2,
            singletonList(IMAGE_B)
    );

    static final String QUERY = "kittens";

    @Mock
    ImagesRepository repository;

    @InjectMocks
    ImageSearchInteractor testee;

    Source<Result<Paginated<List<Image>>>> firstPageSource = new Source<>();
    Source<Result<Paginated<List<Image>>>> secondPageSource = new Source<>();

    @Before
    public void setUp() throws Exception {
        given(repository.queryImages(QUERY, 1))
                .willReturn(Pipe.fromSource(firstPageSource));
        given(repository.queryImages(QUERY, 2))
                .willReturn(Pipe.fromSource(secondPageSource));
    }

    @Test
    public void loadingResults_False_ByDefault() throws Exception {
        // When
        Tester<Boolean> tester = test(testee.loadingResults());

        // Then
        tester.assertValue(false);
    }

    @Test
    public void loadingResults_True_WhenStartingSearch() throws Exception {
        // Given
        given(repository.queryImages(anyString(), anyInt()))
                .willReturn(Pipe.empty());

        Tester<Boolean> tester = test(testee.loadingResults());

        // When
        testee.search(QUERY);

        // Then
        tester.assertValues(false, true);
    }

    @Test
    public void loadingResults_False_AfterResultsAreLoaded() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.loadingResults());

        // When
        testee.search(QUERY);
        firstPageSource.push(success(new Paginated<>(
                1,
                1,
                emptyList()
        )));

        // Then
        tester.assertValues(false, true, false);
    }

    @Test
    public void searchResults_EmptyList_ByDefault() throws Exception {
        // When
        Tester<Result<List<ImagesRepository.Image>>> tester = test(testee.searchResults());

        // Then
        tester.assertValue(
                success(emptyList())
        );
    }

    @Test
    public void searchResult_SingleQuery_Success() throws Exception {
        // Given
        Tester<Result<List<ImagesRepository.Image>>> tester = test(testee.searchResults());

        // When
        testee.search(QUERY);
        firstPageSource.push(success(new Paginated<>(
                1,
                1,
                singletonList(IMAGE_A)
        )));

        // Then
        tester.assertValues(
                success(emptyList()),
                success(singletonList(IMAGE_A))
        );
    }

    @Test
    public void searchResult_SingleQuery_Failure() throws Exception {
        // Given
        Tester<Result<List<ImagesRepository.Image>>> tester = test(testee.searchResults());
        Exception throwable = new Exception();

        // When
        testee.search(QUERY);
        firstPageSource.push(error(throwable));

        // Then
        tester.assertValues(
                success(emptyList()),
                error(throwable)
        );
    }

    @Test
    public void morePagesAvailable_False_WhenNoQuery() throws Exception {
        // When
        Tester<Boolean> tester = test(testee.morePagesAvailable());

        // Then
        tester.assertValue(false);
    }

    @Test
    public void morePagesAvailable_True() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.morePagesAvailable());

        // Then
        testee.search(QUERY);
        firstPageSource.push(success(new Paginated<>(
                1,
                2,
                singletonList(IMAGE_A)
        )));

        // Then
        tester.assertValues(false, true);
    }

    @Test
    public void morePagesAvailable_False_OnlyOnePage() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.morePagesAvailable());

        // Then
        testee.search(QUERY);
        firstPageSource.push(success(new Paginated<>(
                1,
                1,
                singletonList(IMAGE_A)
        )));

        // Then
        tester.assertValues(false, false);
    }

    @Test
    public void morePagesAvailable_False_OnError() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.morePagesAvailable());

        // Then
        testee.search(QUERY);
        firstPageSource.push(error(new Exception()));

        // Then
        tester.assertValues(false, false);
    }

    @Test
    public void requestNextPage_Success() throws Exception {
        // Given
        Tester<Result<List<ImagesRepository.Image>>> tester = test(testee.searchResults());

        // When
        testee.search(QUERY);

        firstPageSource.push(
                success(FIRST_PAGE)
        );

        testee.requestNextPage();

        secondPageSource.push(
                success(SECOND_PAGE)
        );

        // Then
        tester.assertValues(
                success(emptyList()),
                success(singletonList(IMAGE_A)),
                success(asList(IMAGE_A, IMAGE_B))
        );
    }

    @Test
    public void morePagesAvailable_False_WhenAtTheLastPage() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.morePagesAvailable());

        // When
        testee.search(QUERY);

        firstPageSource.push(
                success(FIRST_PAGE)
        );

        testee.requestNextPage();

        secondPageSource.push(
                success(SECOND_PAGE)
        );

        // Then
        tester.assertValues(false, true, false);
    }

    @Test
    public void requestNextPage_FailToLoadSecondPage() throws Exception {
        // Given
        Tester<Result<List<ImagesRepository.Image>>> tester = test(testee.searchResults());

        // When
        testee.search(QUERY);

        firstPageSource.push(
                success(FIRST_PAGE)
        );

        testee.requestNextPage();

        secondPageSource.push(
                error(new Exception())
        );

        // Then
        tester.assertValues(
                success(emptyList()),
                success(singletonList(IMAGE_A))
        );
    }

    @Test
    public void loadingNextPage_False_ByDefault() throws Exception {
        // When
        Tester<Boolean> tester = test(testee.loadingNextPage());

        // Then
        tester.assertValue(false);
    }

    @Test
    public void loadingNextPage_True() throws Exception {
        // Given
        Tester<Boolean> tester = test(testee.loadingNextPage());

        // When
        testee.search(QUERY);

        firstPageSource.push(
                success(FIRST_PAGE)
        );

        testee.requestNextPage();

        secondPageSource.push(
                error(new Exception())
        );

        // Then
        tester.assertValues(false, true, false);
    }

    @Test
    public void dropOldQuery() throws Exception {
        // Given
        Source<Result<Paginated<List<Image>>>> firstQuerySource = new Source<>();
        Source<Result<Paginated<List<Image>>>> secondQuerySource = new Source<>();

        given(repository.queryImages("A", 1))
                .willReturn(
                        Pipe.fromSource(firstQuerySource)
                );

        given(repository.queryImages("B", 1))
                .willReturn(
                        Pipe.fromSource(secondQuerySource)
                );

        Tester<Result<List<Image>>> tester = test(testee.searchResults());

        // When
        testee.search("A");
        testee.search("B");

        firstQuerySource.push(
                success(FIRST_PAGE)
        );

        secondQuerySource.push(
                success(SECOND_PAGE)
        );

        // Then
        tester.assertValues(
                success(emptyList()),
                success(singletonList(IMAGE_B))
        );
    }

}