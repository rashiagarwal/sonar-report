package org.sonarqube.service;

import org.sonarqube.model.TagResource;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TagService {

  @GET("api/rules/tags")
  Call<TagResource> listTags(@Query("componentRoots") String componentRoots);
}
