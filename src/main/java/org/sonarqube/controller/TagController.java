package org.sonarqube.controller;

import org.sonarqube.model.TagResource;
import org.sonarqube.service.TagService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class TagController {
  private TagService service;

  public TagController(Retrofit retrofit) {
    this.service = retrofit.create(TagService.class);
  }

  public TagResource getResource() throws IOException {
    Response<TagResource> response = execute();
    if (response.isSuccessful()) {
      return response.body();
    }
    return null;
  }

  private Response<TagResource> execute() throws IOException {
    Call<TagResource> call = service.listTags("com.usfoods.prime:PrimeAPP");
    return call.execute();
  }
}