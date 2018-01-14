package com.example.images.domain;

import android.support.annotation.NonNull;

import com.example.images.data.ImagesRepository;
import com.example.images.data.Paginated;
import com.example.images.data.Result;
import com.example.pipe.Pipe;
import com.example.pipe.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.images.data.ImagesRepository.Image;
import static java.util.Collections.emptyList;

/**
 * Business logic of image search.
 * <p>
 * Should be used only from a single thread.
 */
public class ImageSearchInteractor {

    private final ImagesRepository repository;

    private final Source<Boolean> morePagesAvailable = new Source<>(false);
    private final Source<Boolean> loadingResults = new Source<>(false);
    private final Source<Boolean> loadingNextPage = new Source<>(false);
    private final Source<Result<List<Image>>> searchResults = new Source<>(
            Result.success(emptyList())
    );

    private String currentQuery;
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final List<Image> currentResult = new ArrayList<>();

    private final Set<Pipe.Subscription> activeSubscriptions = new HashSet<>();

    public ImageSearchInteractor(ImagesRepository repository) {
        this.repository = repository;
    }

    /**
     * Searches for images which match given query.
     * <p>
     * Results are being emitted in {@link #searchResults()}.
     */
    public void search(@NonNull String query) {
        stopActiveSubscriptions();

        currentQuery = query;
        loadingResults.push(true);

        Pipe<Result<Paginated<List<Image>>>>.Subscription subscription = repository.queryImages(query, 1)
                .subscribe(it -> {
                    loadingResults.push(false);

                    if (it.isSuccess()) {
                        synchronized (currentResult) {
                            currentResult.clear();
                            currentResult.addAll(it.value.value);
                        }

                        searchResults.push(
                                Result.success(it.value.value)
                        );

                        morePagesAvailable.push(
                                it.value.currentPage < it.value.totalPages
                        );
                    } else {
                        searchResults.push(
                                Result.error(it.throwable)
                        );

                        morePagesAvailable.push(false);
                    }
                });

        activeSubscriptions.add(subscription);
    }

    /**
     * Requests new page with results if they are available.
     */
    public void requestNextPage() {
        verifyQueryIsNotEmpty();

        loadingNextPage.push(true);
        Pipe<Result<Paginated<List<Image>>>>.Subscription subscription = repository.queryImages(currentQuery, currentPage.get() + 1)
                .subscribe(it -> {
                    loadingNextPage.push(false);

                    if (it.isSuccess()) {
                        currentPage.incrementAndGet();

                        synchronized (currentResult) {
                            currentResult.addAll(it.value.value);

                            searchResults.push(Result.success(
                                    new ArrayList<>(currentResult)
                            ));

                            morePagesAvailable.push(
                                    it.value.currentPage < it.value.totalPages
                            );
                        }
                    }
                });

        activeSubscriptions.add(subscription);
    }

    private void stopActiveSubscriptions() {
        for (Pipe.Subscription subscription : activeSubscriptions) {
            subscription.unsubscribe();
        }
        activeSubscriptions.clear();
    }

    private void verifyQueryIsNotEmpty() {
        if (currentQuery == null || currentQuery.isEmpty()) {
            throw new IllegalStateException("There is no query");
        }
    }

    /**
     * @return pipe which emits search results for current query. When new page is requested, pipe
     * emits a list with new values being appended at the end.
     */
    public Pipe<Result<List<ImagesRepository.Image>>> searchResults() {
        return Pipe.fromSource(searchResults);
    }

    /**
     * @return pipe which emits {@code true} when more results are available. Emits {@code false} if
     * there are no more results available.
     */
    public Pipe<Boolean> morePagesAvailable() {
        return Pipe.fromSource(morePagesAvailable);
    }

    /**
     * @return pipe which emits {@code true} if results are being loaded. Emits {@code false} if
     * nothing is being loaded at the moment.
     */
    public Pipe<Boolean> loadingResults() {
        return Pipe.fromSource(loadingResults);
    }

    /**
     * @return pipe which emits {@code true} if next page is being loaded. Emits {@code false} if
     * nothing is being loaded at the moment.
     */
    public Pipe<Boolean> loadingNextPage() {
        return Pipe.fromSource(loadingNextPage);
    }

}
