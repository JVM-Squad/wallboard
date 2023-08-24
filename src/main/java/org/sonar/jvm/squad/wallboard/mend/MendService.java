package org.sonar.jvm.squad.wallboard.mend;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.entityForPOST;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.headersForGET;

@Service
@Lazy
public class MendService {

  public final MendConfig.Credentials credentials;
  public final RestTemplate rest;

  public MendService(MendConfig.Credentials credentials, RestTemplate rest) {
    this.credentials =credentials;
    this.rest = rest;
  }

  interface Login {
    record Request(String email, String orgToken, String userKey) {
    }
    record Response(RetVal retVal) {
    }
    record RetVal(String userUuid, String userName, String email, String jwtToken, String orgName, String orgUuid) {
    }
  }

  public Login.Response login() {
    Login.Request requestData = new Login.Request(credentials.userEmail(), credentials.organizationApiKey(), credentials.userKey());
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/login",
      HttpMethod.POST, entityForPOST(requestData), String.class);
    return responseAs(response, Login.Response.class);
  }

  interface AlertTypesSummary {
    record Response(RetVal retVal) {
    }
    record RetVal(Policies policies) {
    }
    record Policies(Long violations) {
    }
  }

  public AlertTypesSummary.Response getOrganizationAlertTypesSummary(Login.Response login) {
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/orgs/{orgToken}/summary/alertTypes",
      HttpMethod.GET,
      headersForGET(login.retVal.jwtToken),
      String.class,
      Map.of("orgToken", login.retVal.orgUuid));
    return responseAs(response, AlertTypesSummary.Response.class);
  }

  interface LibrarySummary {
    record Response(AdditionalData additionalData, List<RetVal> retVal) {
    }
    record AdditionalData(Long totalItems, Boolean isLastPage) {
    }
    record RetVal(String productName, String projectName, Long vulnerableLibraries) {
      public String shortProjectName() {
        return projectName.replaceFirst("^SonarSource/", "");
      }
    }
  }

  public LibrarySummary.Response getOrganizationVulnerableLibrarySummary(Login.Response login) {
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/orgs/{orgToken}/summary/projects/vulnerableLibraryCount?pageSize=10000&search=productName:regex:.*",
      HttpMethod.GET,
      headersForGET(login.retVal.jwtToken),
      String.class,
      Map.of("orgToken", login.retVal.orgUuid));
    return responseAs(response, LibrarySummary.Response.class);
  }

  public static HttpHeaders headers() {
    return headers(null);
  }

  public static HttpHeaders headers(@Nullable String bearerToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (bearerToken != null) {
      headers.setBearerAuth(bearerToken);
    }
    return headers;
  }

}
