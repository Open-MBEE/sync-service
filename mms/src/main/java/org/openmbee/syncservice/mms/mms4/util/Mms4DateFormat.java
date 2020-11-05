package org.openmbee.syncservice.mms.mms4.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Mms4DateFormat extends SimpleDateFormat {
    public Mms4DateFormat() {
        super("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
