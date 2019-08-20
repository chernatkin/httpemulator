package ru.hh.httpemulator.server.scenario;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import ru.hh.httpemulator.client.entity.HttpEntry;

public interface Scenario {

  Collection<HttpEntry> execute(HttpServletRequest request, Response.ResponseBuilder response, Collection<HttpEntry> otherEntries);

}
