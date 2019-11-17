package com.wfm.soundcollaborations.webservice;

import com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp;
import com.wfm.soundcollaborations.webservice.dtos.CompositionRequest;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.CompositionUpdateRequest;
import com.wfm.soundcollaborations.webservice.dtos.OverviewResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface CompositionService {

  @Headers({"Content-Type: application/json"})
  @GET("compositions/compositionsOverview?page=0&size=5")
  Call<OverviewResponse> getOverviews();

  @Headers({"Content-Type: application/json"})
  @POST("compositions")
  Call<CompositionOverviewResp> createComposition(@Body CompositionRequest composition);

  @Headers({"Content-Type: application/json"})
  @PUT("compositions/{id}?updateRequest=PICK")
  Call<CompositionResponse> pickComposition(@Path("id") Long id,
                                            @Body CompositionUpdateRequest compositionUpdateRequest);

  @Headers({"Content-Type: application/json"})
  @PUT("compositions/{id}?updateRequest=RELEASE")
  Call<CompositionResponse> releaseComposition(@Path("id") Long id,
                                              @Body CompositionUpdateRequest compositionUpdateRequest);

}