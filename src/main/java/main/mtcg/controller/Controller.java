package main.mtcg.controller;

import main.server.http.Request;
import main.server.http.Response;

public interface Controller {

    boolean supports(String route);

    Response handle(Request request);
}
