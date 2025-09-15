package com.example.backend.service;

import com.example.backend.dto.NotificationDTO;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private JavaMailSender mailSender;
    private NotificationWebSocketHandler webSocketHandler;
    
    public NotificationDTO sendNotification(Long userId, String title, String message, 
                                           String type, Long relatedId) {
        log.info("Sending notification to user: {}", userId);
        
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .notificationType(Notification.NotificationType.valueOf(type))
                .relatedId(relatedId)
                .isRead(false)
                .priority(determinePriority(type))
                .sentVia(Notification.SentVia.APP)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        if (webSocketHandler != null) {
            webSocketHandler.sendNotificationToUser(userId, convertToDTO(savedNotification));
        }
        
        // Send email notification if enabled
        if (shouldSendEmail(type)) {
            sendEmailNotification(recipient.getEmail(), title, message);
        }
        
        log.info("Notification sent successfully with ID: {}", savedNotification.getId());
        
        return convertToDTO(savedNotification);
    }
    
    @Async
    public void sendEmailNotification(String email, String subject, String body) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("noreply@sahaay.com");
                
                mailSender.send(message);
                log.info("Email sent successfully to: {}", email);
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
        }
    }
    
    @Async
    public void sendSMSNotification(String phone, String message) {
        // Implement Twilio SMS integration here
        log.info("SMS notification would be sent to: {}", phone);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);
        
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findUnreadNotificationsByUserId(userId);
        
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
        
        log.info("Notification {} marked as read", notificationId);
    }
    
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("Notification {} deleted", notificationId);
    }
    
    public void cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository
                .findExpiredUnreadNotifications(LocalDateTime.now());
        
        for (Notification notification : expiredNotifications) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
        
        log.info("Cleaned up {} expired notifications", expiredNotifications.size());
    }
    
    private Notification.Priority determinePriority(String type) {
        return switch (type) {
            case "SOS_ALERT" -> Notification.Priority.CRITICAL;
            case "BLOOD_REQUEST" -> Notification.Priority.HIGH;
            case "HELP_REQUEST" -> Notification.Priority.MEDIUM;
            default -> Notification.Priority.LOW;
        };
    }
    
    private boolean shouldSendEmail(String type) {
        return type.equals("SOS_ALERT") || type.equals("BLOOD_REQUEST");
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipient().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType().name())
                .relatedId(notification.getRelatedId())
                .isRead(notification.getIsRead())
                .priority(notification.getPriority().name())
                .actionRequired(notification.getActionRequired())
                .actionUrl(notification.getActionUrl())
                .expiresAt(notification.getExpiresAt())
                .sentVia(notification.getSentVia().name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}