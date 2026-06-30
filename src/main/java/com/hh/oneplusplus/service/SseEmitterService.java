package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterService {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Value("${app.sse.max-tabs-per-user:7}")
    private int maxTabsPerUser;

    @Value("${app.sse.reconnect-time-ms:10000}")
    private long reconnectTimeMs;

    public void addEmitter(Long userId, SseEmitter emitter){
        List<SseEmitter> userEmitters = emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());

        if(isLimitExceeded(userId)){
            SseEmitter oldEmitter = userEmitters.getFirst();
            userEmitters.remove(oldEmitter);
            oldEmitter.complete();
        }
        userEmitters.add(emitter);

        try{
            emitter.send(SseEmitter.event()
                    .name("init")
                    .reconnectTime(reconnectTimeMs)
                    .data("Connected"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }
    }

    public void removeEmitter(Long userId, SseEmitter emitter){
        List<SseEmitter> userEmitters = emitters.get(userId);
        if(userEmitters != null){
            userEmitters.remove(emitter);
            if(userEmitters.isEmpty()){
                emitters.remove(userId);
            }
        }
    }

    public void sendToUser(Long userId, NotificationResponseDto responseDto){
        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }

        userEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(responseDto));
            } catch (IOException e) {
                emitter.completeWithError(e);
                removeEmitter(userId, emitter);
            }
        });
    }

    @Scheduled(fixedDelayString = "${app.sse.heartbeat-delay-ms:20000}")
    public void sendPing(){
        emitters.forEach((userId, userEmitter) -> {
            userEmitter.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().comment("ping"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    removeEmitter(userId, emitter);
                }
                catch (IllegalStateException e) {
                    removeEmitter(userId, emitter);
                }
            });
        });
    }

    private boolean isLimitExceeded(Long userId) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        return userEmitters != null && userEmitters.size() >= maxTabsPerUser;
    }
}
