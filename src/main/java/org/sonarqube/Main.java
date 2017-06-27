package org.sonarqube;

import org.sonarqube.controller.IssueController;
import org.sonarqube.controller.TagController;
import org.sonarqube.model.Issue;
import org.sonarqube.model.IssueResource;
import org.sonarqube.model.Severity;
import org.sonarqube.model.TagResource;
import org.sonarqube.model.Type;
import org.sonarqube.utility.ExcelWriter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Main {

  public static void main(String[] args) throws IOException {

    Retrofit retrofit = Builder.build();

    final Set<org.sonarqube.model.Issue> issues = new HashSet<>();
    TagController controller = new TagController(retrofit);
    TagResource resource = controller.getResource();
    Set<String> tags = resource.getTags();
    IssueController issueController = new IssueController(retrofit);
    Arrays.stream(Severity.values()).parallel().forEach(
        getIssuesBySeverity(issues, tags, issueController)
    );

    new ExcelWriter(issues);
  }

  private static Consumer<Severity> getIssuesBySeverity(Set<Issue> issues, Set<String> tags,
      IssueController issueController) {
    return severity -> Arrays.stream(Type.values()).forEach(
        getIssuesByType(issues, tags, issueController, severity)
    );
  }

  private static Consumer<Type> getIssuesByType(Set<Issue> issues, Set<String> tags, IssueController issueController,
      Severity severity) {
    return type -> tags.forEach(getIssuesByTag(issues, issueController, severity, type));
  }

  private static Consumer<String> getIssuesByTag(Set<Issue> issues, IssueController issueController, Severity severity,
      Type type) {
    return (String tag) -> {
      try {
        IssueResource issue = issueController.getIssue(severity.name(), type.name(), tag, 1, 500);
        if (issue != null && !issue.getIssues().isEmpty()) {
          org.sonarqube.model.Paging paging = issue.getPaging();
          int totalNumberOfPages = (int) Math.ceil((double) paging.getTotal() / paging.getPageSize());
          int maxPageIndex;
          maxPageIndex = totalNumberOfPages > 20 ? 20 : totalNumberOfPages;

          for (int pageIndex = 1; pageIndex <= maxPageIndex; pageIndex++) {
            getAndAddIssues(issues, issueController, severity, type, tag, pageIndex);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }

  private static void getAndAddIssues(Set<Issue> issues, IssueController issueController, Severity severity, Type type,
      String tag, int pageIndex) throws IOException {
    IssueResource issue;
    issue = issueController.getIssue(severity.name(), type.name(), tag, pageIndex, 500);
    if (issue != null && !issue.getIssues().isEmpty()) {
      issues.addAll(issue.getIssues());
    }
  }
}