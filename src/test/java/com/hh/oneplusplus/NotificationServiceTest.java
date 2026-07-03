package com.hh.oneplusplus;

import com.hh.oneplusplus.dto.NotificationReadyToSendEvent;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.NotificationResponseDtoFactory;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.mapper.NotificationMapper;
import com.hh.oneplusplus.model.Notification;
import com.hh.oneplusplus.repository.NotificationDeliveryRepository;
import com.hh.oneplusplus.repository.NotificationRepository;
import com.hh.oneplusplus.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationResponseDtoFactory factory;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private NotificationService notificationService;

    private NotificationEvent event;
    private NotificationResponseDto responseDto;
    private Long userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                factory, notificationRepository, mapper, notificationDeliveryRepository, eventPublisher);

        userId = 11L;
        notificationId = UUID.randomUUID();
        event = mock(NotificationEvent.class);
        when(event.getNotificationId()).thenReturn(notificationId);

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
    void shouldSaveAndPublishEventWhenTypeIsWeb() {
        Notification mockNotification = mock(Notification.class);

        when(event.getType()).thenReturn(NotificationType.WEB);
        when(event.getUserId()).thenReturn(userId);
        when(notificationDeliveryRepository.tryReserve(notificationId, "WEB")).thenReturn(1);
        when(factory.create(event)).thenReturn(responseDto);
        when(mapper.toEntity(event, responseDto)).thenReturn(mockNotification);

        notificationService.handle(event);

        verify(notificationRepository).save(mockNotification);

        ArgumentCaptor<NotificationReadyToSendEvent> captor =
                ArgumentCaptor.forClass(NotificationReadyToSendEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        NotificationReadyToSendEvent published = captor.getValue();
        assertThat(published.channel()).isEqualTo("WEB");
        assertThat(published.userId()).isEqualTo(userId);
        assertThat(published.responseDto()).isEqualTo(responseDto);
    }

    @Test
    void shouldNotSaveWhenTypeIsMail() {
        when(event.getType()).thenReturn(NotificationType.MAIL);
        when(event.getUserId()).thenReturn(userId);
        when(notificationDeliveryRepository.tryReserve(notificationId, "MAIL")).thenReturn(1);
        when(factory.create(event)).thenReturn(responseDto);

        notificationService.handle(event);

        verify(notificationRepository, never()).save(any());

        ArgumentCaptor<NotificationReadyToSendEvent> captor =
                ArgumentCaptor.forClass(NotificationReadyToSendEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().channel()).isEqualTo("MAIL");
    }

    @Test
    void shouldSkipProcessingWhenAlreadyReserved() {
        when(event.getType()).thenReturn(NotificationType.WEB);
        when(notificationDeliveryRepository.tryReserve(notificationId, "WEB")).thenReturn(0);

        notificationService.handle(event);

        verify(notificationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
        verify(factory, never()).create(any());
    }

    @Test
    void shouldReserveWithCorrectChannelPerType() {
        when(event.getType()).thenReturn(NotificationType.MAIL);
        when(event.getUserId()).thenReturn(userId);
        when(notificationDeliveryRepository.tryReserve(notificationId, "MAIL")).thenReturn(1);
        when(factory.create(event)).thenReturn(responseDto);

        notificationService.handle(event);

        verify(notificationDeliveryRepository).tryReserve(notificationId, "MAIL");
    }
}
