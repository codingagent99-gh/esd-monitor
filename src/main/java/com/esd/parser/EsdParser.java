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
        if (p.length < 2) {
            System.out.println("Malformed line, too few fields: {}"+ line);
            return null;
        }
        try {
            if ("CT".equals(p[1])) {
                if (p.length < 10) { // adjust to actual required length
                    System.out.println("Malformed CT message (expected >=10 fields): {}"+ line);
                    return null;
                }
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
            if ("WM".equals(p[1])) {
                if (p.length < 7) {
                    System.out.println("Malformed WM message (expected >=7 fields): {}"+ line);
                    return null;
                }
                WmEvent wm = new WmEvent();
                wm.setUnitId(Integer.parseInt(p[2]));
                wm.setOperator1(p[3]);
                wm.setOperator2(p[4]);
                wm.setMat1(p[5]);
                wm.setMat2(p[6]);
                wm.setEventTime(LocalDateTime.now());
                return wm;
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Failed to parse numeric field: {}; line={}"+ nfe.getMessage()+ line);
            return null;
        } catch (Exception ex) {
            System.out.println("Unexpected parse error for line={}" + line+ ex);
            return null;
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
