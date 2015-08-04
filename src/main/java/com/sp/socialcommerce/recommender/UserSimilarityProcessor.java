package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;

import java.util.*;

/**
 * This class is responsible for finding users existing in the system similar to a new user
 * based on their social media information.
 *
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Async
@Scope("session")
public class UserSimilarityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UserSimilarityProcessor.class);

    @Autowired
    private GraphDBManager GDBM;

    private Map<String, SimilarUser> similarUsersMap = new HashMap<>();

    public List<SimilarUser> findSimilarUsers(String userId, int howMany) {
        List<SimilarUser> similarUsers = new ArrayList<>();

        // TODO

        return sortUsersDescendingWithSimilarity(similarUsers);
    }

    public void processUserFriends(List<String> friendsIdList, Map<String, SimilarUser> map) {
        if(!GraphConstants.similarityWeights.containsKey(GraphConstants.RelTypes.KNOWS)) {
            logger.error("No similarity weights for key " + GraphConstants.RelTypes.KNOWS.toString());
            return;
        }
        double similarityWeight = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.KNOWS);

        for(String friendId : friendsIdList) {
            SimilarUser similarUser = createSimilarUserIfNotExistsAndPutIntoMap(map, friendId);
            similarUser.similaritySum += similarityWeight;
        }
    }

    public SimilarUser createSimilarUserIfNotExistsAndPutIntoMap(Map<String, SimilarUser> map, String id) {
        SimilarUser similarUser;
        if(map.containsKey(id)) {
            similarUser = map.get(id);
        } else {
            similarUser = new SimilarUser();
            map.put(id, similarUser);
        }
        return similarUser;
    }

    public List<SimilarUser> sortUsersDescendingWithSimilarity(List<SimilarUser> users) {
        Collections.sort(users);
        return users;
    }



}
