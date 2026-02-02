package com.esd.service;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import com.esd.parser.EsdParser;
import com.esd.repository.CtEventRepository;
import com.esd.repository.WmEventRepository;
import com.esd.util.FileFallbackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EsdService {

    @Autowired
    private EsdParser parser;

    @Autowired
    private CtEventRepository ctRepository;

    @Autowired
    private WmEventRepository wmRepository;

    @Transactional
    public void process(String msg) {
        System.out.println("jdhdkfhskjhfkjdshf");
        try {
            Object event = parser.parse(msg);

            if (event == null) {
                System.err.println("Invalid message skipped: " + msg);
                return;
            }

            if (event instanceof CtEvent ct) {
                ct.setEventTime(LocalDateTime.now());
                ctRepository.save(ct);
                System.out.println("CT inserted into DB");

            } else if (event instanceof WmEvent wm) {
                wm.setEventTime(LocalDateTime.now());
                wmRepository.save(wm);
                System.out.println("WM inserted into DB");
            }

        } catch (Exception ex) {
            // DB failure → write to local file
//            FileFallbackUtil.write(msg);
            System.err.println("DB insert failed, written to file");
            ex.printStackTrace();
        }
    }
}
