package com.sp.socialcommerce.labels;

import org.neo4j.graphdb.Label;

import com.sp.socialcommerce.neo4j.GraphConstants;

public class PageCategory implements Label {

	@Override
	public String name() {
		return GraphConstants.PageCategory.PAGE_CATEGORY_LABEL;
	}

}
