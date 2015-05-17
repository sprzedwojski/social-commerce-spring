package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSResponse;
import com.sp.socialcommerce.labels.City;
import com.sp.socialcommerce.labels.Religion;
import com.sp.socialcommerce.models.User;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.api.properties.Property;
import org.neo4j.kernel.impl.util.register.NeoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphDBManager {

	public static final String DB_PATH = "/home/szymon/programs/neo4j/neo4j-test/";

	private static final Logger logger = LoggerFactory.getLogger(GraphDBManager.class);

	GraphDatabaseService graphDb;
//	Node firstNode;
//	Node secondNode;
//	Relationship relationship;
	
	public GraphDBManager() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
	}
	
//	public void initDB() {
//
//		try ( Transaction tx = graphDb.beginTx() )
//		{
//		    // Database operations go here
//			firstNode = graphDb.createNode();
//			firstNode.setProperty( "message", "Hello, " );
//			secondNode = graphDb.createNode();
//			secondNode.setProperty( "message", "World!" );
//
//			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
//			relationship.setProperty( "message", "brave Neo4j " );
//
//			tx.success();
//		}
//
//		try ( Transaction tx = graphDb.beginTx() )
//		{
//			System.out.print( firstNode.getProperty( "message" ) );
//			System.out.print( relationship.getProperty( "message" ) );
//			System.out.print( secondNode.getProperty( "message" ) );
//			tx.success();
//		}
//	}
	
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

	public void processUserResponse(GSResponse response) {
		String UID = response.getString("UID", "uid placeholder");
		String firstName = response.getString("firstName", "first name placeholder");
		String lastName = response.getString("lastName", "last name placeholder");
		logger.info("firstName: " + firstName);
		logger.info("lastName: " + lastName);

		Node user = getUserNode(UID, firstName, lastName);

		// CITY
		String cityName = response.getString("city", null);
		logger.info("city: " + cityName);

		if(cityName != null) {
			Node city = getCityNode(cityName);

			if (!hasNodeRelationshipType(user, GraphConstants.RelTypes.LIVES_IN)) {
				createRelationship(user, city, GraphConstants.RelTypes.LIVES_IN);
			} else {
				logger.info("Relationship already exists.");
			}
		}


		// RELIGION
		String religionName = response.getString("religion", null);
		logger.info("religion: " + religionName);

		if(religionName != null) {
			Node religion = getNode(new Religion(), GraphConstants.Religion.RELIGION_LABEL, religionName);
			if (religion == null) {
				String[][] religionProperties = {
						{GraphConstants.Religion.RELIGION_NAME, religionName}
				};
				religion = createNode(new Religion(), religionProperties);
			}

			if (!hasNodeRelationshipType(user, GraphConstants.RelTypes.FOLLOWS_RELIGION)) {
				createRelationship(user, religion, GraphConstants.RelTypes.FOLLOWS_RELIGION);
			} else {
				logger.info("Relationship already exists.");
			}

		}

	}

	public Node getNode(Label label, String key, Object value) {
		try (Transaction tx = graphDb.beginTx()) {
			Node node = graphDb.findNode(label, key, value);
			tx.success();

			return node;
		}
	}

	public boolean hasNodeRelationshipType(Node node, RelationshipType type) {
		try ( Transaction tx = graphDb.beginTx() ) {
			boolean result = node.hasRelationship(type);
			tx.success();

			return result;
		}
	}

	public Node getUserNode(String UID, String fName, String lName) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node user = graphDb.findNode(new User(), GraphConstants.User.UID, UID);
			tx.success();
			if (user == null) {
				logger.info("User not found. Creating new user.");
				return createUserNode(UID, fName, lName);
			} else {
				logger.info("Existing user found.");
				return user;
			}
		}
	}

	public Node getCityNode(String cityName) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node city = graphDb.findNode(new City(), GraphConstants.City.CITY_NAME, cityName);
			tx.success();
			if (city == null) {
				logger.info("City not found. Creating new city.");
				return createCityNode(cityName);
			} else {
				logger.info("Existing city found.");
				return city;
			}
		}
	}

	public Node createUserNode(String UID, String fName, String lName) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node node = graphDb.createNode();
			node.addLabel(new User());
			node.setProperty(GraphConstants.User.UID, UID);
			node.setProperty(GraphConstants.User.FIRST_NAME, fName);
			node.setProperty(GraphConstants.User.LAST_NAME, lName);
			tx.success();
			logger.info("User node created successfully (" + fName + " " + lName + ")");

			return node;
		}
	}

	public Node createCityNode(String cityName) { // e.g. "Lodz, Poland"
		try(Transaction tx = graphDb.beginTx()) {
			Node node = graphDb.createNode(new City());
			node.setProperty("cityName", cityName);
			tx.success();
			logger.info("City node created successfully (" + cityName + ")");

			return node;
		}
	}

	public Node createNode(Label label, String[][] parameters) {
		try(Transaction tx = graphDb.beginTx()) {
			Node node = graphDb.createNode(label);
			for(String[] paramRow : parameters) {
				node.setProperty(paramRow[0], paramRow[1]);
			}
			tx.success();

			return node;
		}
	}

	public Relationship createRelationship(Node from, Node to, RelationshipType type) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Relationship relationship = from.createRelationshipTo(to, type);
			tx.success();
			logger.info("Relationship of type " + type + " created");
			return relationship;
		}
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
