package com.esd.controller;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import com.esd.repository.CtEventRepository;
import com.esd.repository.WmEventRepository;
import com.esd.service.CtEventService;
import com.esd.service.WmEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private static final Logger log =
            LoggerFactory.getLogger(EventController.class);

    // Repositories (READ)
    @Autowired
    private CtEventRepository ctRepo;

    @Autowired
    private WmEventRepository wmRepo;

    // Services (WRITE + WebSocket)
    @Autowired
    private CtEventService ctEventService;

    @Autowired
    private WmEventService wmEventService;

    // ---------- CT EVENTS ----------
    @GetMapping("/ct")
    public Page<CtEvent> getCtEvents(Pageable pageable) {

        log.info(
                "Fetching CT events | page={} size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<CtEvent> result = ctRepo.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "eventTime")
                )
        );

        log.info("CT events fetched | totalElements={}", result.getTotalElements());
        return result;
    }

    @PostMapping("/ct")
    public CtEvent createCtEvent(@RequestBody CtEvent event) {

        log.info(
                "Creating CT event | unitId={} eventTime={}",
                event.getUnitId(),
                event.getEventTime()
        );

        CtEvent saved = ctEventService.save(event);

        log.debug("CT event saved successfully | id={}", saved.getId());
        return saved;
    }

    // ---------- WM EVENTS ----------
    @GetMapping("/wm")
    public Page<WmEvent> getWmEvents(Pageable pageable) {

        log.info(
                "Fetching WM events | page={} size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<WmEvent> result = wmRepo.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "eventTime")
                )
        );

        log.info("WM events fetched | totalElements={}", result.getTotalElements());
        return result;
    }

    @PostMapping("/wm")
    public WmEvent createWmEvent(@RequestBody WmEvent event) {

        log.info(
                "Creating WM event | deviceId={} eventTime={}",
                event.getEventTime()
        );

        WmEvent saved = wmEventService.save(event);

        log.debug("WM event saved successfully | id={}", saved.getId());
        return saved;
    }
    // In EventController.java, add these methods:

    @GetMapping("/ct/latest")
    public ResponseEntity<List<CtEvent>> getLatestCtEvents(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long since) {

        Page<CtEvent> page = ctRepo.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "eventTime"))
        );

        List<CtEvent> events = page.getContent();

        if (since != null) {
            events = events.stream()
                    .filter(e ->
                            e.getEventTime()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli() > since
                    )
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.SECONDS))
                .eTag(String.valueOf(events.hashCode()))
                .body(events);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }


}
