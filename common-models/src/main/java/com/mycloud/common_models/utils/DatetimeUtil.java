package com.mycloud.common_models.utils;
import java.time.Duration;
import java.time.LocalDateTime;

public class DatetimeUtil {
    public static String GetUploadedAgo(LocalDateTime uploadedTime) {

        Duration duration = Duration.between(uploadedTime, LocalDateTime.now());

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Just now";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        long days = hours / 24;
        if (days < 30) {
            return days + (days == 1 ? " day ago" : " days ago");
        }

        long months = days / 30;
        if (months < 12) {
            return months + (months == 1 ? " month ago" : " months ago");
        }

        long years = months / 12;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}
