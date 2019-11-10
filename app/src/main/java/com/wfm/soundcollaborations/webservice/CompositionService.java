package com.wfm.soundcollaborations.webservice;

import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface CompositionService {

  @Headers({"Content-Type: application/json"})
  @POST("compositions")
  Call<CompositionOverviewResp> createComposition(@Body CompositionRequest composition);

  @Headers({"Content-Type: application/json"})
  @PUT("compositions/{id}")
  Call<CompositionResponse> updateComposition(@Path("id") Long id, @Body CompositionUpdateRequest compositionUpdateRequest);

}