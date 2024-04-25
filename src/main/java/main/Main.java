package main;

import main.mtcg.MtcgApp;
import main.server.Server;

public class Main {

    public static void main(String a[])
    {
        Server server = new Server(new MtcgApp());
        server.start();
    }
}
