package com.hh.oneplusplus.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.exception.TemplateNotFoundException;
import com.hh.oneplusplus.repository.MessageTemplateRepository;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageResolverService {
    private final MessageTemplateRepository messageTemplateRepository;
    private final Cache<String, String> templateCache;
    private final ObjectMapper objectMapper;

    public MessageResolverService(
            MessageTemplateRepository messageTemplateRepository,
            Cache<String, String> templateCache,
            ObjectMapper objectMapper) {
        this.messageTemplateRepository = messageTemplateRepository;
        this.templateCache = templateCache;
        this.objectMapper = objectMapper;
    }

    public String resolveMessage(NotificationEvent event){
        String templateKey = event.getEventType().name() + "_" + event.getType().name();

        String template = templateCache.get(
                templateKey,
                key -> messageTemplateRepository
                        .findByEventTypeAndChannel(event.getEventType(), event.getType())
                        .orElseThrow(() -> new TemplateNotFoundException(key))
                        .getTemplate()
        );

        Map<String, Object> fields = objectMapper.convertValue(event, Map.class);
        Map<String, Object> flatFields = new HashMap<>();
        flatten("", fields, flatFields);

        return StringSubstitutor.replace(template, flatFields);
    }

    public String resolveGroupMessage(NotificationEventType eventType, Integer groupCount, Map<String, Object> params){
        String eventTypeGroup = eventType.name() + ":group";
        String template = templateCache.get(
                eventTypeGroup,
                key -> messageTemplateRepository
                        .findByEventType(eventType)
                        .orElseThrow(() -> new TemplateNotFoundException(key))
                        .getGroupTemplate()
        );
        int others = groupCount - 1;

        Map<String, Object> fields = Map.of(
                "params", params,
                "othersCount", others,
                "othersWord", pluralForm(others)
        );
        Map<String, Object> flatFields = new HashMap<>();
        flatten("", fields, flatFields);
        return StringSubstitutor.replace(template, flatFields);
    }
    private void flatten(String prefix, Map<String, Object> source, Map<String, Object> result){
        for(Map.Entry<String, Object> entry: source.entrySet()){
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if(entry.getValue() instanceof  Map){
                flatten(key, (Map<String, Object>) entry.getValue(), result);
            }
            else{
                result.put(key, entry.getValue());
            }
        }
    }
    private String pluralForm(int n) {
        int mod100 = n % 100;
        int mod10 = mod100 % 10;
        if (mod100 > 10 && mod100 < 20){
            return "человек";
        }
        if (mod10 > 1 && mod10 < 5){
            return "человека";
        }
        if (mod10 == 1){
            return "человек";
        }
        return "человек";
    }
}
