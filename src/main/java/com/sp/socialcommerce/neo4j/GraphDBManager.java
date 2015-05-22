package com.sp.socialcommerce.neo4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.sp.socialcommerce.labels.City;
import com.sp.socialcommerce.labels.Page;
import com.sp.socialcommerce.labels.PageCategory;
import com.sp.socialcommerce.labels.PoliticalView;
import com.sp.socialcommerce.labels.Religion;
import com.sp.socialcommerce.models.User;

public class GraphDBManager {

	public static final String DB_PATH = "/home/szymon/programs/neo4j/neo4j-test/";

	private static final Logger logger = LoggerFactory.getLogger(GraphDBManager.class);

	private Label politicalViewLabel = new PoliticalView();
	private Label religionLabel = new Religion();
	private Label userLabel = new User();
	private Label cityLabel = new City();
	private Label pageLabel = new Page();
	private Label pageCategoryLabel = new PageCategory();
	
	GraphDatabaseService graphDb;
	
	public GraphDBManager() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
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

	public void processUserResponse(GSResponse response) {
		String UID = response.getString(GraphConstants.User.UID, "uid");
		String firstName = response.getString(GraphConstants.User.FIRST_NAME, "first_name");
		String lastName = response.getString(GraphConstants.User.LAST_NAME, "last_name");
		logger.info("firstName: " + firstName);
		logger.info("lastName: " + lastName);

		Node user = getUserNode(UID, firstName, lastName);

		// CITY
		String cityName = response.getString(GraphConstants.City.CITY_KEY, null);
		logger.info("city: " + cityName);

		if(cityName != null) {
			Node city = getCityNode(cityName);

			if (!hasNodeRelationshipType(user, GraphConstants.RelTypes.LIVES_IN)) {
				createRelationship(user, city, GraphConstants.RelTypes.LIVES_IN);
			} else {
				logger.info(GraphConstants.RelTypes.LIVES_IN + " relationship already exists.");
			}
		}


		// RELIGION
		String religionName = response.getString(GraphConstants.Religion.RELIGION_KEY, null);
		logger.info("religion: " + religionName);

		if(religionName != null) {
			Node religion = getNode(religionLabel, GraphConstants.Religion.RELIGION_NAME, religionName);
			if (religion == null) {
				String[][] religionProperties = { {GraphConstants.Religion.RELIGION_NAME, religionName} };
				religion = createNode(religionLabel, religionProperties);
			}

			if (!hasNodeRelationshipType(user, GraphConstants.RelTypes.FOLLOWS_RELIGION)) {
				createRelationship(user, religion, GraphConstants.RelTypes.FOLLOWS_RELIGION);
			} else {
				logger.info(GraphConstants.RelTypes.FOLLOWS_RELIGION + " relationship already exists.");
			}
		}
		
		
		// POLITICAL VIEW
		String politicalViewName = response.getString(GraphConstants.PoliticalView.POLITICAL_VIEW_KEY, null);
		logger.info("political view: " + politicalViewName);
		
		if(politicalViewName != null) {
			Node politicalView = getNode(politicalViewLabel, GraphConstants.PoliticalView.POLITICAL_VIEW_NAME, politicalViewName);
			
			if (politicalView == null) {
				String[][] politicalViewProperties = { {GraphConstants.PoliticalView.POLITICAL_VIEW_NAME, politicalViewName} };
				politicalView = createNode(politicalViewLabel, politicalViewProperties);
			}
			
			if (!hasNodeRelationshipType(user, GraphConstants.RelTypes.HAS_POLITICAL_VIEW)) {
				createRelationship(user, politicalView, GraphConstants.RelTypes.HAS_POLITICAL_VIEW);
			} else {
				logger.info(GraphConstants.RelTypes.HAS_POLITICAL_VIEW + " relationship already exists.");
			}
		}
		
		logger.info("starting processing pages...");
		
		
		// PAGES
		GSArray pages = response.getArray(GraphConstants.LIKES_KEY, null);
		
		try {
			if (pages != null) {
				logger.info("likes: " + pages.toString());
				
				List<String> existingUserPagesIds = getUserPagesIds(user);
				
				GSObject row = null;
				String id, name, category;
				for(int i=0; i<pages.length(); i++) {
					row = pages.getObject(i);
					id = (String)row.get(GraphConstants.Page.PAGE_ID);
					category = (String)row.get(GraphConstants.PageCategory.PAGE_CATEGORY_KEY);
					name = (String)row.get(GraphConstants.Page.PAGE_NAME);
					logger.info(" >> page: " + id + ", " + name + ", " + category);
					
//					logger.info("existingUserPagesIds: " + existingUserPagesIds.toString());
					
					if(!existingUserPagesIds.contains(id)) {
//						logger.info("[Inside page]");
						Node page = getNode(pageLabel, GraphConstants.Page.PAGE_ID, id);
						if(page == null) {
							String[][] pageProperties = {
									{GraphConstants.Page.PAGE_ID, id},
									{GraphConstants.Page.PAGE_NAME, name}
							};
							page = createNode(pageLabel, pageProperties);
							
							Node pageCategory = getNode(pageCategoryLabel, GraphConstants.PageCategory.PAGE_CATEGORY_NAME, category);
							if(pageCategory == null) {
								String[][] pageCategoryProperties = { {GraphConstants.PageCategory.PAGE_CATEGORY_NAME, category} };
								pageCategory = createNode(pageCategoryLabel, pageCategoryProperties);
							}
							
							createRelationship(page, pageCategory, GraphConstants.RelTypes.HAS_CATEGORY);							
							logger.info("Page " + name + " and its category (" + category + ") created.");
						} else {
							logger.info("Page " + name + " already exists.");
						}
						
						createRelationship(user, page, GraphConstants.RelTypes.LIKES);
					} else {
						logger.info("User is already connected to page: " + name);
					}
				}
			} else {
				logger.info("pages is NULL");
			}
		} catch (GSKeyNotFoundException e) {
			e.printStackTrace();
		}
		logger.info("pages processing ended.");

	}

	public void processUserFriendsResponse(String UID, GSResponse response) {
		Node user = getNode(userLabel, GraphConstants.User.UID, UID);
		if(user == null) {
			logger.error("User with UID=" + UID + " doesn't exist. Aborting user friends processing.");
			return;
		}
		
		List<String> existingUserFriendsIds = getUserFriendsIds(user);
		
		GSArray friends = response.getArray("friends", null);
		
		try {
			for(int i=0; i<friends.length(); i++) {
				GSObject row = friends.getObject(i);
				String friendUID = (String)row.get(GraphConstants.User.UID);
				logger.info("Processing friend with UID=" + friendUID);
				
				if(!existingUserFriendsIds.contains(friendUID)) {
					Node friend = getNode(userLabel, GraphConstants.User.UID, friendUID);
					if(friend == null) {
						String friendName = (String)row.get("nickname");
						String[][] friendUserParameters = {
								{GraphConstants.User.UID, friendUID},
								{GraphConstants.User.USER_NAME, friendName} };
						friend = createNode(userLabel, friendUserParameters);
						logger.info("Created new node of type " + userLabel.name() + ": " + friendName);
					}
					createRelationship(user, friend, GraphConstants.RelTypes.KNOWS);
				} else {
					logger.info("Users are already connected.");
				}
			}
		} catch (GSKeyNotFoundException e) {
			e.printStackTrace();
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

	public List<String> getUserPagesIds(Node node) {
		try (Transaction tx = graphDb.beginTx()) {
			List<String> ids = new ArrayList<String>();
			Iterable<Relationship> pages = node.getRelationships(GraphConstants.RelTypes.LIKES);
			Iterator<Relationship> it = pages.iterator();
			while(it.hasNext()) {
				Relationship relationship = (Relationship)it.next();
				Node page = relationship.getOtherNode(node);
				String id = (String)page.getProperty(GraphConstants.Page.PAGE_ID);
				ids.add(id);
			}
			return ids;
		}
	}
	
	public List<String> getUserFriendsIds(Node node) {
		try (Transaction tx = graphDb.beginTx()) {
			List<String> ids = new ArrayList<String>();
			Iterable<Relationship> friends = node.getRelationships(GraphConstants.RelTypes.KNOWS);
			Iterator<Relationship> it = friends.iterator();
			while(it.hasNext()) {
				Relationship relationship = (Relationship)it.next();
				Node friend = relationship.getOtherNode(node);
				String id = (String)friend.getProperty(GraphConstants.User.UID);
				ids.add(id);
			}
			return ids;
		}		
	}
	
	public Node getUserNode(String UID) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node user = graphDb.findNode(userLabel, GraphConstants.User.UID, UID);
			tx.success();
			if (user == null) {
				logger.info("User not found. Creating new user.");
				return createUserNode(UID);
			} else {
				logger.info("Existing user found.");
				return user;
			}
		}
	}
	
	public Node getUserNode(String UID, String fName, String lName) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node user = graphDb.findNode(userLabel, GraphConstants.User.UID, UID);
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
			Node city = graphDb.findNode(cityLabel, GraphConstants.City.CITY_NAME, cityName);
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

	public Node createUserNode(String UID) {
		String[][] userProperties = { {GraphConstants.User.UID, UID} };
		return createNode(userLabel, userProperties);
	}
	
	public Node createUserNode(String UID, String fName, String lName) {
		try ( Transaction tx = graphDb.beginTx() ) {
			String userName = fName + " " + lName;
			Node node = graphDb.createNode();
			node.addLabel(userLabel);
			node.setProperty(GraphConstants.User.UID, UID);
			node.setProperty(GraphConstants.User.USER_NAME, userName);
			tx.success();
			logger.info("User node created successfully (" + userName + ")");

			return node;
		}
	}

	public Node createCityNode(String cityName) { // e.g. "Lodz, Poland"
		try(Transaction tx = graphDb.beginTx()) {
			Node node = graphDb.createNode(cityLabel);
			node.setProperty(GraphConstants.City.CITY_NAME, cityName);
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
	
}
