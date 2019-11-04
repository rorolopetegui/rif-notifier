import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.controllers.UserController;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @Test
    public void canRegister() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        when(userServices.userExists(address)).thenReturn(false);
        MvcResult result = mockMvc.perform(
                post("/users")
                        .param("address", address)
        )
                .andExpect(status().isOk())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dto.toString(), dtResponse.toString());
    }
    @Test
    public void errorUserAlreadyRegistered() throws Exception {
        String address = "0x0";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.APIKEY_ALREADY_ADDED);
        when(userServices.userExists(address)).thenReturn(true);
        MvcResult result = mockMvc.perform(
                post("/users")
                        .param("address", address)
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
}
