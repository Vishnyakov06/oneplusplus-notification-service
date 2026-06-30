package com.hh.oneplusplus.controller;

import com.hh.oneplusplus.service.SseEmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final SseEmitterService sseEmitterService;

    public NotificationController(SseEmitterService sseEmitterService) {
        this.sseEmitterService = sseEmitterService;
    }
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onTimeout(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onError((e) -> sseEmitterService.removeEmitter(userId, emitter));

        sseEmitterService.addEmitter(userId, emitter);
        return emitter;
    }

}
