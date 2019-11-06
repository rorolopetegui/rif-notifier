import mocked.MockTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.SubscriptionType;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.services.SubscribeServices;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscribeServiceTest {
    @InjectMocks
    private SubscribeServices subscribeServices;

    @Mock
    private DbManagerFacade dbManagerFacade;

    private MockTestData mockTestData = new MockTestData();

    @Test
    public void canCreateSubscription(){
        // given
        User user = mockTestData.mockUser();
        SubscriptionType type = mockTestData.mockSubscriptionType();
        Subscription sub = mockTestData.mockSubscription();
        String luminoVal = LuminoInvoice.generateInvoice(user.getAddress());

        doReturn(sub).when(dbManagerFacade).createSubscription(new Date(), user.getAddress(), type, SubscriptionConstants.PENDING_PAYMENT);
        doReturn(type).when(dbManagerFacade).getSubscriptionTypeByType(0);

        // when
        String retVal = subscribeServices.createSubscription(user, type);

        // then
        assertEquals(luminoVal, retVal);
    }
    @Test
    public void errorCreateSubscriptionNotProvidingUser(){
        // given
        User user = null;
        SubscriptionType type = mockTestData.mockSubscriptionType();

        // when
        String retVal = subscribeServices.createSubscription(user, type);

        // then
        assertEquals("", retVal);
    }
    @Test
    public void errorCreateSubscriptionNotProvidingType(){
        // given
        User user = mockTestData.mockUser();
        SubscriptionType type = null;

        // when
        String retVal = subscribeServices.createSubscription(user, type);

        // then
        assertEquals("", retVal);
    }
    @Test
    public void errorCreateSubscriptionInvalidType(){
        // given
        User user = mockTestData.mockUser();
        SubscriptionType type = mockTestData.mockSubscriptionType();

        doReturn(null).when(dbManagerFacade).getSubscriptionTypeByType(0);
        // when
        String retVal = subscribeServices.createSubscription(user, type);

        // then
        assertEquals("", retVal);
    }
    @Test
    public void isSubscriptionTypeValid() {
        SubscriptionType type = mockTestData.mockSubscriptionType();

        doReturn(type).when(dbManagerFacade).getSubscriptionTypeByType(0);

        boolean retVal = subscribeServices.isSubscriptionTypeValid(0);

        assertTrue(retVal);
    }
    @Test
    public void errorSubscriptionTypeInvalid() {
        SubscriptionType type = mockTestData.mockSubscriptionType();

        doReturn(null).when(dbManagerFacade).getSubscriptionTypeByType(0);

        boolean retVal = subscribeServices.isSubscriptionTypeValid(0);

        assertFalse(retVal);
    }
    @Test
    public void getSubscriptionTypeByType() {
        SubscriptionType type = mockTestData.mockSubscriptionType();

        doReturn(type).when(dbManagerFacade).getSubscriptionTypeByType(0);

        SubscriptionType retVal = subscribeServices.getSubscriptionTypeByType(0);

        assertEquals(retVal, type);
    }
    @Test
    public void canValidateTopic() throws IOException {
        Topic topic = mockTestData.mockTopic();

        boolean retVal = subscribeServices.validateTopic(topic);

        assertTrue(retVal);
    }
    @Test
    public void errorValidateTopicInvalid() throws IOException {
        Topic topic = mockTestData.mockInvalidTopic();

        boolean retVal = subscribeServices.validateTopic(topic);

        assertFalse(retVal);
    }
    @Test
    public void getActiveSubscriptionByAddress() {
        Subscription subscription = mockTestData.mockSubscription();

        doReturn(subscription).when(dbManagerFacade).getActiveSubscriptionByAddress("0x0");

        Subscription retVal = subscribeServices.getActiveSubscriptionByAddress("0x0");

        assertEquals(subscription, retVal);
    }
    @Test
    public void canActivateSubscription(){
        // given
        Subscription activeSubscription = mockTestData.mockSubscription();
        Subscription inactiveSubscription = mockTestData.mockInactiveSubscription();

        doReturn(activeSubscription).when(dbManagerFacade).updateSubscription(inactiveSubscription);

        // when
        boolean retVal = subscribeServices.activateSubscription(inactiveSubscription);

        // then
        assertTrue(retVal);
    }
    @Test
    public void errorActivateSubscriptionAlreadyActive(){
        // given
        Subscription activeSubscription = mockTestData.mockSubscription();
        Subscription inactiveSubscription = mockTestData.mockInactiveSubscription();

        doReturn(activeSubscription).when(dbManagerFacade).updateSubscription(inactiveSubscription);

        // when
        boolean retVal = subscribeServices.activateSubscription(activeSubscription);

        // then
        assertFalse(retVal);
    }
    @Test
    public void canAddBalanceToSubscription(){
        // given
        String luminoInvoice = "123457A90123457B901234C579012345D79012E345790F12345G790123H45790I";
        Subscription subscription = mockTestData.mockSubscription();
        SubscriptionType type = mockTestData.mockSubscriptionType();

        doReturn(subscription).when(dbManagerFacade).updateSubscription(subscription);

        // when
        String retVal = subscribeServices.addBalanceToSubscription(subscription, type);

        // then
        assertEquals(luminoInvoice, retVal);
    }
    @Test
    public void errorAddBalanceToSubscriptionNotProvidingSubscription(){
        // given
        String expected = "";
        //Subscription subscription = mockTestData.mockSubscription();
        SubscriptionType type = mockTestData.mockSubscriptionType();

        //doReturn(subscription).when(dbManagerFacade).updateSubscription(subscription);

        // when
        String retVal = subscribeServices.addBalanceToSubscription(null, type);

        // then
        assertEquals(expected, retVal);
    }
    @Test
    public void errorAddBalanceToSubscriptionNotProvidingSubscriptionType(){
        // given
        String expected = "";
        Subscription subscription = mockTestData.mockSubscription();
        //SubscriptionType type = mockTestData.mockSubscriptionType();

        //doReturn(subscription).when(dbManagerFacade).updateSubscription(subscription);

        // when
        String retVal = subscribeServices.addBalanceToSubscription(subscription, null);

        // then
        assertEquals(expected, retVal);
    }
}
