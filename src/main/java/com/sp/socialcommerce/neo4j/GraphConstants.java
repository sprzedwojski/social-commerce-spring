package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.RelationshipType;

/**
 * Created by szymon on 17.05.15.
 */
public abstract class GraphConstants {

    public static enum RelTypes implements RelationshipType
    {
        LIVES_IN, FOLLOWS_RELIGION
    }

    public abstract class User {
        public static final String USER_LABEL = "User";
        public static final String UID = "UID";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
    }

    public abstract class City {
        public static final String CITY_LABEL = "City";
        public static final String CITY_NAME = "cityName";
    }

    public abstract class Religion {
        public static final String RELIGION_LABEL = "Religion";
        public static final String RELIGION_NAME = "religionName";
    }

}
