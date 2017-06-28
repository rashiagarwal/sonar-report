package org.sonarqube;

import org.sonarqube.controller.IssueController;
import org.sonarqube.controller.TagController;
import org.sonarqube.model.Issue;
import org.sonarqube.model.IssueResource;
import org.sonarqube.model.Paging;
import org.sonarqube.model.Severity;
import org.sonarqube.model.Type;
import org.sonarqube.utility.Excel;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sonarqube.Sonar.createConnection;

public class Main {

  private static final Set<Issue> issues = new HashSet<>();

  public static void main(String[] args) throws IOException {

    Retrofit connection = createConnection();

    Set<String> tags = getTags(connection);

    fetchIssues(connection, tags);

    if (!issues.isEmpty()) {
      Excel excel = new Excel();
      excel.write(issues);
    }
  }

  private static void fetchIssues(Retrofit connection, Set<String> tags) throws IOException {
    IssueController issueController = new IssueController(connection);
    List<Severity> severities = Stream.of(Severity.values()).collect(Collectors.toList());
    severities.parallelStream().forEach(
      getIssuesBySeverity(issues, tags, issueController)
    );

  }

  private static Set<String> getTags(Retrofit connection) throws IOException {
    TagController controller = new TagController(connection);
    return controller.getTags();
  }

  private static Consumer<Severity> getIssuesBySeverity(Set<Issue> issues, Set<String> tags,
    IssueController issueController) {
    return severity -> {
      List<Type> types = Stream.of(Type.values()).collect(Collectors.toList());
      types.parallelStream().forEach(
        getIssuesByType(issues, tags, issueController, severity)
      );
    };
  }

  private static Consumer<Type> getIssuesByType(Set<Issue> issues, Set<String> tags, IssueController issueController,
    Severity severity) {
    return type -> tags.parallelStream().forEach(getIssuesByTag(issues, issueController, severity, type));
  }

  private static Consumer<String> getIssuesByTag(Set<Issue> issues, IssueController issueController, Severity severity,
    Type type) {
    return (String tag) -> {
      try {
        System.out.println("Severity = " + severity.name() + "; Type = " + type.name() + "; Tag = " + tag);
        IssueResource issue = issueController.getIssue(severity.name(), type.name(), tag, 1, 500);
        if (issue != null && !issue.getIssues().isEmpty()) {
          int maxPageIndex = getMaxPageIndex(issue);
          for (int pageIndex = 1; pageIndex <= maxPageIndex; pageIndex++) {
            getAndAddIssues(issues, issueController, severity, type, tag, pageIndex);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }

  private static int getMaxPageIndex(IssueResource issue) {
    Paging paging = issue.getPaging();
    int totalNumberOfPages = (int) Math.ceil((double) paging.getTotal() / paging.getPageSize());
    int maxPageIndex;
    maxPageIndex = totalNumberOfPages > 20 ? 20 : totalNumberOfPages;
    return maxPageIndex;
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