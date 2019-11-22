import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rif.notifier.Application;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.controllers.UserController;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.services.UserServices;
import org.rif.notifier.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes={Application.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @Test
    public void canRegister() throws Exception {
        String address = "0xE82e938B33954E231c51FcE98576049f13471226";
        DTOResponse dto = new DTOResponse();
        when(userServices.userExists(address)).thenReturn(false);
        MvcResult result = mockMvc.perform(
                post("/users")
                        .param("address", address)
                        .content("0x7ba4f7b18562a84e936ed50b7450f008546596230d2118105cc346c0c6fcf59062f56f30a90a68566de60d8634b75fee821b3ef5998dc0f3d86fc43254f93e3f1b")
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
        String address = "0xE82e938B33954E231c51FcE98576049f13471226";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.APIKEY_ALREADY_ADDED);
        when(userServices.userExists(address)).thenReturn(true);
        MvcResult result = mockMvc.perform(
                post("/users")
                        .param("address", address)
                        .content("0x7ba4f7b18562a84e936ed50b7450f008546596230d2118105cc346c0c6fcf59062f56f30a90a68566de60d8634b75fee821b3ef5998dc0f3d86fc43254f93e3f1b")
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
    @Test
    public void errorAddressNotProvided() throws Exception {
        String address = "0xE82e938B33954E231c51FcE98576049f13471226";
        DTOResponse dto = new DTOResponse();
        when(userServices.userExists(address)).thenReturn(false);
        mockMvc.perform(
                post("/users")
        )
                .andExpect(status().isBadRequest());
        ;
    }
    @Test
    public void errorSignedAddressNotProvided() throws Exception {
        String address = "0xE82e938B33954E231c51FcE98576049f13471226";
        DTOResponse dto = new DTOResponse();
        when(userServices.userExists(address)).thenReturn(false);
        mockMvc.perform(
                post("/users")
                        .param("address", address)
        )
                .andExpect(status().isBadRequest());
        ;
    }
    @Test
    public void errorWhenProvidingSignedAddressIncorrect() throws Exception {
        String address = "0xE82e938B33954E231c51FcE98576049f13471226";
        DTOResponse dto = new DTOResponse();
        dto.setMessage(ResponseConstants.INCORRECT_SIGNED_ADDRESS);
        when(userServices.userExists(address)).thenReturn(false);
        MvcResult result = mockMvc.perform(
                post("/users")
                        .param("address", address)
                        .content("12345")
        )
                .andExpect(status().isConflict())
                .andReturn();
        DTOResponse dtResponse = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                DTOResponse.class);
        assertEquals(dto.getMessage(), dtResponse.getMessage());
    }
}
