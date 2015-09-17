package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.RelationshipType;

import java.util.HashMap;

import static com.sp.socialcommerce.neo4j.GraphConstants.RelTypes.*;

/**
 * Created by szymon on 17.05.15.
 */
public abstract class GraphConstants {

    public enum RelTypes implements RelationshipType
    {
        LIVES_IN,               /* User -> City */
        WAS_BORN_IN,            /* User -> City */
        FOLLOWS_RELIGION,       /* User -> Religion */
        HAS_POLITICAL_VIEW,     /* User -> PoliticalView */
        LIKES,                  /* User -> Page */
        KNOWS,                  /* User -> User */
        IS_OF_GENDER,           /* User -> Gender */

        HAS_CATEGORY,           /* Product -> ProductCategory */
        RATES,                  /* User -> Product */

        COMMON_FRIEND,          /* User -> User */
    }

    public static final HashMap<RelationshipType, Double> similarityWeights = new HashMap<RelationshipType, Double>() {{
        put(LIVES_IN, 3.0);
        put(WAS_BORN_IN, 3.0);
        put(FOLLOWS_RELIGION, 5.0);
        put(HAS_POLITICAL_VIEW, 5.0);
        put(LIKES, 1.0);
        put(KNOWS, 20.0);
        put(IS_OF_GENDER, 10.0);

        put(COMMON_FRIEND, 1.0);
    }};

    public abstract class User {
        public static final String USER_LABEL = "User";
        public static final String UID = "UID";
        public static final String USER_NAME = "name";
        public static final String USER_PROLONGED_TOKEN = "prolongedToken";
    }

    public abstract class City {
        public static final String CITY_LABEL = "City";
        public static final String CITY_NAME = "name";
    }

    public abstract class Religion {
        public static final String RELIGION_LABEL = "Religion";
        public static final String RELIGION_NAME = "name";
    }
    
    public abstract class PoliticalView {
    	public static final String POLITICAL_VIEW_LABEL = "PoliticalView";
    	public static final String POLITICAL_VIEW_NAME = "name";
    }
    
    public abstract class Page {
        public static final String PAGE_LABEL = "Page";
        public static final String PAGE_ID = "id";
        public static final String PAGE_NAME = "name";
    }
    
    public abstract class PageCategory {
    	public static final String PAGE_CATEGORY_LABEL = "PageCategory";
    	public static final String PAGE_CATEGORY_NAME = "name";
    }

    public abstract class Product {
        public static final String PRODUCT_LABEL = "Product";
        public static final String PRODUCT_NAME_PL = "name_pl";
        public static final String PRODUCT_NAME_EN = "name_en";
        public static final String PRODUCT_DESC_PL = "description_pl";
        public static final String PRODUCT_DESC_EN = "description_en";
        public static final String PRODUCT_IMG_URL = "img_url";
        public static final String PRODUCT_PROD_URL = "product_url";
        public static final String PRODUCT_PRICE_EUR = "price_eur";
        public static final String PRODUCT_ID = "id";
    }

    public abstract class ProductCategory {
        public static final String PRODUCT_CATEGORY_NAME = "name";
    }

    /** Relationship of rating a product by the user */
    public abstract class Rates {
        public static final String RATING_VALUE = "value";
    }

}
