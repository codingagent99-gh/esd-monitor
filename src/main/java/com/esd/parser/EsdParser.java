package com.esd.parser;

import com.esd.model.CtEvent;
import com.esd.model.WmEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EsdParser {

    public Object parse(String line) {

        if (line == null || !line.startsWith("$$$$")) {
            return null;
        }

        String[] p = line.split(",");

        // Combo Tester (CT)
        if ("CT".equals(p[1])) {
            CtEvent ct = new CtEvent();
            ct.setUnitId(Integer.parseInt(p[2]));
            ct.setCardNumber(p[3]);
            ct.setLeftFootOhms(parseOhms(p[4]));
            ct.setRightFootOhms(parseOhms(p[5]));
            ct.setFootResult(p[6]);
            ct.setWristValue(p[7]);
            ct.setWristResult(p[8]);
            ct.setEntryExit(p[9]);
            ct.setEventTime(LocalDateTime.now());
            return ct;
        }

        // Workstation Monitor (WM)
        if ("WM".equals(p[1])) {
            WmEvent wm = new WmEvent();
            wm.setUnitId(Integer.parseInt(p[2]));
            wm.setOperator1(p[3]);
            wm.setOperator2(p[4]);
            wm.setMat1(p[5]);
            wm.setMat2(p[6]);
            wm.setEventTime(LocalDateTime.now());
            return wm;
        }

        return null;
    }

    private double parseOhms(String value) {
        if (value.endsWith("M")) {
            return Double.parseDouble(value.replace("M", "")) * 1_000_000;
        }
        if (value.endsWith("K")) {
            return Double.parseDouble(value.replace("K", "")) * 1_000;
        }
        return 0;
    }
}
