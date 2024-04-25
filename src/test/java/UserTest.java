import main.mtcg.entity.PushUpRecord;
import main.mtcg.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;


import static org.junit.Assert.assertEquals;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        PushUpRecord pushUpRecord= new PushUpRecord(20, 100, 1);
        List<PushUpRecord> pushUpRecordList=new ArrayList<>();
        pushUpRecordList.add(pushUpRecord);
        user = new User(1, "testUser", "password", "test@example.com", pushUpRecordList, "token123");
    }

    @Test
    void testGetPlayerId() {
        assertEquals(1, user.getPlayerId());
    }

    @Test
    void testSetPlayerId() {
        user.setPlayerId(2);
        assertEquals(2, user.getPlayerId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testSetUsername() {
        user.setUsername("newUsername");
        assertEquals("newUsername", user.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("password", user.getPassword());
    }

    @Test
    void testSetPassword() {
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetEmail() {
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
    }



    @Test
    void testGetToken() {
        assertEquals("token123", user.getToken());
    }

    @Test
    void testSetToken() {
        user.setToken("newToken");
        assertEquals("newToken", user.getToken());
    }

}
