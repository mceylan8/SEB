import main.mtcg.controller.AuthenticationService;
import main.mtcg.controller.UserController;
import main.server.http.HttpStatus;
import main.server.http.Request;
import main.server.http.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class UserControllerTest {
    private UserController userController;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        AuthenticationService mockAuthService = Mockito.mock(AuthenticationService.class);
        userController = new UserController(mockAuthService);

        // Set up the real database connection
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/SEB",
                "postgres",
                "12345678"
        );
    }

    @Test
//    void testUserRegistrationViaCurlExample() throws Exception {
//        // Simulate the curl command as an HTTP request
//        String requestBody = "{\"playerId\":1, \"Username\":\"Mehmat\", \"Password\":\"AUS\", \"email\":\"danial@gmail.com\"}";
//        Request request = new Request("POST", "/users", "Content-Type: application/json", requestBody);
//
//        // Perform the action using the actual database connection
//        Response response = userController.handle(request);
//
//        // Check that the response status code is CREATED (201) or CONFLICT (409) depending on if the user already exists
//        boolean userCreated = response.getStatusMessage() == HttpStatus.CREATED.toString();
//        boolean userExists = response.getStatusMessage() == HttpStatus.CONFLICT.toString();
//        assertTrue(userCreated || userExists);
//
//        if (userCreated) {
//            assertEquals("{\"message\":\"User created\"}", response.getBody());
//        } else {
//            assertEquals(HttpStatus.CONFLICT.getMessage(), response.getBody());
//        }
//    }

    // Other tests for different scenarios...

    // Clean up after tests
    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
