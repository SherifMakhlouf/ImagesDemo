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

        activeSubscriptions.add(
                repository.queryImages(query, 1)
                        .subscribe(this::onQueryResult)
        );
    }

    private void onQueryResult(Result<Paginated<List<Image>>> result) {
        loadingResults.push(false);

        if (result.isSuccess()) {
            onQuerySuccess(result);
        } else {
            onQueryFailure(result);
        }
    }

    private void onQuerySuccess(Result<Paginated<List<Image>>> result) {
        replaceCurrentResult(result);

        searchResults.push(
                Result.success(result.value.value)
        );

        updateMorePagesAvailability(result);
    }

    private void replaceCurrentResult(Result<Paginated<List<Image>>> result) {
        synchronized (currentResult) {
            currentResult.clear();
            currentResult.addAll(result.value.value);
        }
    }

    private void onQueryFailure(Result<Paginated<List<Image>>> result) {
        searchResults.push(
                Result.error(result.throwable)
        );

        morePagesAvailable.push(false);
    }

    /**
     * Requests new page with results if they are available.
     */
    public void requestNextPage() {
        verifyQueryIsNotEmpty();

        loadingNextPage.push(true);
        activeSubscriptions.add(
                repository.queryImages(currentQuery, currentPage.get() + 1)
                        .subscribe(this::onNextPageResult)
        );
    }

    private void onNextPageResult(Result<Paginated<List<Image>>> result) {
        loadingNextPage.push(false);

        if (result.isSuccess()) {
            onNextPageSuccess(result);
        }
    }

    private void onNextPageSuccess(Result<Paginated<List<Image>>> result) {
        currentPage.incrementAndGet();

        synchronized (currentResult) {
            currentResult.addAll(result.value.value);

            searchResults.push(Result.success(
                    new ArrayList<>(currentResult)
            ));
        }

        updateMorePagesAvailability(result);
    }

    private void updateMorePagesAvailability(Result<Paginated<List<Image>>> result) {
        morePagesAvailable.push(
                canLoadMorePages(result)
        );
    }

    private boolean canLoadMorePages(Result<Paginated<List<Image>>> result) {
        return result.value.currentPage < result.value.totalPages;
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
