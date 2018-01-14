package com.example.images.ui;

import android.support.annotation.NonNull;

import com.example.images.domain.ImageSearchInteractor;
import com.example.pipe.Pipe;
import com.example.pipe.Pipes;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.images.ui.ImageSearchView.Item;
import static com.example.images.ui.ImageSearchView.Listener;
import static com.example.images.ui.ImageSearchView.State;
import static com.example.images.util.FunctionalUtils.map;

/**
 * Presenter for {@link ImageSearchView}.
 */
public class ImageSearchPresenter implements Listener {

    private final ImageSearchInteractor interactor;
    private final AtomicReference<String> currentQuery = new AtomicReference<>("");

    private Pipe<State>.Subscription subscription;

    public ImageSearchPresenter(ImageSearchInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Starts presenting the data to the given view.
     */
    public void start(ImageSearchView view) {
        view.setListener(this);

        interactor.loadingResults()
                .subscribe(loading -> {
                    if (loading) {
                        view.updateState(State.Loading.INSTANCE);
                    }
                });

        subscription = Pipes
                .combine(
                        interactor.searchResults(),
                        interactor.loadingNextPage(),
                        interactor.morePagesAvailable(),
                        (result, loadingNextPage, morePagesAvailable) -> {
                            if (result.isSuccess()) {

                                String query = currentQuery.get();

                                if (query.isEmpty()) {
                                    return State.Default.INSTANCE;
                                } else {
                                    if (result.value.isEmpty()) {
                                        return State.NoResults.INSTANCE;
                                    } else {
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
                                }
                            } else {
                                return State.Failure.INSTANCE;
                            }
                        }
                )
                .subscribe(view::updateState);
    }

    /**
     * Stops presenting the data. Clears the reference to the view.
     */
    public void stop() {
        subscription.unsubscribe();
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
