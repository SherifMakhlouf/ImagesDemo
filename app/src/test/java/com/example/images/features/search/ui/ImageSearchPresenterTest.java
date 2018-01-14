package com.example.images.features.search.ui;

import com.example.images.features.search.data.ImagesRepository;
import com.example.images.features.search.data.Result;
import com.example.images.features.search.domain.ImageSearchInteractor;
import com.example.pipe.Pipe;
import com.example.pipe.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.example.images.features.search.data.Result.error;
import static com.example.images.features.search.data.Result.success;
import static com.example.images.features.search.ui.ImageSearchView.Item;
import static com.example.images.features.search.ui.ImageSearchView.State;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ImageSearchPresenterTest {

    static final ImagesRepository.Image REPOSITORY_IMAGE_A = new ImagesRepository.Image("http://example.com/a");

    @Mock
    ImageSearchInteractor interactor;
    @Mock
    ImageSearchView view;

    @InjectMocks
    ImageSearchPresenter testee;

    Source<Result<List<ImagesRepository.Image>>> resultsSource = new Source<>(
            success(emptyList())
    );

    @Before
    public void setUp() throws Exception {
        given(interactor.searchResults())
                .willReturn(
                        Pipe.fromSource(resultsSource)
                );

        given(interactor.loadingResults())
                .willReturn(Pipe.constant(false));

        given(interactor.morePagesAvailable())
                .willReturn(Pipe.constant(false));

        given(interactor.loadingNextPage())
                .willReturn(Pipe.constant(false));
    }

    @Test
    public void setListener() throws Exception {
        // When
        testee.start(view);

        // Then
        verify(view).setListener(testee);
    }

    @Test
    public void requestMoreResults() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.requestMoreResults();

        // Then
        verify(interactor).requestNextPage();
    }

    @Test
    public void onQueryUpdated() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.onQueryUpdated("query");

        // Then
        verify(interactor).search("query");
    }

    @Test
    public void defaultState() throws Exception {
        // When
        testee.start(view);

        // Then
        verify(view).updateState(State.Default.INSTANCE);
    }

    @Test
    public void loadingState() throws Exception {
        // Given
        given(interactor.loadingResults())
                .willReturn(Pipe.constant(true));

        // When
        testee.start(view);

        // Then
        verify(view).updateState(State.Loading.INSTANCE);
    }

    @Test
    public void noResultsState() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.onQueryUpdated("query");
        resultsSource.push(
                success(emptyList())
        );

        // Then
        verify(view).updateState(State.NoResults.INSTANCE);
    }

    @Test
    public void failureState() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.onQueryUpdated("query");
        resultsSource.push(
                error(new Exception())
        );

        // Then
        verify(view).updateState(State.Failure.INSTANCE);
    }

    @Test
    public void loadedResults_NoMoreResultsAvailable() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.onQueryUpdated("query");
        resultsSource.push(
                success(singletonList(REPOSITORY_IMAGE_A))
        );

        // Then
        verify(view).updateState(new State.LoadedResults(
                singletonList(new Item.Image(
                        REPOSITORY_IMAGE_A.url
                )),
                false
        ));
    }

    @Test
    public void loadedResults_MoreResultsAvailable() throws Exception {
        // Given
        given(interactor.morePagesAvailable())
                .willReturn(Pipe.constant(true));

        testee.start(view);

        // When
        testee.onQueryUpdated("query");
        resultsSource.push(
                success(singletonList(REPOSITORY_IMAGE_A))
        );

        // Then
        verify(view).updateState(new State.LoadedResults(
                singletonList(new Item.Image(
                        REPOSITORY_IMAGE_A.url
                )),
                true
        ));
    }

    @Test
    public void loadingNextPage() throws Exception {
        // Given
        given(interactor.loadingNextPage())
                .willReturn(Pipe.constant(true));

        testee.start(view);

        // When
        testee.onQueryUpdated("query");
        resultsSource.push(
                success(singletonList(REPOSITORY_IMAGE_A))
        );

        // Then
        verify(view).updateState(new State.LoadedResults(
                asList(
                        new Item.Image(
                                REPOSITORY_IMAGE_A.url
                        ),
                        Item.Loading.INSTANCE
                ),
                false
        ));
    }

    @Test
    public void unsubscribeOnStop() throws Exception {
        // Given
        testee.start(view);

        // When
        testee.stop();

        resultsSource.push(
                success(singletonList(REPOSITORY_IMAGE_A))
        );

        // Then
        verify(view).updateState(State.Default.INSTANCE);
    }

}