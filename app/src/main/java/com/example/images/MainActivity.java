package com.example.images;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.images.features.search.data.repository.FlickrImagesRepository;
import com.example.images.features.search.domain.ImageSearchInteractor;
import com.example.images.features.search.ui.ImageSearchPresenter;
import com.example.images.features.search.ui.ImageSearchView;
import com.example.images.features.search.ui.android.ImagesAdapter;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ImageSearchView {

    private ImageSearchPresenter presenter;
    private ImagesAdapter adapter;

    @NonNull
    private Listener listener = Listener.NULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initQueryView();
        initRecyclerView();

        presenter = new ImageSearchPresenter(
                new ImageSearchInteractor(
                        new FlickrImagesRepository(
                                Executors.newSingleThreadExecutor()
                        )
                )
        );
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
        Log.d("^^^", state.toString());

        if (state instanceof State.LoadedResults) {
            adapter.setItems(
                    ((State.LoadedResults) state).items
            );
        }
    }

}
