package org.sonarqube.controller;

import org.sonarqube.model.Issue;
import org.sonarqube.model.IssueResource;
import org.sonarqube.model.Paging;
import org.sonarqube.model.Severity;
import org.sonarqube.model.Type;
import org.sonarqube.service.IssueService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

public class IssueController {

  private IssueService service;

  public IssueController(Retrofit retrofit) {
    this.service = retrofit.create(IssueService.class);
  }

  public void fetchIssues(Set<Issue> issues, Set<String> tags) {
    List<Severity> severities = getSeverities();

    severities.parallelStream().forEach(
      severity -> fetchAndAddIssuesBySeverity(issues, tags, severity));
  }

  private void fetchAndAddIssuesBySeverity(Set<Issue> issues, Set<String> tags, Severity severity) {
    List<Type> types = getTypes();

    types.parallelStream().forEach(type -> fetchAndAddIssuesByType(issues, tags, severity, type));
  }

  private List<Type> getTypes() {
    return Stream.of(Type.values()).collect(Collectors.toList());
  }

  private void fetchAndAddIssuesByType(Set<Issue> issues, Set<String> tags, Severity severity, Type type) {
    tags.parallelStream().forEach(tag -> fetchAndAddIssuesByTag(issues, severity, type, tag));
  }

  private void fetchAndAddIssuesByTag(Set<Issue> issues, Severity severity,
    Type type, String tag) {
    try {
      out.println("Severity = " + severity.name() + "; Type = " + type.name() + "; Tag = " + tag);
      IssueResource issue = getIssue(severity.name(), type.name(), tag, 1, 500);
      if (issue != null && !issue.getIssues().isEmpty()) {
        int maxPageIndex = getMaxPageIndex(issue);
        for (int pageIndex = 1; pageIndex <= maxPageIndex; pageIndex++) {
          getAndAddIssues(issues, severity, type, tag, pageIndex);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private int getMaxPageIndex(IssueResource issue) {
    Paging paging = issue.getPaging();
    int totalNumberOfPages = (int) Math.ceil((double) paging.getTotal() / paging.getPageSize());
    int maxPageIndex;
    maxPageIndex = totalNumberOfPages > 20 ? 20 : totalNumberOfPages;
    return maxPageIndex;
  }

  private void getAndAddIssues(Set<Issue> issues, Severity severity, Type type,
    String tag, int pageIndex) throws IOException {
    IssueResource issue;
    issue = getIssue(severity.name(), type.name(), tag, pageIndex, 500);
    if (issue != null && !issue.getIssues().isEmpty()) {
      issues.addAll(issue.getIssues());
    }
  }

  private IssueResource getIssue(String severity, String type, String tag, int pageIndex, int pageSize) throws
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
//    queryMap.put("componentRoots", "com.usfoods.prime:PrimeAPP");
    queryMap.put("resolved", String.valueOf(false));
    queryMap.put("severities", severity);
    queryMap.put("types", type);
    queryMap.put("tags", tag);
    queryMap.put("pageIndex", String.valueOf(pageIndex));
    queryMap.put("pageSize", String.valueOf(pageSize));

    Call<IssueResource> call = service.listIssues(queryMap);

    return call.execute();
  }

  private static List<Severity> getSeverities() {
    return Stream.of(Severity.values()).collect(toList());
  }
}