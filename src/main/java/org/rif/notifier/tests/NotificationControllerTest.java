package org.rif.notifier.tests;

import org.junit.runner.RunWith;
import org.rif.notifier.controllers.NotificationController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = NotificationController.class)
public class NotificationControllerTest {
}
