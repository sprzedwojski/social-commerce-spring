package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.RelationshipType;

/**
 * Created by szymon on 17.05.15.
 */
public abstract class GraphConstants {

    public static enum RelTypes implements RelationshipType
    {
        LIVES_IN, FOLLOWS_RELIGION, HAS_POLITICAL_VIEW, LIKES, KNOWS, HAS_CATEGORY
    }

//    public static final String NAME = "name";
    public static final String LIKES_KEY = "likes";
    
    public abstract class User {
        public static final String USER_LABEL = "User";
        public static final String UID = "UID";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String USER_NAME = "name";
    }

    public abstract class City {
    	public static final String CITY_KEY = "city";
        public static final String CITY_LABEL = "City";
        public static final String CITY_NAME = "name";
    }

    public abstract class Religion {
    	public static final String RELIGION_KEY = "religion";
        public static final String RELIGION_LABEL = "Religion";
        public static final String RELIGION_NAME = "name";
    }
    
    public abstract class PoliticalView {
    	public static final String POLITICAL_VIEW_KEY = "politicalView";
    	public static final String POLITICAL_VIEW_LABEL = "PoliticalView";
    	public static final String POLITICAL_VIEW_NAME = "name";
    }
    
    public abstract class Page {
//    	public static final String PAGE_KEY = "likes";
        public static final String PAGE_LABEL = "Page";
        public static final String PAGE_ID = "id";
        public static final String PAGE_NAME = "name";
    }
    
    public abstract class PageCategory {
    	public static final String PAGE_CATEGORY_KEY = "category";
    	public static final String PAGE_CATEGORY_LABEL = "PageCategory";
    	public static final String PAGE_CATEGORY_NAME = "name";
    }

    public abstract class Product {
        public static final String PRODUCT_LABEL = "Product";
        public static final String PRODUCT_NAME = "name";
    }

}
