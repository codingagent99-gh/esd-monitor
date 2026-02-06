package com.esd.service;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import com.esd.parser.EsdParser;
import com.esd.repository.CtEventRepository;
import com.esd.repository.WmEventRepository;
import com.esd.util.FileFallbackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EsdService {

    private static final Logger log =
            LoggerFactory.getLogger(EsdService.class);

    @Autowired
    private EsdParser parser;

    @Autowired
    private CtEventRepository ctRepository;

    @Autowired
    private WmEventRepository wmRepository;

    @Transactional
    public void process(String msg) {

        // Safe fingerprint instead of full payload
        int msgLength = msg != null ? msg.length() : 0;
        int msgHash = msg != null ? msg.hashCode() : 0;

        log.debug(
                "Processing incoming message | length={} hash={}",
                msgLength, msgHash
        );

        try {
            Object event = parser.parse(msg);

            if (event == null) {
                log.warn(
                        "Parser returned null | length={} hash={}",
                        msgLength, msgHash
                );
                return;
            }

            if (event instanceof CtEvent ct) {

                ct.setEventTime(LocalDateTime.now());
                ctRepository.save(ct);

                log.info(
                        "CT event persisted | unitId={} card={} eventTime={}",
                        ct.getUnitId(),
                        ct.getCardNumber(),
                        ct.getEventTime()
                );

            } else if (event instanceof WmEvent wm) {

                wm.setEventTime(LocalDateTime.now());
                wmRepository.save(wm);

                log.info(
                        "WM event persisted | unitId={} eventTime={}",
                        wm.getUnitId(),
                        wm.getEventTime()
                );

            } else {
                log.warn(
                        "Unknown event type | class={} length={} hash={}",
                        event.getClass().getName(),
                        msgLength,
                        msgHash
                );
            }

        } catch (Exception ex) {

            // Transaction will roll back automatically
            log.error(
                    "DB insert failed, writing to fallback | length={} hash={}",
                    msgLength,
                    msgHash,
                    ex
            );

            FileFallbackUtil.write(msg);
        }
    }
}
