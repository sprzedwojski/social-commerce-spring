package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.*;
import com.sp.socialcommerce.labels.User;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class UserSimilarityProcessorTest {

    UserSimilarityProcessor userSimilarityProcessor;
    protected GraphDatabaseService graphDb;
    private GraphDBManager GDBM;
    Label userLabel, cityLabel, pageLabel, productLabel, religionLabel, politicalLabel;

    @Before
    public void init() {
        userSimilarityProcessor = new UserSimilarityProcessor();
    }

    @Before
    public void prepareTestDatabase()
    {
        if(graphDb == null)
            graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
            /*graphDb = new TestGraphDatabaseFactory().newEmbeddedDatabase(Properties.DB_PATH);*/

        if(GDBM == null)
            GDBM = new GraphDBManager(graphDb);

        userLabel = new User();
        cityLabel = new City();
        pageLabel = new Page();
        productLabel = new Product();
        religionLabel = new Religion();
        politicalLabel = new PoliticalView();
    }

    @Test
    public void shouldCalculateSimilarityPercentage() throws Exception {
        SimilarUser user = new SimilarUser();
        user.similaritySum = 2;

        double max = 5.0;
        user.calculateSimilarityPercentage(max);

        /*System.out.println("user.similarityPercentage=" + user.similarityPercentage);*/
        Assert.assertTrue(user.similarityPercentage == 40.0);
    }

    @Test
    public void shouldSortUsersDescendingWithSimilarity() throws Exception {
        List<SimilarUser> users = new ArrayList<>();
        List<SimilarUser> usersExpected = new ArrayList<>();

        SimilarUser user1 = new SimilarUser();
        user1.similarityPercentage = 10.0;

        SimilarUser user2 = new SimilarUser();
        user2.similarityPercentage = 100.0;

        SimilarUser user3 = new SimilarUser();
        user3.similarityPercentage = 50.0;

        users.add(user1);
        users.add(user2);
        users.add(user3);

        usersExpected.add(user2);
        usersExpected.add(user3);
        usersExpected.add(user1);

/*        System.out.println("Users before sort:");
        for(SimilarUser u : users)
            System.out.println(u.similarity);*/

        UserSimilarityProcessor usp = new UserSimilarityProcessor();
        users = usp.sortUsersDescendingWithSimilarityPercentage(users);

  /*      System.out.println("Users:");
        for(SimilarUser u : users)
            System.out.println(u.similarity);

        System.out.println("Users expected:");
        for(SimilarUser u : usersExpected)
            System.out.println(u.similarity);
*/
        assertThat("List equality", users, contains(usersExpected.toArray()));


    }

    @Test
    public void testCreateSimilarUserIfNotExistsAndPutIntoMap() throws Exception {
        Map<String, SimilarUser> map1 = new HashMap<>();
        SimilarUser user1 = new SimilarUser();
        String id1 = "1";
        map1.put(id1, user1);

        /*SimilarUser user2 = new SimilarUser();*/
        String id2 = "2";
        /*map1.put(id2, user2);*/

        UserSimilarityProcessor usp = new UserSimilarityProcessor();

        SimilarUser receivedUser1 = usp.createSimilarUserIfNotExistsAndPutIntoMap(map1, id1);
        SimilarUser receivedUser2 = usp.createSimilarUserIfNotExistsAndPutIntoMap(map1, id2);

/*        System.out.println("user1=" + user1.toString());
        System.out.println("receivedUser1=" + receivedUser1.toString());
        System.out.println("receivedUser2=" + receivedUser2.toString());*/

        Assert.assertTrue(receivedUser1 == user1);
        Assert.assertTrue(receivedUser2 != null);
    }

    @Test
    public void testProcessUserFriends() throws Exception {
        Map<String, SimilarUser> map1 = new HashMap<>();
        SimilarUser user1 = new SimilarUser();
        final String id1 = "1";
        map1.put(id1, user1);

        double weight = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.KNOWS);

        UserSimilarityProcessor usp = new UserSimilarityProcessor();

        // Normal test
        usp.processUserFriends(new ArrayList<String>() {{add(id1);}}, map1);
        Assert.assertEquals(map1.get(id1).similaritySum, weight, 0.01);

        // New user creation test
        final String id2 = "2";
        usp.processUserFriends(new ArrayList<String>() {{add(id2);}}, map1);
        Assert.assertEquals(map1.get(id2).similaritySum, weight, 0.01);

        // User with existing nonzero similaritySum
        SimilarUser user3 = new SimilarUser();
        final String id3 = "3";
        double initialSimilarity = 4.1;
        user3.similaritySum = initialSimilarity;
        map1.put(id3, user3);
        usp.processUserFriends(new ArrayList<String>() {{add(id3);}}, map1);
        Assert.assertEquals(map1.get(id3).similaritySum, weight+initialSimilarity, 0.01);

    }

    @Test
    public void testProcessUserLikes() throws Exception {
        double weight = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.LIKES);

        Map<String, Integer> commonLikes = new HashMap<>();
        final String id1 = "1";
        int likesNumber = 10;
        commonLikes.put(id1, likesNumber);

        Map<String, SimilarUser> similarUserHashMap = new HashMap<>();
        SimilarUser user1 = new SimilarUser();
        similarUserHashMap.put(id1, user1);

        userSimilarityProcessor.processUserLikes(commonLikes, similarUserHashMap);
        Assert.assertEquals(similarUserHashMap.get(id1).similaritySum, weight*likesNumber, 0.01);
    }

    @Test
    public void testProcessCommonMap() throws Exception {
        Node user1;

        try(Transaction tx = graphDb.beginTx()){
            user1 = graphDb.createNode(userLabel);
            user1.setProperty("UID", "1");

            Node user2 = graphDb.createNode(userLabel);
            user2.setProperty("UID", "2");

            Node user3 = graphDb.createNode(userLabel);
            user3.setProperty("UID", "3");

            Node page1 = graphDb.createNode(pageLabel);
            Node page2 = graphDb.createNode(pageLabel);
            Node page3 = graphDb.createNode(pageLabel);

            user1.createRelationshipTo(user2, GraphConstants.RelTypes.KNOWS);
            user1.createRelationshipTo(user3, GraphConstants.RelTypes.KNOWS);

            user1.createRelationshipTo(page1, GraphConstants.RelTypes.LIKES);
            user3.createRelationshipTo(page1, GraphConstants.RelTypes.LIKES);

            user1.createRelationshipTo(page2, GraphConstants.RelTypes.LIKES);
            user2.createRelationshipTo(page2, GraphConstants.RelTypes.LIKES);
            user3.createRelationshipTo(page2, GraphConstants.RelTypes.LIKES);

            tx.success();
        }

        Map<String, Map<String, AtomicInteger>> commonMap = GDBM.getOtherUsersWithRelationship(user1);
        Map<String, SimilarUser> similarUsersMap = new HashMap<>();

        userSimilarityProcessor.processCommonInterestsMap(commonMap, similarUsersMap);

        double expectedSimilarityToUser2 = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.LIKES);
        double expectedSimilarityToUser3 = GraphConstants.similarityWeights.get(GraphConstants.RelTypes.LIKES) * 2;

        Assert.assertEquals(expectedSimilarityToUser2, similarUsersMap.get("2").similaritySum, 0.01);
        Assert.assertEquals(expectedSimilarityToUser3, similarUsersMap.get("3").similaritySum, 0.01);
    }

    @Test
    public void testSortBySimilarity() {
//        Random random = new Random(System.currentTimeMillis());
//        Map<String, Integer> testMap = new HashMap<String, Integer>(1000);
//        for(int i = 0 ; i < 1000 ; ++i) {
//            testMap.put( "SomeString" + random.nextInt(), random.nextInt());
//        }

        SimilarUser user1 = new SimilarUser();
        user1.similaritySum = 10.0;

        SimilarUser user2 = new SimilarUser();
        user2.similaritySum = 100.0;

        SimilarUser user3 = new SimilarUser();
        user3.similaritySum = 50.0;

        Map<String, SimilarUser> testMap = new HashMap<>();
        testMap.put("1", user1);
        testMap.put("2", user2);
        testMap.put("3", user3);

        testMap = userSimilarityProcessor.sortUsersDescendingWithSimilarity(testMap);
        Assert.assertEquals( 3, testMap.size() );

        double previous = -1.0;
        for(Map.Entry<String, SimilarUser> entry : testMap.entrySet()) {
            Assert.assertNotNull( entry.getValue() );
            if (previous != -1.0) {
                Assert.assertTrue( entry.getValue().similaritySum <= previous );
            }
            previous = entry.getValue().similaritySum;
        }
    }
}