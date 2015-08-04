package com.sp.socialcommerce.neo4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class GraphDBManagerTest /*extends TestCase */{

    protected GraphDatabaseService graphDb;

    private GraphDBManager GDBM;

    @Before
    public void prepareTestDatabase()
    {
        if(graphDb == null)
            graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
        System.out.println("graphDb is " + (graphDb == null ? "null" : "not null"));

        if(GDBM == null)
            GDBM = new GraphDBManager(graphDb);
    }

    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }

    @Test
    public void aa() {
        Assert.assertTrue(true);
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