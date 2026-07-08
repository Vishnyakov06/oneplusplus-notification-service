package com.hh.oneplusplus;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.service.SseEmitterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.atLeastOnce;
@ExtendWith(MockitoExtension.class)
class SseEmitterServiceTest {

    private SseEmitterService sseEmitterService;

    @BeforeEach
    void setUp() {
        sseEmitterService = new SseEmitterService();
        ReflectionTestUtils.setField(sseEmitterService, "maxTabsPerUser", 2);
        ReflectionTestUtils.setField(sseEmitterService, "reconnectTimeMs", 1000L);
    }

    private NotificationResponseDto buildDto() {
        return new NotificationResponseDto(
                UUID.randomUUID(),
                NotificationEventType.WELCOME,
                "test@gmail.com",
                Instant.now(),
                "test message",
                Map.of()
        );
    }

    @Test
    void shouldAddEmitterForUser() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);

        sseEmitterService.addEmitter(1L, emitter);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void shouldEvictOldestEmitterWhenLimitExceeded() throws IOException {
        SseEmitter emitter1 = mock(SseEmitter.class);
        SseEmitter emitter2 = mock(SseEmitter.class);
        SseEmitter emitter3 = mock(SseEmitter.class);

        sseEmitterService.addEmitter(1L, emitter1);
        sseEmitterService.addEmitter(1L, emitter2);
        sseEmitterService.addEmitter(1L, emitter3);

        verify(emitter1).complete();
    }

    @Test
    void shouldSendNotificationToAllUserEmitters() throws IOException {
        SseEmitter emitter1 = mock(SseEmitter.class);
        SseEmitter emitter2 = mock(SseEmitter.class);

        sseEmitterService.addEmitter(1L, emitter1);
        sseEmitterService.addEmitter(1L, emitter2);

        NotificationResponseDto dto = buildDto();

        sseEmitterService.sendToUser(1L, dto);

        verify(emitter1, times(2)).send(any(SseEmitter.SseEventBuilder.class));
        verify(emitter2, times(2)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void shouldDoNothingWhenUserHasNoEmitters() {
        NotificationResponseDto dto = buildDto();

        assertDoesNotThrow(() -> sseEmitterService.sendToUser(999L, dto));
    }

    @Test
    void shouldRemoveEmitterOnSendFailure() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        doThrow(new IOException("connection closed"))
                .when(emitter).send(any(SseEmitter.SseEventBuilder.class));

        sseEmitterService.addEmitter(1L, emitter);

        NotificationResponseDto dto = buildDto();

        sseEmitterService.sendToUser(1L, dto);

        verify(emitter, atLeastOnce()).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void shouldRemoveEmitterManually() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        sseEmitterService.addEmitter(1L, emitter);

        sseEmitterService.removeEmitter(1L, emitter);

        NotificationResponseDto dto = buildDto();

        assertDoesNotThrow(() -> sseEmitterService.sendToUser(1L, dto));

        verify(emitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }
}
