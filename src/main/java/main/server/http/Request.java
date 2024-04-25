package main.server.http;

public class Request {
    // GET, POST, PUT, DELETE
    private String method;
    // /, /home, /package
    private String route;
    private String host;
    // application/json, text/plain
    private String contentType;
    // 0, 17
    private int contentLength;
    // none, "{ "name": "foo" }"
    private String body;
    private String authorization;

    public Request(){

    }
    public Request(String method, String route, String host, String contentType, int contentLength, String body, String authorization) {
        this.method = method;
        this.route = route;
        this.host = host;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;
        this.authorization = authorization;
    }

    public Request(String post, String s, String s1, String s2) {
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(HttpMethod httpMethod) {
        this.method = httpMethod.getMethod();
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
