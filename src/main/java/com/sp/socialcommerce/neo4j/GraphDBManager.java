package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphDBManager {

	public static final String DB_PATH = "/home/szymon/programs/neo4j/neo4j-test/";
	
	private static enum RelTypes implements RelationshipType
	{
	    KNOWS
	}
	
	GraphDatabaseService graphDb;
	Node firstNode;
	Node secondNode;
	Relationship relationship;
	
	public GraphDBManager() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
//		graphDb = connectAndStartBootstrapper();
		registerShutdownHook( graphDb );
	}
	
	public void initDB() {
		
		try ( Transaction tx = graphDb.beginTx() )
		{
		    // Database operations go here
			firstNode = graphDb.createNode();
			firstNode.setProperty( "message", "Hello, " );
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", "World!" );
			
			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
			relationship.setProperty( "message", "brave Neo4j " );

			tx.success();
		}
		
		try ( Transaction tx = graphDb.beginTx() )
		{
			System.out.print( firstNode.getProperty( "message" ) );
			System.out.print( relationship.getProperty( "message" ) );
			System.out.print( secondNode.getProperty( "message" ) );
			tx.success();
		}
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
//	public static GraphDatabaseService connectAndStartBootstrapper() {
//	    WrappingNeoServerBootstrapper neoServerBootstrapper;
//	    GraphDatabaseService db = new GraphDatabaseFactory()
//	            .newEmbeddedDatabaseBuilder(DB_PATH).newGraphDatabase();
//
//	    try {
//	        GraphDatabaseAPI api = (GraphDatabaseAPI) db;
//
//	        ServerConfigurator config = new ServerConfigurator(api);
//	        config.configuration().addProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "127.0.0.1");
//	        config.configuration().addProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, "7575");
//
//	        neoServerBootstrapper = new WrappingNeoServerBootstrapper(api, config);
//	        neoServerBootstrapper.start();
//	    }
//	    catch(Exception e) {
//	       //handle appropriately
//	    }
//	     
//	    return db;
//	}
	
}
