package org.sonar.jvm.squad.wallboard.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class RestUtils {

  private RestUtils() {
    // utility class
  }

  public static HttpEntity<Void> headersForGET() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return new HttpEntity<>(headers);
  }

  public static HttpEntity<Void> headersForGET(String bearerToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setBearerAuth(bearerToken);
    return new HttpEntity<>(headers);
  }

  public static HttpEntity<Void> headersForGET(Map<String, String> properties) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setAll(properties);
    return new HttpEntity<>(headers);
  }

  public static <T> HttpEntity<T> entityForPOST(T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return new HttpEntity<>(body, headers);
  }

  public static <T> HttpEntity<T> entityWithProperties(T body, Map<String, String> properties) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setAll(properties);
    return new HttpEntity<>(body, headers);
  }

}
