package com.example.backend.websocket;

import com.example.backend.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        try {
            log.info("Sending notification to user: {}", userId);
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
            );
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", userId, e);
        }
    }
    
    /**
     * Broadcast notification to all users
     */
    public void broadcastNotification(NotificationDTO notification) {
        try {
            log.info("Broadcasting notification: {}", notification.getTitle());
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        } catch (Exception e) {
            log.error("Error broadcasting notification", e);
        }
    }
    
    /**
     * Send notification count update
     */
    public void sendNotificationCount(Long userId, int unreadCount) {
        try {
            Map<String, Object> countUpdate = new HashMap<>();
            countUpdate.put("userId", userId);
            countUpdate.put("unreadCount", unreadCount);
            
            log.info("Sending notification count to user {}: {}", userId, unreadCount);
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-count",
                countUpdate
            );
        } catch (Exception e) {
            log.error("Error sending notification count to user: {}", userId, e);
        }
    }
    
    /**
     * Send real-time help request update
     */
    public void sendHelpRequestUpdate(Long requestId, String status) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("requestId", requestId);
            update.put("status", status);
            update.put("timestamp", System.currentTimeMillis());
            
            log.info("Sending help request update for request: {}", requestId);
            messagingTemplate.convertAndSend("/topic/help-requests", update);
        } catch (Exception e) {
            log.error("Error sending help request update", e);
        }
    }
    
    /**
     * Send blood request urgent notification
     */
    public void sendUrgentBloodRequest(Long requestId, String bloodType, String hospitalName) {
        try {
            Map<String, Object> urgentRequest = new HashMap<>();
            urgentRequest.put("requestId", requestId);
            urgentRequest.put("bloodType", bloodType);
            urgentRequest.put("hospitalName", hospitalName);
            urgentRequest.put("urgent", true);
            
            log.info("Sending urgent blood request notification: {}", requestId);
            messagingTemplate.convertAndSend("/topic/blood-requests/urgent", urgentRequest);
        } catch (Exception e) {
            log.error("Error sending urgent blood request", e);
        }
    }
    
    /**
     * Send volunteer assignment notification
     */
    public void sendVolunteerAssignment(Long volunteerId, Long requestId, String requestTitle) {
        try {
            Map<String, Object> assignment = new HashMap<>();
            assignment.put("requestId", requestId);
            assignment.put("requestTitle", requestTitle);
            assignment.put("timestamp", System.currentTimeMillis());
            
            log.info("Sending assignment notification to volunteer: {}", volunteerId);
            messagingTemplate.convertAndSendToUser(
                volunteerId.toString(),
                "/queue/assignments",
                assignment
            );
        } catch (Exception e) {
            log.error("Error sending assignment notification", e);
        }
    }
}
