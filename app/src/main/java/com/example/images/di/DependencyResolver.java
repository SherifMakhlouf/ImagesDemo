package com.example.images.di;

import android.support.annotation.NonNull;

import com.example.images.features.search.data.repository.ImagesRepository;
import com.example.images.features.search.data.repository.flickr.FlickrImagesRepository;
import com.example.images.features.search.data.repository.flickr.ResponseDeserializer;
import com.example.images.features.search.domain.ImageSearchInteractor;
import com.example.images.features.search.ui.ImageSearchPresenter;
import com.example.images.util.concurrent.ThrottlingExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Encapsulates dependency resolution allowing us to do IoC.
 */
public class DependencyResolver {

    private static final long REQUEST_THROTTLE_MS = 200L;

    public ImageSearchPresenter provideImageSearchPresenter() {
        return new ImageSearchPresenter(
                provideImageSearchInteractor()
        );
    }

    @NonNull
    private ImageSearchInteractor provideImageSearchInteractor() {
        return new ImageSearchInteractor(
                provideImagesRepository()
        );
    }

    @NonNull
    private ImagesRepository provideImagesRepository() {
        return new FlickrImagesRepository(
                provideRequestExecutor(),
                new ResponseDeserializer()
        );
    }

    @NonNull
    private Executor provideRequestExecutor() {
        return ThrottlingExecutor.fromExecutor(
                Executors.newSingleThreadExecutor(),
                REQUEST_THROTTLE_MS
        );
    }

}
