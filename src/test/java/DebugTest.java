import com.fasterxml.jackson.databind.ObjectMapper;
import mocked.MockTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.Topic;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DebugTest {
    @Mock
    private DbManagerFacade dbManagerFacade;

    private MockTestData mockTestData = new MockTestData();

    @Test
    public void testHash() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String tp = "{\"type\": \"CONTRACT_EVENT\", \"topicParams\":[{\"type\": \"CONTRACT_ADDRESS\", \"value\": \"0x96463f6463771ed9f9d730986501b17127823fd2\" }, {\"type\": \"EVENT_NAME\", \"value\": \"LogSellArticle\" }, { \"type\": \"EVENT_PARAM\", \"value\": \"seller\", \"order\": 0, \"valueType\": \"Address\", \"indexed\": 1 }, { \"type\": \"EVENT_PARAM\",\"value\": \"article\", \"order\": 1, \"valueType\": \"Utf8String\", \"indexed\": 0 }, { \"type\": \"EVENT_PARAM\", \"value\": \"price\", \"order\": 2, \"valueType\": \"Uint256\",\"indexed\": 0 }]}";
        Topic topic = mapper.readValue(tp, Topic.class);
        int hash = topic.hashCode();

        assertEquals(hash, -554920063);
    }
}
