package com.example.images;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.images.features.search.data.repository.FlickrImagesRepository;
import com.example.images.features.search.domain.ImageSearchInteractor;
import com.example.images.features.search.ui.ImageSearchPresenter;
import com.example.images.features.search.ui.ImageSearchView;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ImageSearchView {

    private ImageSearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new ImageSearchPresenter(
                new ImageSearchInteractor(
                        new FlickrImagesRepository(
                                Executors.newSingleThreadExecutor()
                        )
                )
        );
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
        listener.onQueryUpdated("kittens");
    }

    @Override
    public void updateState(@NonNull State state) {
        Log.d("^^^", state.toString());
    }

}
