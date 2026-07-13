package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.MarkReadRequestDto;
import com.hh.oneplusplus.dto.NotificationPageResponse;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.UnreadCountResponse;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.exception.NotificationNotFoundException;
import com.hh.oneplusplus.mapper.NotificationGroupMapper;
import com.hh.oneplusplus.repository.NotificationRepository;
import com.hh.oneplusplus.repository.projection.NotificationGroupDetailProjection;
import com.hh.oneplusplus.repository.projection.NotificationGroupSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationQueryService {
    private final NotificationRepository notificationRepository;
    private final SecurityContextService securityContextService;
    private final NotificationGroupMapper notificationGroupMapper;

    public NotificationQueryService(
            NotificationRepository notificationRepository,
            SecurityContextService securityContextService,
            NotificationGroupMapper notificationGroupMapper) {
        this.notificationRepository = notificationRepository;
        this.securityContextService = securityContextService;
        this.notificationGroupMapper = notificationGroupMapper;
    }

    @Transactional(readOnly = true)
    public NotificationPageResponse getNotifications(Pageable pageable) {
        Long userId = securityContextService.getUserId();
        Set<String> eventTypes = NotificationEventType.GROUPABLE_TYPES;
        Page<NotificationGroupSummaryProjection> summaries =
                notificationRepository.findGroupSummaries(userId, eventTypes, pageable);

        Set<String> groupKeys = summaries.getContent().stream()
                .map(NotificationGroupSummaryProjection::getGroupKey)
                .collect(Collectors.toSet());

        long totalUnread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        List<NotificationGroupDetailProjection> details =
                notificationRepository.findGroupDetails(userId, eventTypes, groupKeys);

        Map<String, NotificationGroupDetailProjection> headByGroupKey = details.stream()
                .filter(d -> d.getRn() == 1)
                .collect(Collectors.toMap(NotificationGroupDetailProjection::getGroupKey, Function.identity()));

        List<NotificationResponseDto> result = summaries.getContent().stream()
                .map(s -> notificationGroupMapper.map(s, headByGroupKey.get(s.getGroupKey())))
                .toList();

        Page<NotificationResponseDto> page = new PageImpl<>(result, pageable, summaries.getTotalElements());

        return new NotificationPageResponse(page, totalUnread);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(){
        Long userId = securityContextService.getUserId();
        Long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new UnreadCountResponse(count);
    }

    @Transactional
    public void markSelectedAsRead(MarkReadRequestDto request){
        Long userId = securityContextService.getUserId();
        if(request.ids() == null || request.ids().isEmpty()){
            notificationRepository.markAllAsRead(userId);
        }
        else{
            notificationRepository.markSelectedAsRead(request.ids(), userId);
        }
    }

    @Transactional
    public void deleteNotification(UUID notificationId){
        Long userId = securityContextService.getUserId();
        int deleted = notificationRepository.deleteByNotificationIdAndUserId(notificationId, userId);
        if (deleted == 0) {
            throw new NotificationNotFoundException(notificationId);
        }
    }

    @Transactional
    public void deleteAllNotifications(){
        Long userId = securityContextService.getUserId();
        notificationRepository.deleteByUserId(userId);
    }

}
