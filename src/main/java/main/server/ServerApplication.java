package main.server;

import main.server.http.Request;
import main.server.http.Response;

public interface ServerApplication {

    Response handle(Request request);
}
