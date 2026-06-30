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
    private UUID notificationId;
    private Long userId;

    @BeforeEach
    void setUp() {
        Map<String, NotificationSender> senders = Map.of(
                "WEB", webSender,
                "MAIL", mailSender
        );
        notificationService = new NotificationService(senders, factory, notificationRepository, mapper);

        notificationId = UUID.randomUUID();
        userId = 11L;

        event = mock(NotificationEvent.class);

        responseDto = new NotificationResponseDto(
                notificationId,
                NotificationEventType.WELCOME,
                "test@gmail.com",
                Instant.now(),
                "Добро пожаловать!",
                Map.of()
        );
    }

    @Test
    void shouldSaveAndSendWhenTypeIsWeb() {
        when(event.getType()).thenReturn(NotificationType.WEB);
        when(event.getNotificationId()).thenReturn(notificationId);
        when(event.getUserId()).thenReturn(userId);
        when(factory.create(event)).thenReturn(responseDto);
        when(notificationRepository.existsByNotificationId(notificationId)).thenReturn(false);
        when(mapper.toEntity(event, responseDto)).thenReturn(mock(Notification.class));

        notificationService.handle(event);

        verify(notificationRepository).save(any(Notification.class));
        verify(webSender).send(userId, responseDto);
        verify(mailSender, never()).send(any(), any());
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
        when(event.getType()).thenReturn(NotificationType.WEB);
        when(event.getNotificationId()).thenReturn(notificationId);
        when(event.getUserId()).thenReturn(userId);
        when(factory.create(event)).thenReturn(responseDto);
        when(notificationRepository.existsByNotificationId(notificationId)).thenReturn(true);

        notificationService.handle(event);

        verify(notificationRepository, never()).save(any());
        verify(webSender).send(userId, responseDto);
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
