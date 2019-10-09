package org.rif.notifier.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rif.notifier.controllers.SubscribeController;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.services.SubscribeServices;
import org.rif.notifier.services.UserServices;
import org.rif.notifier.tests.mocked.MockTestData;
import org.rif.notifier.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubscribeController.class)
public class SubscribeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @MockBean
    private SubscribeServices subscribeServices;

    private MockTestData mockTestData;

    @Test
    public void canSubscribe() throws Exception {
        String address = "0x0";
        String luminoInvoice = "123457A90123457B901234C579012345D79012E345790F12345G790123H45790I";
        DTOResponse dto = new DTOResponse();
        dto.setData(luminoInvoice);
        String apiKey = Utils.generateNewToken();
        User us = new User(address, apiKey);
        when(userServices.getUserByApiKey(apiKey)).thenReturn(us);
        when(subscribeServices.createSubscription(us, 0)).thenReturn(luminoInvoice);
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
        assertEquals(dtResponse.toString(), dto.toString());
    }
    @Test
    public void canSendTopic() throws Exception {
        /*
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        String apiKey = Utils.generateNewToken();
        //User us = new User(address, apiKey);
        Topic tp = mockTestData.mockTopic();
        //when(subscribeServices.getSubscriptionByAddress(address)).thenReturn(sub);
        when(subscribeServices.validateTopic(tp)).thenReturn(true);
        MvcResult result = mockMvc.perform(
                post("/subscribeToTopic")
                .header("apiKey", apiKey)
                .content(tp.toString())
        )
                .andExpect(status().isOk())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dtResponse.toString(), dto.toString());
         */
    }
}
