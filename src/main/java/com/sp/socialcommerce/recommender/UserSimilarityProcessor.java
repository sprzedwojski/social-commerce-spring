package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for finding users existing in the system similar to a new user
 * based on their social media information.
 *
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Async
public class UserSimilarityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UserSimilarityProcessor.class);

    @Autowired
    private GraphDBManager GDBM;

    public List<SimilarUser> findSimilarUsers(String userId, int howMany) {
        List<SimilarUser> similarUsers = new ArrayList<>();

        return similarUsers;
    }

    public List<SimilarUser> sortUsersDescendingWithSimilarity(List<SimilarUser> users) {
        Collections.sort(users);
        return users;
    }



}
