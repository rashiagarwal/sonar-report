package org.sonarqube;

import org.sonarqube.controller.IssueController;
import org.sonarqube.controller.TagController;
import org.sonarqube.model.Issue;
import org.sonarqube.utility.JsonUtil;
import org.sonarqube.utility.Sonar;
import retrofit2.Retrofit;
import spark.Request;

import java.io.IOException;
import java.util.*;

import static org.sonarqube.utility.JsonUtil.json;
import static spark.Spark.*;

public class Main {

  private static final Set<Issue> issues = new HashSet<>();

  public static void main(String[] args) throws IOException {
    port(getHerokuAssignedPort());
    post("/issues", (request, res) -> getResponse(request), json());
    get("/hello", (request, res) -> "Hello World");

//
//    if (!issues.isEmpty()) {
//      Excel excel = new Excel();
//      excel.write(issues);
//    }
  }

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
  }

  private static org.sonarqube.model.Response getResponse(Request request) throws IOException {
    org.sonarqube.model.Request request1 = JsonUtil.deSerialize(request.body(), org.sonarqube.model.Request.class);
    Retrofit connection = Sonar.createConnection(request1.getUrl());
    if (connection == null) {
      return null;
    }

    Set<String> tags = getTags(connection, request1.getKey());

    fetchIssues(connection, tags, request1.getKey());
    if (!issues.isEmpty()) {
      return new org.sonarqube.model.Response("success", issues);
//      return issues;
    }
    return null;
  }

  private static Set<String> getTags(Retrofit connection, String key) throws IOException {
    TagController controller = new TagController(connection, key);
    return controller.getTags();
  }

  private static void fetchIssues(Retrofit connection, Set<String> tags, String key) {
    IssueController issueController = new IssueController(connection, key);
    issueController.fetchIssues(issues, tags);
  }
}