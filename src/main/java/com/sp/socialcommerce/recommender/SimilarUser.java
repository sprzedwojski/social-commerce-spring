package com.sp.socialcommerce.recommender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class SimilarUser implements Comparable {

    private static final Logger logger = LoggerFactory.getLogger(SimilarUser.class);

    /*String userId;*/
    double similaritySum = 0;
    double similarityPercentage = 0.0;

    public void calculateSimilarityPercentage(double max) {
        similarityPercentage = similaritySum*100/max;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof SimilarUser)) {
            logger.error("Cannot compare object other than SimilarUser!");
            return 0;
        }

        // descending order - the most similar on top
        double diff = ((SimilarUser) o).similarityPercentage - this.similarityPercentage;
        if(diff == 0.0)
            return 0;
        else if(diff > 0.0)
            return 1;
        else
            return -1;
        /*return Integer.parseInt(Double.toString(Math.signum(((SimilarUser) o).similarity) - this.similarity));*/
    }
}
