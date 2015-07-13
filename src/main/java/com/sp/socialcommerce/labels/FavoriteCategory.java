package com.sp.socialcommerce.labels;

import com.sp.socialcommerce.neo4j.GraphConstants;
import org.neo4j.graphdb.Label;

/**
 * Created by szymon on 11.07.15.
 */
public class FavoriteCategory implements Label {
    @Override
    public String name() {
        return GraphConstants.FavoriteCategory.FAVORITE_CATEGORY_LABEL;
    }
}
