package org.sonarqube;

import org.sonarqube.controller.IssueController;
import org.sonarqube.controller.TagController;
import org.sonarqube.model.Issue;
import org.sonarqube.utility.Excel;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.sonarqube.utility.Sonar.createConnection;

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

  private static Set<String> getTags(Retrofit connection) throws IOException {
    TagController controller = new TagController(connection);
    return controller.getTags();
  }

  private static void fetchIssues(Retrofit connection, Set<String> tags) {
    IssueController issueController = new IssueController(connection);
    issueController.fetchIssues(issues, tags);
  }
}