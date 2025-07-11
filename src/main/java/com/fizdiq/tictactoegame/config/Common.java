package com.fizdiq.tictactoegame.config;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Common {

    public static final String EXCEPTION = "EXCEPTION: {}";

    public static final String AT = " at ";

    private Common() {
    }

    public static void errorLog(Exception e) {
        log.error(EXCEPTION, e + AT + e.getStackTrace()[0].toString());
    }

    public static String getElapsedTime(long stopWatchMillis) {
        Duration duration = Duration.ofMillis(stopWatchMillis);
        String elapsedTime;
        if (duration != null && !duration.isZero() && !duration.isNegative()) {
            StringBuilder formattedDuration = new StringBuilder().append("'");
            long hours = duration.toHours();
            long minutes = duration.toMinutes();
            long seconds = duration.getSeconds();
            long millis = duration.toMillis();
            if (hours != 0L) {
                formattedDuration.append(hours).append("h ");
            }

            if (minutes != 0L) {
                formattedDuration.append(minutes - TimeUnit.HOURS.toMinutes(hours)).append("m ");
            }

            if (seconds != 0L) {
                formattedDuration.append(seconds - TimeUnit.MINUTES.toSeconds(minutes)).append("s ");
            }

            if (millis != 0L) {
                formattedDuration.append(millis - TimeUnit.SECONDS.toMillis(seconds)).append("ms");
            }

            elapsedTime = formattedDuration.append("'").toString();
        } else {
            elapsedTime = "'0ms'";
        }
        return elapsedTime;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
