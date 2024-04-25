package main.server;
import main.server.http.Request;
import main.server.http.Response;
import main.server.util.HttpMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Make the class implement the Runnable interface
public class RequestHandler implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private final Socket client;
    private final ServerApplication app;

    public RequestHandler(Socket client, ServerApplication app) {
        this.client = client;
        this.app = app;
    }

    // Override the run method provided by the Runnable interface
    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() throws IOException {
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String httpRequest = getHttpStringFromStream(in);
        Request request = HttpMapper.toRequestObject(httpRequest);

        Response response = app.handle(request);

        out = new PrintWriter(client.getOutputStream(), true);
        out.write(HttpMapper.toResponseString(response));

        out.close();
        in.close();
        client.close();
    }

    // THOUGHT: create a SocketReader class
    private String getHttpStringFromStream(BufferedReader in) throws IOException {
        StringBuilder builder = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null && !inputLine.equals("")) {
            builder
                    .append(inputLine)
                    .append(System.lineSeparator());

            // Check for the Authorization header specifically
            if (inputLine.startsWith("Authorization: ")) {
                // Extract the token from the header
                String token = inputLine.substring("Authorization: ".length());
                // Assuming the Request object has a method to set the authorization token
                // request.setAuthorization(token);
            }
        }

        // Read the body of the request if Content-Length is present
        String httpRequest = builder.toString();
        Pattern regex = Pattern.compile("^Content-Length:\\s(.+)", Pattern.MULTILINE);
        Matcher matcher = regex.matcher(httpRequest);
        if (matcher.find()) {
            builder.append(System.lineSeparator());
            int contentLength = Integer.parseInt(matcher.group(1).trim());
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            builder.append(buffer);
        }

        return builder.toString();
    }

}
