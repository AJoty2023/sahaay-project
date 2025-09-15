package com.example.backend.websocket;
import com.example.backend.dto.SOSAlertDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SOSWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Broadcast SOS alert to all connected clients
     */
    public void broadcastSOSAlert(SOSAlertDTO sosAlert) {
        try {
            log.info("Broadcasting SOS alert: {}", sosAlert.getId());
            messagingTemplate.convertAndSend("/topic/sos-alerts", sosAlert);
        } catch (Exception e) {
            log.error("Error broadcasting SOS alert", e);
        }
    }
    
    /**
     * Send SOS alert to specific users within radius
     */
    public void sendSOSAlertToNearbyUsers(SOSAlertDTO sosAlert, Long userId) {
        try {
            log.info("Sending SOS alert to user: {}", userId);
            messagingTemplate.convertAndSendToUser(
                userId.toString(), 
                "/queue/sos-alerts", 
                sosAlert
            );
        } catch (Exception e) {
            log.error("Error sending SOS alert to user: {}", userId, e);
        }
    }
    
    /**
     * Send SOS response notification
     */
    public void sendSOSResponse(Long alertId, Long responderId, String responderName) {
        try {
            SOSResponse response = new SOSResponse(alertId, responderId, responderName);
            log.info("Sending SOS response for alert: {}", alertId);
            messagingTemplate.convertAndSend("/topic/sos-responses", response);
        } catch (Exception e) {
            log.error("Error sending SOS response", e);
        }
    }
    
    /**
     * Send SOS resolution notification
     */
    public void sendSOSResolution(Long alertId) {
        try {
            log.info("Sending SOS resolution for alert: {}", alertId);
            messagingTemplate.convertAndSend(
                "/topic/sos-resolutions", 
                new SOSResolution(alertId)
            );
        } catch (Exception e) {
            log.error("Error sending SOS resolution", e);
        }
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SOSResponse {
        private Long alertId;
        private Long responderId;
        private String responderName;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SOSResolution {
        private Long alertId;
    }
}

