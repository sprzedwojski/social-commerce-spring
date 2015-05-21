package com.sp.socialcommerce.labels;

import com.sp.socialcommerce.neo4j.GraphConstants;
import org.neo4j.graphdb.Label;

/**
 * Created by szymon on 21.05.15.
 */
public class PoliticalView implements Label {

    @Override
    public String name() {
        return GraphConstants.PoliticalView.POLITICAL_VIEW_LABEL;
    }
}
