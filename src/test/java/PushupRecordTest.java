import main.mtcg.entity.PushUpRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class PushupRecordTest {

    private PushUpRecord pushUpRecord;
    @BeforeEach
    void setup()
    {
        pushUpRecord=new PushUpRecord(20, 100, 1);

    }
    @Test
    void testGetPlayerId() {
        assertEquals(1, pushUpRecord.getUserId());
    }

    @Test
    void testSetPlayerId() {
        pushUpRecord.setUserId(2);
        assertEquals(2, pushUpRecord.getUserId());
    }

    @Test
    void testPushUpRecord() {
        assertEquals(20, pushUpRecord.getCount());
    }

    @Test
    void testDuraction() {
        assertEquals(100, pushUpRecord.getDuration());
    }

    @Test
    void newCount()
    {
        pushUpRecord.setCount(30);
        assertEquals(30, pushUpRecord.getCount());
    }


}
