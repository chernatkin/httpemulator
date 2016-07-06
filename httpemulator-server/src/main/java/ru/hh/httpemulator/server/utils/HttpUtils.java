package ru.hh.httpemulator.server.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import ru.hh.httpemulator.client.entity.AttributeType;
import ru.hh.httpemulator.client.entity.HttpEntry;

public final class HttpUtils {

  private static final AtomicLong REQUEST_COUNTER = new AtomicLong(0);

  public static final String REQUEST_ID_PARAM_NAME = "REQUEST_ID";

  private HttpUtils() {
  }

  public static long nextRequestId() {
    return REQUEST_COUNTER.incrementAndGet();
  }

  public static Collection<HttpEntry> convertToHttpEntries(HttpServletRequest request) {
    final Collection<HttpEntry> entries = new ArrayList<>();

    entries.add(new HttpEntry(AttributeType.PATH, null, request.getPathInfo()));

    for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
      final String headerName = headerNames.nextElement();

      for (Enumeration<String> headerValues = request.getHeaders(headerName); headerValues.hasMoreElements();) {
        entries.add(new HttpEntry(AttributeType.HEADER, headerName, headerValues.nextElement()));
      }
    }

    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        entries.add(new HttpEntry(AttributeType.COOKIE, cookie.getName(), cookie.getValue()));
      }
    }

    entries.add(new HttpEntry(AttributeType.METHOD, null, request.getMethod()));

    request.getParameterMap().entrySet().stream()
        .flatMap(parameter -> Arrays.stream(parameter.getValue()).map(value -> new HttpEntry(AttributeType.PARAMETER, parameter.getKey(), value)))
        .forEach(entries::add);

    entries.add(new HttpEntry(AttributeType.PROTOCOL, null, request.getProtocol()));

    return entries;
  }
}
