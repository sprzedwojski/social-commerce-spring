package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * This class is responsible for finding users existing in the system similar to a new user
 * based on their social media information.
 *
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
/*@Async
@Scope("session")*/
@Service
public class UserSimilarityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UserSimilarityProcessor.class);

    @Autowired
    private GraphDBManager GDBM;

    private Map<String, SimilarUser> similarUsersMap = new LinkedHashMap<>();

    public List<SimilarUser> findSimilarUsers(String userId, int howMany) {
        List<SimilarUser> similarUsersSortedList = new ArrayList<>(howMany);

        processCommonMap(GDBM.getOtherUsersWithRelationship(GDBM.getUserNode(userId)), similarUsersMap);
        similarUsersMap = sortUsersDescendingWithSimilarity(similarUsersMap);

        int counter = 0;
        for(Map.Entry<String, SimilarUser> entry : similarUsersMap.entrySet()) {
            if(counter <= howMany) {
                counter++;
                SimilarUser user = entry.getValue();
                user.userId = entry.getKey();
                similarUsersSortedList.add(user);
            } else break;
        }

        return similarUsersSortedList;
        /*return sortUsersDescendingWithSimilarity(similarUsersMap);*/
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

    public void processCommonMap(Map<String, Map<String, AtomicInteger>> commonMap, Map<String, SimilarUser> similarUsersMap) {
        commonMap.forEach((userId, relMap) -> {
            SimilarUser user = createSimilarUserIfNotExistsAndPutIntoMap(similarUsersMap, userId);
            relMap.forEach((relName, count) -> {
                double similarityWeight = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.valueOf(relName));
                user.similaritySum += similarityWeight * count.intValue();
            });
        });
    }

    public void processUserLikes(Map<String, Integer> numberOfCommonLikes, Map<String, SimilarUser> map) {
        if(!GraphConstants.similarityWeights.containsKey(GraphConstants.RelTypes.LIKES)) {
            logger.error("No similarity weights for key " + GraphConstants.RelTypes.LIKES.toString());
            return;
        }
        double similarityWeight = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.LIKES);

        numberOfCommonLikes.forEach((x,y) -> createSimilarUserIfNotExistsAndPutIntoMap(map, x).similaritySum += similarityWeight*y);
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

    public List<SimilarUser> sortUsersDescendingWithSimilarityPercentage(List<SimilarUser> users) {
        Collections.sort(users);
        return users;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortUsersDescendingWithSimilarity( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Comparator.comparing(e -> ((SimilarUser)e.getValue()).similaritySum)))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }



}
