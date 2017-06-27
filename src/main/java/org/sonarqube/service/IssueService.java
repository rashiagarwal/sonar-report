package org.sonarqube.service;

import org.sonarqube.model.IssueResource;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface IssueService {

  @GET("api/issues/search")
  Call<IssueResource> listIssues(@QueryMap Map<String, String> options);
}
