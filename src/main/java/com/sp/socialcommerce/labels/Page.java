package com.sp.socialcommerce.labels;

import org.neo4j.graphdb.Label;

import com.sp.socialcommerce.neo4j.GraphConstants;

public class Page implements Label {

	@Override
	public String name() {
		return GraphConstants.Page.PAGE_LABEL;
	}

}
