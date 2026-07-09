package com.hh.oneplusplus.controller;

import com.hh.oneplusplus.dto.MarkReadRequestDto;
import com.hh.oneplusplus.dto.NotificationPageResponse;
import com.hh.oneplusplus.dto.UnreadCountResponse;
import com.hh.oneplusplus.service.NotificationQueryService;
import com.hh.oneplusplus.service.SecurityContextService;
import com.hh.oneplusplus.service.SseEmitterService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final SseEmitterService sseEmitterService;
    private final NotificationQueryService notificationQueryService;
    private final SecurityContextService securityContextService;

    public NotificationController(
            SseEmitterService sseEmitterService,
            NotificationQueryService notificationQueryService,
            SecurityContextService securityContextService) {
        this.sseEmitterService = sseEmitterService;
        this.notificationQueryService = notificationQueryService;
        this.securityContextService = securityContextService;
    }
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(){
        Long userId = securityContextService.getUserId();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onTimeout(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onError((e) -> sseEmitterService.removeEmitter(userId, emitter));

        sseEmitterService.addEmitter(userId, emitter);
        return emitter;
    }

    @GetMapping
    public ResponseEntity<NotificationPageResponse> getNotifications(Pageable pageable){
        return ResponseEntity.ok().body(notificationQueryService.getNotifications(pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(){
        return ResponseEntity.ok().body(notificationQueryService.getUnreadCount());
    }

    @PatchMapping
    public ResponseEntity<Void> markAsRead(@RequestBody MarkReadRequestDto readRequestDto){
        notificationQueryService.markSelectedAsRead(readRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId) {
        notificationQueryService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications() {
        notificationQueryService.deleteAllNotifications();
        return ResponseEntity.noContent().build();
    }

}
