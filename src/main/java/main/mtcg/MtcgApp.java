package main.mtcg;

import main.mtcg.controller.*;
import main.mtcg.repository.*;
import main.server.ServerApplication;
import main.server.http.*;

import java.util.ArrayList;
import java.util.List;

public class MtcgApp implements ServerApplication {

    private List<Controller> controllers = new ArrayList<>();
    private AuthenticationService authenticationService;


    public MtcgApp() {
        // Initialize repositories here
        authenticationService = new AuthenticationService();

        UserRepository userRepository = new UserRepository();



        // Add controllers
        controllers.add(new PushupController(authenticationService));
        controllers.add(new UserController(authenticationService));


    }

    @Override
    public Response handle(Request request) {
        for (Controller controller : controllers) {
            if (controller.supports(request.getRoute())) {
                return controller.handle(request);
            }
        }
        return null;
    }
}
