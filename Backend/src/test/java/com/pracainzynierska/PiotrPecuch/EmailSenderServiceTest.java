package com.pracainzynierska.PiotrPecuch;

import com.pracainzynierska.PiotrPecuch.Services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailSenderServiceTest {
    @Mock
    private EmailSenderService emailSenderService;

    @Test
    public void testSendEmail() {
        // Given
        String userEmail = "student@example.com";
        String adminEmail = "admin@example.com";
        String link = "http://example.com/reset";

        // When
        doNothing().when(emailSenderService).sendEmail(anyString(), anyString(), anyString());
        emailSenderService.sendEmail(userEmail, adminEmail, link);

        // Then
        verify(emailSenderService, times(1)).sendEmail(userEmail, adminEmail, link);
    }
}
