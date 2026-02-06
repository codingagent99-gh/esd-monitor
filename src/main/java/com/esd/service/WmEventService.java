package com.esd.service;

import com.esd.model.WmEvent;
import com.esd.repository.WmEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WmEventService {

    @Autowired
    private WmEventRepository wmEventRepository;

    @Autowired
    private EventSocketPublisher socketPublisher;

    public WmEvent save(WmEvent event) {


        // 🔥 Push real-time update
        socketPublisher.publishWmEvent(event);
        wmEventRepository.save(event);

        return event;
    }
}
