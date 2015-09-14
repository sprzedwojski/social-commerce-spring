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

        /*COMMON_FRIEND,*/          /* User -> User */
        ARE_FRIENDS,            /* User -> User */

        // DEPRECATED
        FAVORITES,              /* User -> Favorite */
        HAS_EDUCATION_LEVEL,    /* User -> EducationLevel */
        IS_OF_TYPE,             /* School -> SchoolType */
        ATTENDED,               /* User -> School */
        WORKED_IN,              /* User -> Work */
        HAS_RELATIONSHIP_STATUS /* User -> RelationshipStatus */
    }

    /*public static final HashMap<RelationshipType, Double> similarityWeights = new HashMap<RelationshipType, Double>() {{
        put(LIVES_IN, 1.0);
        put(WAS_BORN_IN, 1.0);
        put(FOLLOWS_RELIGION, 3.0);
        put(HAS_POLITICAL_VIEW, 3.0);
        put(LIKES, 1.0);
        put(KNOWS, 10.0);
        put(IS_OF_GENDER, 5.0);
    }};*/

    public static final HashMap<RelationshipType, Double> similarityWeights = new HashMap<RelationshipType, Double>() {{
        put(LIVES_IN, 3.0);
        put(WAS_BORN_IN, 3.0);
        put(FOLLOWS_RELIGION, 5.0);
        put(HAS_POLITICAL_VIEW, 5.0);
        put(LIKES, 1.0);
        put(KNOWS, 1.0); // Two users have a common friend
        put(IS_OF_GENDER, 10.0);

        /*put(COMMON_FRIEND, 1.0);*/
        put(ARE_FRIENDS, 20.0); // Two users know each other
    }};

//    public static final String NAME = "name";
    /*public static final String LIKES_KEY = "likes";*/
    public static final String GENERIC_NODE_NAME_PARAM = "name";

    public abstract class User {
        public static final String USER_LABEL = "User";
        public static final String UID = "UID";
        public static final String USER_NAME = "name";
        public static final String USER_NICKNAME = "nickname";
        /*public static final String USER_GIGYA_RESPONSE = "gigya_response";*/
        public static final String USER_PROLONGED_TOKEN = "prolongedToken";
    }

    public abstract class City {
    	/*public static final String CITY_KEY = "city";*/
        public static final String CITY_LABEL = "City";
        public static final String CITY_NAME = "name";
    }

    public abstract class Religion {
    	/*public static final String RELIGION_KEY = "religion";*/
        public static final String RELIGION_LABEL = "Religion";
        public static final String RELIGION_NAME = "name";
    }
    
    public abstract class PoliticalView {
    	/*public static final String POLITICAL_VIEW_KEY = "politicalView";*/
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
    	/*public static final String PAGE_CATEGORY_KEY = "category";*/
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
        /*public static final String PRODUCT_CATEGORY_LABEL = "ProductCategory";*/
        public static final String PRODUCT_CATEGORY_NAME = "name";
    }

    /** Relationship of rating a product by the user */
    public abstract class Rates {
        public static final String RATING_VALUE = "value";
    }



    // DEPRECATED

    public abstract class Education {
        public static final String EDUCATION_KEY = "education";
        public static final String EDUCATION_LABEL = "Education";
        public static final String EDUCATION_NAME = "name";
    }

    public abstract class EducationLevel {
        public static final String EDUCATION_LEVEL_KEY = "educationLevel";
        public static final String EDUCATION_LEVEL_LABEL = "EducationLevel";
        public static final String EDUCATION_LEVEL_NAME = "name";
    }

    public abstract class Work {
        public static final String WORK_KEY = "work";
        public static final String WORK_LABEL = "Work";
        public static final String WORK_NAME = "name";
    }

    public abstract class Favorite {
        public static final String FAVORITES_KEY = "favorites";
        public static final String FAVORITE_LABEL = "Favorite";
        public static final String FAVORITE_ID = "id";
        public static final String FAVORITE_NAME = "name";
    }

    public abstract class FavoriteCategory {
        public static final String FAVORITE_CATEGORY_KEY = "category";
        public static final String FAVORITE_CATEGORY_LABEL = "FavoriteCategory";
        public static final String FAVORITE_CATEGORY_NAME = "name";
    }

    public abstract class Hometown {
        /*public static final String HOMETOWN_KEY = "hometown";*/
        public static final String HOMETOWN_LABEL = "Hometown";
        public static final String HOMETOWN_NAME = "name";
    }

}
