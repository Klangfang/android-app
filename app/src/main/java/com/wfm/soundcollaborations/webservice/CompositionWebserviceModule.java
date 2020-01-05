package com.wfm.soundcollaborations.webservice;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class CompositionWebserviceModule {

    private final static long HTTP_TIMEOUT = 10_000;


    @Provides
    public CompositionRetrofitService provideCompositionWebservice() {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .callTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        return new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://klangfang-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompositionRetrofitService.class);

    }
}
