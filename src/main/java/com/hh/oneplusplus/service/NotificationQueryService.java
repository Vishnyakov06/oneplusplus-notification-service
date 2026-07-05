package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.MarkReadRequestDto;
import com.hh.oneplusplus.dto.NotificationPageResponse;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.UnreadCountResponse;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.exception.NotificationNotFoundException;
import com.hh.oneplusplus.mapper.NotificationGroupMapper;
import com.hh.oneplusplus.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

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
        Set<String> groupableTypes = NotificationEventType.GROUPABLE_TYPES;
        Page<NotificationResponseDto> responseDto = notificationRepository.findGroupedPage(userId, groupableTypes, pageable)
                .map(notificationGroupMapper::map);
        long totalUnread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new NotificationPageResponse(responseDto, totalUnread);
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
