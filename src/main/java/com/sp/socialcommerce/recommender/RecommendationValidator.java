package com.sp.socialcommerce.recommender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class RecommendationValidator {

//    private final static double MAX_POSSIBLE_RATING = 5.0;
    private final static double MAX_POSSIBLE_DIFF = 4.0;
    private List<Double[]> realAndSuggestedRatings = new ArrayList<>();

    /**
     *
     * @param real
     * @param suggested
     * @return The discrepancy between the 2 ratings.
     */
    public double addRatings(double real, double suggested) {
        double individualCorrectness = calculateIndividualCorrectness(real, suggested);
        realAndSuggestedRatings.add(new Double[]{real, suggested, individualCorrectness});
        return individualCorrectness;
    }

    /**
     *
     * @return Correctness value between 0 (no correctness whatsoever) and 1 (full correctness).
     */
    public double getRecommendationCorrectness() {
        double correctnessFinal = 0.0;
        List<Double> correctnessList = new ArrayList<>();

        for(Double[] realAndSuggestedRating : realAndSuggestedRatings) {
            correctnessFinal += realAndSuggestedRating[2];
        }

        correctnessFinal /= realAndSuggestedRatings.size();

        return correctnessFinal;
    }

    private double calculateIndividualCorrectness(double var1, double var2) {
        return (MAX_POSSIBLE_DIFF - Math.abs(var1 - var2)) / MAX_POSSIBLE_DIFF;
    }

}

