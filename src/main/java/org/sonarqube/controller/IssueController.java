package org.sonarqube.controller;

import org.sonarqube.model.IssueResource;
import org.sonarqube.service.IssueService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueController {

  private IssueService service;

  public IssueController(Retrofit retrofit) {
    this.service = retrofit.create(IssueService.class);
  }

  public IssueResource getIssue(String severity, String type, String tag, int pageIndex, int pageSize) throws
      IOException {
    Response<IssueResource> response = execute(severity, type, tag, pageIndex, pageSize);
    if (response.isSuccessful()) {
      return response.body();
    }
    return null;
  }

  private Response<IssueResource> execute(String severity, String type, String tag, int pageIndex, int pageSize) throws
      IOException {

    Map<String, String> queryMap = new HashMap<>();
    queryMap.put("componentRoots", "com.usfoods.prime:PrimeAPP");
    queryMap.put("resolved", String.valueOf(false));
    queryMap.put("severities", severity);
    queryMap.put("types", type);
    queryMap.put("tags", tag);
    queryMap.put("pageIndex", String.valueOf(pageIndex));
    queryMap.put("pageSize", String.valueOf(pageSize));

    Call<IssueResource> call = service.listIssues(queryMap);

    return call.execute();
  }
}
