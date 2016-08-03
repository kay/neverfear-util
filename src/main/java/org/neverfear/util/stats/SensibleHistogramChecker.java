package org.neverfear.util.stats;

public class SensibleHistogramChecker {
    /**
     * May be used to check that a histogram is sensible
     * @param totalRuntime
     * @param maxObserved
     * @param percentileKeyF
     * @param percentileValue
     */
    public static void saneResults(long totalRuntime, long maxObserved, double percentileKey, long percentileValue) {
        // Percentage of the total runtime that max represents
        double maxRepresents = ((double) maxObserved / totalRuntime) * 100.0;
        double thusPercentileRepresents = (100.0 - percentileKey);
        // I'm blissfully ignoring rounding boundary conditions here
        double minValueOfPercentile = totalRuntime * (maxRepresents - thusPercentileRepresents);
        System.out.println(minValueOfPercentile);
        if (percentileValue < minValueOfPercentile) {
            throw new IllegalStateException("Min value for percentile is " + minValueOfPercentile);
        }
    }

    public static void main(String[] args) {
        saneResults(2028755, 26182, 99.0, 5);
    }
}
