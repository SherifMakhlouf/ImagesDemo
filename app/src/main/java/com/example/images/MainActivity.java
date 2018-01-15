package com.example.images;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.images.di.DependencyResolver;
import com.example.images.features.search.ui.ImageSearchPresenter;
import com.example.images.features.search.ui.ImageSearchView;
import com.example.images.features.search.ui.android.ImagesAdapter;

import java.util.Collections;

import static java.util.Collections.*;

public class MainActivity extends AppCompatActivity implements ImageSearchView {

    private ImageSearchPresenter presenter;
    private ImagesAdapter adapter;

    @NonNull
    private Listener listener = Listener.NULL;

    private RecyclerView recyclerView;
    private View progressBar;
    private View noResults;
    private View defaultMessage;

    private boolean canRequestMoreImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStateViews();
        initQueryView();
        initRecyclerView();

        presenter = new DependencyResolver().provideImageSearchPresenter();
    }

    private void initStateViews() {
        progressBar = findViewById(R.id.progressBar);
        noResults = findViewById(R.id.noResults);
        defaultMessage = findViewById(R.id.defaultMessage);
    }

    private void initQueryView() {
        EditText queryView = findViewById(R.id.searchQuery);
        queryView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listener.onQueryUpdated(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });
    }

    private void initRecyclerView() {
        adapter = new ImagesAdapter(LayoutInflater.from(this));

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (shouldLoadMoreImages(newState, layoutManager)) {
                    listener.requestMoreResults();
                }
            }
        });
    }

    private boolean shouldLoadMoreImages(int newState, GridLayoutManager layoutManager) {
        return newState == RecyclerView.SCROLL_STATE_IDLE
                && canRequestMoreImages
                && layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.stop();
    }

    @Override
    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
    }

    @Override
    public void updateState(@NonNull State state) {
        runOnUiThread(() -> updateStateOnMainThread(state));
    }

    private void updateStateOnMainThread(State state) {
        updateVisibility(state);
        updateAdapterItems(state);
    }

    private void updateAdapterItems(State state) {
        if (state instanceof State.LoadedResults) {
            State.LoadedResults loadedResults = (State.LoadedResults) state;

            adapter.setItems(
                    loadedResults.items
            );
            canRequestMoreImages = loadedResults.morePagesAvailable;
        } else {
            adapter.setItems(
                    emptyList()
            );
            canRequestMoreImages = false;
        }
    }

    private void updateVisibility(State state) {
        recyclerView.setVisibility(
                visibility(state instanceof State.LoadedResults)
        );

        progressBar.setVisibility(
                visibility(state instanceof State.Loading)
        );

        defaultMessage.setVisibility(
                visibility(state instanceof State.Default)
        );

        noResults.setVisibility(
                visibility(state instanceof State.NoResults)
        );
    }

    private static int visibility(boolean visible) {
        return visible
                ? View.VISIBLE
                : View.GONE;
    }

}
