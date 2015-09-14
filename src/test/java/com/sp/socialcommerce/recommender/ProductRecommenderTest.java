package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.*;
import com.sp.socialcommerce.labels.User;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.List;
import java.util.Map;

public class ProductRecommenderTest {

    ProductRecommender productRecommender;
    protected GraphDatabaseService graphDb;
    private GraphDBManager GDBM;
    Label userLabel, cityLabel, pageLabel, productLabel, religionLabel, politicalLabel;

    @Before
    public void init() {
        productRecommender = new ProductRecommender();
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
    public void testGetTopRatedProducts() throws Exception {
        init();
        prepareTestDatabase();
        Node user1,user2;
        try(Transaction tx= graphDb.beginTx()) {
            user1 = graphDb.createNode(userLabel);
            user1.setProperty("UID", "1");
            user2 = graphDb.createNode(userLabel);
            user2.setProperty("UID", "2");

            Node prod1 = GDBM.createNode(new Product(), new String[][]{{"id", "101"}, {"name_en", "aaa"}, {"img_url", "www.wp.pl"}});
            Node prod2 = GDBM.createNode(new Product(), new String[][]{{"id", "102"}, {"name_en", "aaa"}, {"img_url", "www.wp.pl"}});
            Node prod3 = GDBM.createNode(new Product(), new String[][]{{"id", "103"}, {"name_en", "aaa"}, {"img_url", "www.wp.pl"}});

            Relationship rates1 = user1.createRelationshipTo(prod1, GraphConstants.RelTypes.RATES);
            rates1.setProperty(GraphConstants.Rates.RATING_VALUE, "3");

            Relationship rates2 = user1.createRelationshipTo(prod2, GraphConstants.RelTypes.RATES);
            rates2.setProperty(GraphConstants.Rates.RATING_VALUE, "5");

            Relationship rates3 = user1.createRelationshipTo(prod3, GraphConstants.RelTypes.RATES);
            rates3.setProperty(GraphConstants.Rates.RATING_VALUE, "2");



            Relationship rates4 = user2.createRelationshipTo(prod1, GraphConstants.RelTypes.RATES);
            rates4.setProperty(GraphConstants.Rates.RATING_VALUE, "5");

            Relationship rates5 = user2.createRelationshipTo(prod3, GraphConstants.RelTypes.RATES);
            rates5.setProperty(GraphConstants.Rates.RATING_VALUE, "2");


            tx.success();
        }

        productRecommender.GDBM = GDBM;

        Map<Product, Double> products = productRecommender.getTopRatedProducts(3);
        Assert.assertEquals(3, products.size());
/*        Assert.assertEquals(5, products.get(0).doubleValue());
        Assert.assertEquals(4, products.get(1).doubleValue());
        Assert.assertEquals(2, products.get(2).doubleValue());*/
        /*Assert.assertEquals("5", products.get(0));
        Assert.assertEquals("4", products.get(1));
        Assert.assertEquals("2", products.get(2));*/

    }

    /*    @Test
    public void testGetUserHighestRatedProductsSortedDescendigly() throws Exception {
        Node user1;

        try(Transaction tx= graphDb.beginTx()) {
            user1 = graphDb.createNode(userLabel);
            user1.setProperty("UID", "1");

            Node product1 = graphDb.createNode(productLabel);
            Node product2 = graphDb.createNode(productLabel);
            Node product3 = graphDb.createNode(productLabel);

            product1.setProperty(GraphConstants.Product.PRODUCT_ID, "1001");
            product1.setProperty(GraphConstants.Product.PRODUCT_IMG_URL, "www.imgurl.com");
            product1.setProperty(GraphConstants.Product.PRODUCT_NAME_EN, "Product 1001");

            product2.setProperty(GraphConstants.Product.PRODUCT_ID, "1002");
            product2.setProperty(GraphConstants.Product.PRODUCT_IMG_URL, "www.imgurl.com");
            product2.setProperty(GraphConstants.Product.PRODUCT_NAME_EN, "Product 1002");

            product3.setProperty(GraphConstants.Product.PRODUCT_ID, "1003");
            product3.setProperty(GraphConstants.Product.PRODUCT_IMG_URL, "www.imgurl.com");
            product3.setProperty(GraphConstants.Product.PRODUCT_NAME_EN, "Product 1003");

            Relationship rates1 = user1.createRelationshipTo(product1, GraphConstants.RelTypes.RATES);
            rates1.setProperty(GraphConstants.Rates.RATING_VALUE, "3");

            Relationship rates2 = user1.createRelationshipTo(product2, GraphConstants.RelTypes.RATES);
            rates2.setProperty(GraphConstants.Rates.RATING_VALUE, "5");

            Relationship rates3 = user1.createRelationshipTo(product3, GraphConstants.RelTypes.RATES);
            rates3.setProperty(GraphConstants.Rates.RATING_VALUE, "2");

            tx.success();
        }

        productRecommender.GDBM = GDBM;

        List<Product> products = productRecommender.getUserHighestRatedProductsSortedDescendigly("1", 5);
        Assert.assertEquals(3, products.size());
        Assert.assertEquals("5", products.get(0).getRating());
        Assert.assertEquals("3", products.get(1).getRating());
        Assert.assertEquals("2", products.get(2).getRating());


    }*/
}