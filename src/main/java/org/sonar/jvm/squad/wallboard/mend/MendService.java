package org.sonar.jvm.squad.wallboard.mend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.sonar.jvm.squad.wallboard.client.JsonUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Lazy
public class MendService {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public final MendConfig.Credentials credentials;
  public final RestTemplate rest;

  public MendService(MendConfig.Credentials credentials, RestTemplate rest) {
    this.credentials =credentials;
    this.rest = rest;
  }

  interface Login {
    record Request(String email, String orgToken, String userKey) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(RetVal retVal) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record RetVal(String userUuid, String userName, String email, String jwtToken, String orgName, String orgUuid) {
    }
  }

  public Login.Response login() {
    Login.Request requestData = new Login.Request(credentials.userEmail(), credentials.organizationApiKey(), credentials.userKey());
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/login",
      HttpMethod.POST,
      new HttpEntity<>(requestData, headers()),
      String.class);
    return responseAs(response, Login.Response.class);
  }

  interface AlertTypesSummary {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(RetVal retVal) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record RetVal(Policies policies) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Policies(Long violations) {
    }
  }

  public AlertTypesSummary.Response getOrganizationAlertTypesSummary(Login.Response login) {
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/orgs/{orgToken}/summary/alertTypes",
      HttpMethod.GET,
      new HttpEntity<>(headers(login.retVal.jwtToken)),
      String.class,
      Map.of("orgToken", login.retVal.orgUuid));
    return responseAs(response, AlertTypesSummary.Response.class);
  }

  interface LibrarySummary {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(AdditionalData additionalData, List<RetVal> retVal) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record AdditionalData(Long totalItems, Boolean isLastPage) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record RetVal(String productName, String projectName, Long vulnerableLibraries) {
    }
  }

  public LibrarySummary.Response getOrganizationVulnerableLibrarySummary(Login.Response login) {
    ResponseEntity<String> response = rest.exchange(
      credentials.apiBaseUrlV2() + "/api/v2.0/orgs/{orgToken}/summary/projects/vulnerableLibraryCount?pageSize=10000&search=productName:regex:.*",
      HttpMethod.GET,
      new HttpEntity<>(headers(login.retVal.jwtToken)),
      String.class,
      Map.of("orgToken", login.retVal.orgUuid));
    return responseAs(response, LibrarySummary.Response.class);
  }

  public static <T> T responseAs(ResponseEntity<String> response, Class<T> valueType) {
    if (response.getStatusCode().is2xxSuccessful()) {
      try {
        return MAPPER.readValue(response.getBody(), valueType);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(
          "JsonProcessingException for " + valueType.getName() + " exception " + e.getMessage() + " from " + JsonUtils.prettyPrint(response.getBody()));
      }
    }
    throw new IllegalStateException("Invalid status code " + response.getStatusCode() + ": " + JsonUtils.prettyPrint(response.getBody()));
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
