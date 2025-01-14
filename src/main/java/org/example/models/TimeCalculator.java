package org.example.models;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeCalculator {
    long startTime;
    long endTime;
    long durationInNano;

    public TimeCalculator() {
    }

    public void startTimer(){
        startTime = System.nanoTime();
    }

    public List<Long> endTimer() {
        endTime = System.nanoTime();
        durationInNano = endTime - startTime;

        long totalSeconds = TimeUnit.NANOSECONDS.toSeconds(durationInNano);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return new ArrayList<>(Arrays.asList(hours, minutes, seconds));
    }

    public List<Long> calculateEstimatedTime(int processedMovies, int totalMovies) {
        long estimatedInNano = (durationInNano * totalMovies) / processedMovies;
        long timeLeft = estimatedInNano - durationInNano;

        long totalSeconds = TimeUnit.NANOSECONDS.toSeconds(timeLeft);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return new ArrayList<>(Arrays.asList(hours, minutes, seconds));
    }
}
