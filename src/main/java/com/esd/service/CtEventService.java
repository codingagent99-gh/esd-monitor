package com.esd.service;

import com.esd.model.CtEvent;
import com.esd.repository.CtEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CtEventService {

    private static final Logger log =
            LoggerFactory.getLogger(CtEventService.class);

    @Autowired
    private CtEventRepository ctEventRepository;

    @Autowired
    private EventSocketPublisher socketPublisher;

    public CtEvent save(CtEvent event) {

        log.info(
                "Saving CT event | unitId={} eventTime={}",
                event.getUnitId(),
                event.getEventTime()
        );

        try {

            socketPublisher.publishCtEvent(event);
            log.debug(
                    "CT event persisted | id={}",
                    event.getId()
            );

            // 🔥 Push real-time update

            ctEventRepository.save(event);
            log.info(
                    "CT event published to WebSocket | id={}",
                    event.getId()
            );

            return event;

        } catch (Exception e) {
            log.error(
                    "Failed to save CT event | unitId={} eventTime={}",
                    event.getUnitId(),
                    event.getEventTime(),
                    e
            );
            throw e;
        }
    }
}
