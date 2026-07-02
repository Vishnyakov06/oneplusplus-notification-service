package com.hh.oneplusplus;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.NotificationResponseDtoFactory;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.mapper.NotificationMapper;
import com.hh.oneplusplus.model.Notification;
import com.hh.oneplusplus.repository.NotificationRepository;
import com.hh.oneplusplus.sender.NotificationSender;
import com.hh.oneplusplus.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationResponseDtoFactory factory;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private NotificationSender webSender;

    @Mock
    private NotificationSender mailSender;

    private NotificationService notificationService;

    private NotificationEvent event;
    private NotificationResponseDto responseDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        Map<String, NotificationSender> senders = Map.of(
                "WEB", webSender,
                "MAIL", mailSender
        );
        notificationService = new NotificationService(senders, factory, notificationRepository, mapper);

        userId = 11L;
        event = mock(NotificationEvent.class);

        responseDto = new NotificationResponseDto(
                UUID.randomUUID(),
                NotificationEventType.WELCOME,
                "test@gmail.com",
                Instant.now(),
                "Добро пожаловать!",
                Map.of()
        );
    }

    @Test
    void shouldSaveAndSendWhenTypeIsWeb() {
        Notification mockNotification = mock(Notification.class);

        when(event.getType()).thenReturn(NotificationType.WEB);
        when(event.getUserId()).thenReturn(userId);
        when(factory.create(event)).thenReturn(responseDto);
        when(mapper.toEntity(event, responseDto)).thenReturn(mockNotification);

        notificationService.handle(event);

        verify(notificationRepository).save(mockNotification);
        verify(webSender).send(userId, responseDto);
    }

    @Test
    void shouldNotSaveWhenTypeIsMail() {
        when(event.getType()).thenReturn(NotificationType.MAIL);
        when(event.getUserId()).thenReturn(userId);
        when(factory.create(event)).thenReturn(responseDto);

        notificationService.handle(event);

        verify(notificationRepository, never()).save(any());
        verify(mailSender).send(userId, responseDto);
    }

    @Test
    void shouldNotSaveDuplicateNotification() {
        Notification mockNotification = mock(Notification.class);

        when(event.getType()).thenReturn(NotificationType.WEB);
        when(factory.create(event)).thenReturn(responseDto);
        when(mapper.toEntity(event, responseDto)).thenReturn(mockNotification);
        when(notificationRepository.save(mockNotification))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        notificationService.handle(event);

        verify(notificationRepository).save(mockNotification);
        verify(webSender, never()).send(any(), any());
    }

    @Test
    void shouldCallCorrectSenderBasedOnType() {
        when(event.getType()).thenReturn(NotificationType.MAIL);
        when(event.getUserId()).thenReturn(userId);
        when(factory.create(event)).thenReturn(responseDto);

        notificationService.handle(event);

        verify(mailSender, times(1)).send(userId, responseDto);
        verify(webSender, never()).send(any(), any());
    }
}