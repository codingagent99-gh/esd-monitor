package com.esd.service;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventSocketPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(EventSocketPublisher.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Push CT event to frontend
    public void publishCtEvent(CtEvent event) {

        log.debug(
                "Publishing CT event to WebSocket | unitId={} eventTime={}",
                event.getUnitId(),
                event.getEventTime()
        );

        messagingTemplate.convertAndSend("/topic/ct-events", event);

        log.info(
                "CT event sent to /topic/ct-events | unitId={}",
                event.getUnitId()
        );
    }

    // Push WM event to frontend
    public void publishWmEvent(WmEvent event) {

        log.debug(
                "Publishing WM event to WebSocket | unitId={} eventTime={}",
                event.getUnitId(),
                event.getEventTime()
        );

        messagingTemplate.convertAndSend("/topic/wm-events", event);

        log.info(
                "WM event sent to /topic/wm-events | unitId={}",
                event.getUnitId()
        );
    }
}
