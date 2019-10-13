package com.wfm.soundcollaborations.webservice;

import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CompositionService {

  @Multipart
  @POST("/compositions")
  Call<CompositionOverviewResp> createComposition(@Part MultipartBody.Part composition, @Part MultipartBody.Part[] files);

  @Multipart
  @PUT("/compositions/{id}/release")
  Call<CompositionOverviewResp> updateComposition(@Path("id") long id, @Part MultipartBody.Part sounds, @Part MultipartBody.Part[] files);

}