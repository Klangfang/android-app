package com.wfm.soundcollaborations.webservice;

import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CompositionService {

  @Multipart
  @POST("/compositions")
  Call<CompositionOverview> createComposition(@Part MultipartBody.Part composition, @Part MultipartBody.Part[] files);

}