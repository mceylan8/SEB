package main.server.util;

import main.server.http.HttpMethod;
import main.server.http.Request;
import main.server.http.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpMapper {

    public static Request toRequestObject(String httpRequest) {

        Request request = new Request();

        // Set authorization if present and starts with "Bearer "
        String authHeader = getHttpHeader("Authorization", httpRequest);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            request.setAuthorization(authHeader.substring("Bearer ".length()));
        }

        // Set other request parameters
        request.setMethod(getHttpMethod(httpRequest));
        request.setRoute(getRoute(httpRequest));
        request.setHost(getHttpHeader("Host", httpRequest));
        request.setContentType(getHttpHeader("Content-Type", httpRequest));

        // Parse content length and body if present
        String contentLengthHeader = getHttpHeader("Content-Length", httpRequest);
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            request.setContentLength(contentLength);
            if (contentLength > 0) {
                int startIndex = httpRequest.length() - contentLength;
                request.setBody(httpRequest.substring(startIndex));
            }
        }

        return request;
    }

    public static String toResponseString(Response response) {

        return "HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n" +
                "Content-Type: " + response.getContentType() + "\r\n" +
                "Content-Length: " + response.getBody().length() + "\r\n" +
                "\r\n" +
                response.getBody();
    }

    // THOUGHT: Maybe some better place for this logic?
    private static HttpMethod getHttpMethod(String httpRequest) {
        String httpMethod = httpRequest.split(" ")[0];
        switch (httpMethod) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            case "PUT":
                return HttpMethod.PUT;
            case "DELETE":
                return HttpMethod.DELETE;
            default:
                return null; // Oder werfen Sie eine Exception
        }

    }

    private static String getRoute(String httpRequest) {
        return httpRequest.split(" ")[1];
    }

    private static String getHttpHeader(String header, String httpRequest) {
        Pattern regex = Pattern.compile("^" + header + ":\\s(.+)", Pattern.MULTILINE);
        Matcher matcher = regex.matcher(httpRequest);



        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }
}
