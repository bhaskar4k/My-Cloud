package com.mycloud.common_models.utils;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static String GetAutoDeletionText(Long autoDeleteAt) {

        if (autoDeleteAt == null) {
            return "Never";
        }

        Instant now = Instant.now();
        Instant deletionTime = Instant.ofEpochSecond(autoDeleteAt);

        Duration duration = Duration.between(now, deletionTime);

        if (duration.isNegative() || duration.isZero()) {
            return "Deleted";
        }

        long seconds = duration.getSeconds();

        long days = seconds / 86_400;
        long hours = (seconds % 86_400) / 3_600;
        long minutes = (seconds % 3_600) / 60;

        if (days > 0) {
            return days == 1
                    ? "Deleting in 1 day"
                    : "Deleting in " + days + " days";
        }

        if (hours > 0) {
            return hours == 1
                    ? "Deleting in 1 hour"
                    : "Deleting in " + hours + " hours";
        }

        if (minutes > 0) {
            return minutes == 1
                    ? "Deleting in 1 minute"
                    : "Deleting in " + minutes + " minutes";
        }

        return "Deleting in less than a minute";
    }

    public static DateTimeFormatter DateTimeShortMonthFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    public static DateTimeFormatter DateTimeFullMonthFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
}
