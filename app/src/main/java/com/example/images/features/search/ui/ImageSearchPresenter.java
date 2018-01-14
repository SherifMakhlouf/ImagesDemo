package com.example.images.features.search.ui;

import android.support.annotation.NonNull;

import com.example.images.features.search.data.ImagesRepository;
import com.example.images.features.search.data.Result;
import com.example.images.features.search.domain.ImageSearchInteractor;
import com.example.pipe.Pipe;
import com.example.pipe.Pipes;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.images.features.search.ui.ImageSearchView.Item;
import static com.example.images.features.search.ui.ImageSearchView.Listener;
import static com.example.images.features.search.ui.ImageSearchView.State;
import static com.example.images.util.FunctionalUtils.map;

/**
 * Presenter for {@link ImageSearchView}.
 */
public class ImageSearchPresenter implements Listener {

    private final ImageSearchInteractor interactor;
    private final AtomicReference<String> currentQuery = new AtomicReference<>("");

    private Pipe<State>.Subscription dataSubscription;
    private Pipe<Boolean>.Subscription loadingSubscription;

    public ImageSearchPresenter(ImageSearchInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Starts presenting the data to the given view.
     */
    public void start(ImageSearchView view) {
        view.setListener(this);

        loadingSubscription = interactor.loadingResults()
                .subscribe(loading -> {
                    if (loading) {
                        view.updateState(State.Loading.INSTANCE);
                    }
                });

        dataSubscription = Pipes
                .combine(
                        interactor.searchResults(),
                        interactor.loadingNextPage(),
                        interactor.morePagesAvailable(),
                        this::buildState
                )
                .subscribe(view::updateState);
    }

    private State buildState(Result<List<ImagesRepository.Image>> result, Boolean loadingNextPage, Boolean morePagesAvailable) {
        if (result.isSuccess()) {
            return buildSuccessState(result, loadingNextPage, morePagesAvailable);
        } else {
            return State.Failure.INSTANCE;
        }
    }

    private State buildSuccessState(Result<List<ImagesRepository.Image>> result, Boolean loadingNextPage, Boolean morePagesAvailable) {
        String query = currentQuery.get();

        if (query.isEmpty()) {
            return State.Default.INSTANCE;
        } else {
            if (result.value.isEmpty()) {
                return State.NoResults.INSTANCE;
            } else {
                return buildLoadedState(result, loadingNextPage, morePagesAvailable);
            }
        }
    }

    @NonNull
    private State buildLoadedState(Result<List<ImagesRepository.Image>> result, Boolean loadingNextPage, Boolean morePagesAvailable) {
        List<Item> items = map(
                result.value,
                it -> new Item.Image(it.url)
        );

        if (loadingNextPage) {
            items.add(Item.Loading.INSTANCE);
        }

        return new State.LoadedResults(
                items,
                morePagesAvailable
        );
    }

    /**
     * Stops presenting the data. Clears the reference to the view.
     */
    public void stop() {
        dataSubscription.unsubscribe();
        loadingSubscription.unsubscribe();
    }

    @Override
    public void requestMoreResults() {
        interactor.requestNextPage();
    }

    @Override
    public void onQueryUpdated(@NonNull String query) {
        currentQuery.set(query);

        interactor.search(query);
    }

}
