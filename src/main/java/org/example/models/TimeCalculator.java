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

    /**
     * Starts a timer and saves the start in startTime
     */
    public void startTimer(){
        startTime = System.nanoTime();
    }

    /**
     * Ends the timer and calculates the hours, minutes and seconds passed after running startTimer
     * @return: List<Long> of the hours, minutes and seconds passed
     */
    public List<Long> endTimer() {
        endTime = System.nanoTime();
        durationInNano = endTime - startTime;

        long totalSeconds = TimeUnit.NANOSECONDS.toSeconds(durationInNano);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return new ArrayList<>(Arrays.asList(hours, minutes, seconds));
    }

    /**
     * Calculates the estimated time left for the extraction process to end
     * @param processedMovies: count of processed movies so far
     * @param totalMovies: count of total movies to process
     * @return: List<Long> of the hours, minutes and seconds left (estimated)
     */
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
