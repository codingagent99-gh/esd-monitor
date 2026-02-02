package com.esd.controller;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import com.esd.repository.CtEventRepository;
import com.esd.repository.WmEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    @Autowired
    private CtEventRepository ctRepo;

    @Autowired
    private WmEventRepository wmRepo;

    // ---------- CT EVENTS ----------
    @GetMapping("/ct")
    public Page<CtEvent> getCtEvents(Pageable pageable) {
        System.out.println("hello calling the api");
        return ctRepo.findAll(pageable);
    }

    // ---------- WM EVENTS ----------
    @GetMapping("/wm")
    public Page<WmEvent> getWmEvents(Pageable pageable) {
        return wmRepo.findAll(pageable);
    }
}
