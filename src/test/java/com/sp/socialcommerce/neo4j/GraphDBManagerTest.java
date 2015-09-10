package com.sp.socialcommerce.neo4j;

import com.sp.socialcommerce.labels.*;
import com.sp.socialcommerce.labels.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class GraphDBManagerTest /*extends TestCase */{

    protected GraphDatabaseService graphDb;
    private GraphDBManager GDBM;
    Label userLabel, cityLabel, pageLabel, productLabel, religionLabel, politicalLabel;

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

    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }

    @Test
    public void testTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void testGetOtherUsersWithRelationship() throws Exception {
        // 10205351905711072 -> Szymon P.
        /*GDBM.getOtherUsersWithRelationship(GDBM.getUserNode("10205351905711072"));*/
        /*System.out.println(GDBM.getAllProducts().size());*/
        /*assert true;*/

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

        /*System.out.println("commonMap size: " + commonMap.size());
        System.out.println("has likes?: " + ((Map)commonMap.get("1")).containsKey(GraphConstants.RelTypes.LIKES.toString()));
        System.out.println("relType: " + commonMap.get("1").get(GraphConstants.RelTypes.LIKES.toString()));*/

        int expectedNumberOfLikesForUser3 = 2;
        int receivedNumberOfLikesForUser3 = commonMap.get("3").get(GraphConstants.RelTypes.LIKES.toString()).intValue();

        Assert.assertEquals(expectedNumberOfLikesForUser3, receivedNumberOfLikesForUser3);

        int expectedNumberOfLikesForUser2 = 1;
        int receivedNumberOfLikesForUser2 = commonMap.get("2").get(GraphConstants.RelTypes.LIKES.toString()).intValue();

        Assert.assertEquals(expectedNumberOfLikesForUser2, receivedNumberOfLikesForUser2);

        // KNOWS relationships not counted
        Assert.assertTrue(!commonMap.get("2").containsKey(GraphConstants.RelTypes.KNOWS.toString()));
    }

    @Test
    public void testGetUserProductRatings() throws Exception {
        Node user1;

        try(Transaction tx = graphDb.beginTx()){
            user1 = graphDb.createNode(userLabel);
            user1.setProperty("UID", "1");

            Node product1 = graphDb.createNode(productLabel);
            Node product2 = graphDb.createNode(productLabel);

            product1.setProperty(GraphConstants.Product.PRODUCT_ID, "1001");
            product2.setProperty(GraphConstants.Product.PRODUCT_ID, "1002");

            Relationship rates1 = user1.createRelationshipTo(product1, GraphConstants.RelTypes.RATES);
            rates1.setProperty(GraphConstants.Rates.RATING_VALUE, "5");

            Relationship rates2 = user1.createRelationshipTo(product2, GraphConstants.RelTypes.RATES);
            rates2.setProperty(GraphConstants.Rates.RATING_VALUE, "3");

            tx.success();
        }

        Map<Integer, String> productsRatings = GDBM.getUserProductRatings("1");
//        productsRatings.forEach((x,y) -> System.out.println(x + ": " + y));
        Assert.assertEquals(2, productsRatings.size());
        Assert.assertEquals("5", productsRatings.get(1001));
        Assert.assertEquals("3", productsRatings.get(1002));
    }

    /*    @Test
    public void testGetCityNode() throws Exception {
        Node n = null;
        try ( Transaction tx = graphDb.beginTx() )
        {
            n = graphDb.createNode(new City());
            n.setProperty( "name", "Warszawa" );
            tx.success();
        }

        // The node should have a valid id
        assertThat( n.getId(), is( greaterThan( -1L ) ) );

        try ( Transaction tx = graphDb.beginTx() )
        {
            *//*Node foundNode = graphDb.getNodeById( n.getId() );*//*
            Node foundNode = GDBM.getNode(new City(), "name", );
            assertThat( foundNode.getId(), is( n.getId() ) );
            assertThat( (String) foundNode.getProperty( "name" ), is( "Warszawa" ) );
        }
    }*/
}