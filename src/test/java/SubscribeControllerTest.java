import com.fasterxml.jackson.databind.ObjectMapper;
import mocked.MockTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rif.notifier.Application;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.controllers.SubscribeController;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.SubscriptionType;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.services.SubscribeServices;
import org.rif.notifier.services.UserServices;
import org.rif.notifier.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubscribeController.class)
@ContextConfiguration(classes={Application.class})
public class SubscribeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @MockBean
    private SubscribeServices subscribeServices;

    private MockTestData mockTestData = new MockTestData();

    @Test
    public void canSubscribe() throws Exception {
        String address = "0x0";
        String luminoInvoice = "123457A90123457B901234C579012345D79012E345790F12345G790123H45790I";
        DTOResponse dto = new DTOResponse();
        dto.setData(luminoInvoice);
        String apiKey = Utils.generateNewToken();
        User us = new User(address, apiKey);
        when(userServices.getUserByApiKey(apiKey)).thenReturn(us);
        when(subscribeServices.getSubscriptionByAddress(us.getAddress())).thenReturn(null);
        MvcResult result = mockMvc.perform(
                post("/subscribe")
                        .param("type", "0")
                        .header("apiKey", apiKey)
        )
                .andExpect(status().isOk())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dto.getStatus(), dtResponse.getStatus());
    }

    @Test
    public void canSendTopic() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        String apiKey = Utils.generateNewToken();
        User us = new User(address, apiKey);
        SubscriptionType subType = new SubscriptionType(1000);
        Subscription sub = new Subscription(new Date(), us.getAddress(), subType, "PAYED");
        Topic tp = mockTestData.mockTopic();
        when(userServices.getUserByApiKey(apiKey)).thenReturn(us);
        when(subscribeServices.getSubscriptionByAddress(us.getAddress())).thenReturn(sub);
        //Need to mock with any, cause it was always returning false, maybe cause the Topic that we bring in here was not the same as in the controller
        when(subscribeServices.validateTopic(any(Topic.class))).thenReturn(true);
        //when(subscribeServices.validateTopic(tp)).thenCallRealMethod();
        MvcResult result = mockMvc.perform(
                post("/subscribeToTopic")
                        .contentType(APPLICATION_JSON_UTF8)
                        .header("apiKey", apiKey)
                        .content(tp.toString())
        )
                .andExpect(status().isOk())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);

        assertEquals(dto.getStatus(), dtResponse.getStatus());
    }

    @Test
    public void errorWhenNotProvidingCorrectApiKey() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.INCORRECT_APIKEY);
        String apiKey = Utils.generateNewToken();
        Topic tp = mockTestData.mockTopic();
        when(userServices.getUserByApiKey(apiKey)).thenReturn(null);
        MvcResult result = mockMvc.perform(
                post("/subscribeToTopic")
                        .contentType(APPLICATION_JSON_UTF8)
                        .header("apiKey", apiKey)
                        .content(tp.toString())
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);

        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
    @Test
    public void errorWhenNotSubscribed() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.SUBSCRIPTION_NOT_FOUND);
        String apiKey = Utils.generateNewToken();
        User us = new User(address, apiKey);
        Topic tp = mockTestData.mockTopic();
        when(userServices.getUserByApiKey(apiKey)).thenReturn(us);
        MvcResult result = mockMvc.perform(
                post("/subscribeToTopic")
                        .contentType(APPLICATION_JSON_UTF8)
                        .header("apiKey", apiKey)
                        .content(tp.toString())
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);

        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
    @Test
    public void errorWhenTopicWrong() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.TOPIC_VALIDATION_FAILED);
        String apiKey = Utils.generateNewToken();
        User us = new User(address, apiKey);
        SubscriptionType subType = new SubscriptionType(1000);
        Subscription sub = new Subscription(new Date(), us.getAddress(), subType, "PAYED");
        Topic tp = mockTestData.mockInvalidTopic();
        when(userServices.getUserByApiKey(apiKey)).thenReturn(us);
        when(subscribeServices.getSubscriptionByAddress(us.getAddress())).thenReturn(sub);

        MvcResult result = mockMvc.perform(
                post("/subscribeToTopic")
                        .contentType(APPLICATION_JSON_UTF8)
                        .header("apiKey", apiKey)
                        .content(tp.toString())
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);

        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
}
