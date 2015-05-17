package com.sp.socialcommerce.labels;

import com.sp.socialcommerce.neo4j.GraphConstants;
import org.neo4j.graphdb.Label;

/**
 * Created by szymon on 17.05.15.
 */
public class City implements Label {

    @Override
    public String name() {
        return GraphConstants.City.CITY_LABEL;
    }
}
