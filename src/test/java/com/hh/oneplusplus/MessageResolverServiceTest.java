package com.hh.oneplusplus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.dto.notification.WelcomeNotification;
import com.hh.oneplusplus.exception.TemplateNotFoundException;
import com.hh.oneplusplus.model.MessageTemplate;
import com.hh.oneplusplus.repository.MessageTemplateRepository;
import com.hh.oneplusplus.service.MessageResolverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageResolverServiceTest {

    @Mock
    private MessageTemplateRepository templateRepository;

    private MessageResolverService resolverService;

    @BeforeEach
    void setUp() {
        Cache<String, String> cache = Caffeine.newBuilder().build();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()); // добавить
        resolverService = new MessageResolverService(templateRepository, cache, objectMapper);
    }

    @Test
    void shouldResolveMessageWithPlaceholders() {
        WelcomeNotification event = new WelcomeNotification(
                UUID.randomUUID(),
                11L,
                "test@gmail.com",
                NotificationType.WEB,
                Instant.now(),
                "Иван",
                "Петров"
        );

        MessageTemplate template = new MessageTemplate();
        template.setEventType(NotificationEventType.WELCOME);
        template.setTemplate("Добро пожаловать, ${userName} ${userSurname}!");

        when(templateRepository.findByEventType(NotificationEventType.WELCOME))
                .thenReturn(Optional.of(template));

        String result = resolverService.resolveMessage(event);

        assertThat(result).isEqualTo("Добро пожаловать, Иван Петров!");
    }

    @Test
    void shouldThrowWhenTemplateNotFound() {
        WelcomeNotification event = new WelcomeNotification(
                UUID.randomUUID(),
                11L,
                "test@gmail.com",
                NotificationType.WEB,
                Instant.now(),
                "Иван",
                "Петров"
        );

        when(templateRepository.findByEventType(NotificationEventType.WELCOME))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolverService.resolveMessage(event))
                .isInstanceOf(TemplateNotFoundException.class)
                .hasMessageContaining("WELCOME");
    }

    @Test
    void shouldUseCacheOnSecondCall() {
        WelcomeNotification event = new WelcomeNotification(
                UUID.randomUUID(),
                11L,
                "test@gmail.com",
                NotificationType.WEB,
                Instant.now(),
                "Иван",
                "Петров"
        );

        MessageTemplate template = new MessageTemplate();
        template.setEventType(NotificationEventType.WELCOME);
        template.setTemplate("Привет, ${userName}");

        when(templateRepository.findByEventType(NotificationEventType.WELCOME))
                .thenReturn(Optional.of(template));

        resolverService.resolveMessage(event);
        resolverService.resolveMessage(event);

        verify(templateRepository, times(1)).findByEventType(NotificationEventType.WELCOME);
    }

    @Test
    void shouldIgnorePlaceholdersNotPresentInEvent() {
        WelcomeNotification event = new WelcomeNotification(
                UUID.randomUUID(),
                11L,
                "test@gmail.com",
                NotificationType.WEB,
                Instant.now(),
                "Иван",
                "Петров"
        );

        MessageTemplate template = new MessageTemplate();
        template.setEventType(NotificationEventType.WELCOME);
        template.setTemplate("Текст без плейсхолдеров");

        when(templateRepository.findByEventType(NotificationEventType.WELCOME))
                .thenReturn(Optional.of(template));

        String result = resolverService.resolveMessage(event);

        assertThat(result).isEqualTo("Текст без плейсхолдеров");
    }
}
