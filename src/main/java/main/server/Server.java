package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;

    private final ServerApplication app;

    public Server(ServerApplication app) {
        this.app = app;
    }

    public void start() {
        try {
            server = new ServerSocket(10001);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server started on http://localhost:10001");

        while (true) {
            try {
                Socket clientSocket = server.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket, app);
                Thread thread = new Thread(requestHandler);
                thread.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
