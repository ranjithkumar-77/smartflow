package com.smartflow.smartflow.util;

public class MatchingScoreUtil {

    public static double calculateScore(
            double distanceKm,
            double rating,
            int currentJobs) {

        // Distance score
        double distanceScore;

        if (distanceKm <= 2) {
            distanceScore = 100;
        } else if (distanceKm <= 5) {
            distanceScore = 80;
        } else if (distanceKm <= 10) {
            distanceScore = 60;
        } else if (distanceKm <= 20) {
            distanceScore = 40;
        } else {
            distanceScore = 20;
        }

        // Rating score
        double ratingScore = (rating / 5.0) * 100;

        // Workload score
        double workloadScore;

        if (currentJobs == 0) {
            workloadScore = 100;
        } else if (currentJobs == 1) {
            workloadScore = 80;
        } else if (currentJobs == 2) {
            workloadScore = 60;
        } else if (currentJobs == 3) {
            workloadScore = 40;
        } else {
            workloadScore = 20;
        }

        // Final weighted score
        double finalScore =
                (distanceScore * 0.25)
                + (ratingScore * 0.10)
                + (workloadScore * 0.05);

        return finalScore;
    }
}