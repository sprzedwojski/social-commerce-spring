package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.sp.socialcommerce.facebook.FacebookService;
import com.sp.socialcommerce.labels.*;
import com.sp.socialcommerce.models.User;
import com.sp.socialcommerce.prop.Properties;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.impl.batchimport.cache.MemoryStatsVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Configuration
@PropertySource("classpath:properties/${envTarget:localhost}.properties")
public class GraphDBManager {

	private static final Logger logger = LoggerFactory.getLogger(GraphDBManager.class);

	Label politicalViewLabel = new PoliticalView();
	Label religionLabel = new Religion();
	Label userLabel = new User();
	Label cityLabel = new City();
	Label pageLabel = new Page();
	Label pageCategoryLabel = new PageCategory();
	Label productLabel = new Product();
	Label favoriteLabel = new Favorite();
	Label favoriteCategoryLabel = new FavoriteCategory();
	Label educationLevelLabel = new EducationLevel();

	/*IObjectProcessor educationProcessor, workProcessor;

	{
		educationProcessor = new ObjectProcessorImpl("school",
				new String[]{"startYear"},
				new HashMap<String, RelationshipType>() {{
					put("schoolType", GraphConstants.RelTypes.IS_OF_TYPE);
				}},
				new Label() {
					@Override
					public String name() {
						return "School";
					}
				},
				GraphConstants.RelTypes.ATTENDED
		);
		workProcessor = new ObjectProcessorImpl("companyID",
				new String[]{"company", "title", "startDate"},
				new HashMap<String, RelationshipType>() {{
				}},
				new Label() {
					@Override
					public String name() {
						return "Work";
					}
				},
				GraphConstants.RelTypes.WORKED_IN
		);
	}*/

	IUserResponseProcessor[] processors = {
		new SimpleProcessor(/*GraphConstants.City.CITY_KEY,*/ FacebookService.MAP_USER_LOCATION,
				GraphConstants.City.CITY_NAME, cityLabel, GraphConstants.RelTypes.LIVES_IN),
		new SimpleProcessor(/*GraphConstants.Hometown.HOMETOWN_KEY,*/ FacebookService.MAP_USER_HOMETOWN,
				GraphConstants.City.CITY_NAME, cityLabel, GraphConstants.RelTypes.WAS_BORN_IN),
		new SimpleProcessor(FacebookService.MAP_USER_GENDER, "name", new Label() {
			@Override
			public String name() {
				return "Gender";
			}
		}, GraphConstants.RelTypes.IS_OF_GENDER),
		/*new SimpleProcessor("relationshipStatus", "name", new Label() {
				@Override
				public String name() {
					return "RelationshipStatus";
				}
			}, GraphConstants.RelTypes.HAS_RELATIONSHIP_STATUS),*/
		new SimpleProcessor(/*GraphConstants.Religion.RELIGION_KEY,*/ FacebookService.MAP_USER_RELIGION,
				GraphConstants.Religion.RELIGION_NAME, religionLabel, GraphConstants.RelTypes.FOLLOWS_RELIGION),
		new SimpleProcessor(/*GraphConstants.PoliticalView.POLITICAL_VIEW_KEY,*/ FacebookService.MAP_USER_POLITICAL,
				GraphConstants.PoliticalView.POLITICAL_VIEW_NAME, politicalViewLabel, GraphConstants.RelTypes.HAS_POLITICAL_VIEW),
		/*new SimpleProcessor(GraphConstants.EducationLevel.EDUCATION_LEVEL_KEY, GraphConstants.EducationLevel.EDUCATION_LEVEL_NAME, educationLevelLabel, GraphConstants.RelTypes.HAS_EDUCATION_LEVEL),
		new ArrayProcessor(GraphConstants.Education.EDUCATION_KEY, educationProcessor),
		new ArrayProcessor(GraphConstants.Work.WORK_KEY, workProcessor),
		new FavoritesProcessor(),
		new PagesProcessor()*/
	};

	GraphDatabaseService graphDb;

	public GraphDBManager() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( Properties.DB_PATH );
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



	public void processUserResponse(/*GSResponse response*/ Map<String, Object> responseMap) {
		/*String UID = response.getString(GraphConstants.User.UID, "uid");
		String userName = response.getString(GraphConstants.User.USER_NICKNAME, null);*/

		logger.info("Inside processUserResponse");

		com.restfb.types.User userProfile = (com.restfb.types.User)responseMap.get(FacebookService.MAP_USER_PROFILE);

		Node user = getUserNode(userProfile.getId(), userProfile.getName(),
				responseMap.containsKey(FacebookService.MAP_PROLONGED_TOKEN) ? (String) responseMap.get(FacebookService.MAP_PROLONGED_TOKEN) : null);

		logger.info("Start processors.");

		for(IUserResponseProcessor processor : processors) {
			processor.run(responseMap, this, user);
		}

		new PagesProcessor().run(responseMap, this, user);

		processUserFriendsResponse(responseMap, user);

		logger.info("End processors.");
	}

	public void processUserFriendsResponse(Map<String, Object> responseMap, Node user) {

		logger.info("Inside FriendsProcessor.");

		if(!responseMap.containsKey(FacebookService.MAP_USER_FRIENDS)) {
			logger.error("ResponseMap doesn't contain user friends!");
			return;
		}

		List<JsonArray> friendsList = (List<JsonArray>)responseMap.get(FacebookService.MAP_USER_FRIENDS);

		List<String> existingUserFriendsIds = getUserFriendsIds(user);

		for(JsonArray friendsArray : friendsList) {
			for (int i = 0; i < friendsArray.length(); i++) {
				JsonObject jsonObject = friendsArray.getJsonObject(i);
				String friendId = (String) jsonObject.get("id");
				logger.info("Processing friend with ID=" + friendId);

				if (!existingUserFriendsIds.contains(friendId)) {
					Node friend = getNode(userLabel, GraphConstants.User.UID, friendId);
					if (friend == null) {
						String friendName = (String) jsonObject.get(/*GraphConstants.User.USER_NICKNAME*/"name");
						String[][] friendUserParameters = {
								{GraphConstants.User.UID, friendId},
								{GraphConstants.User.USER_NAME, friendName}};
						friend = createNode(userLabel, friendUserParameters);
						logger.info("Created new node of type " + userLabel.name() + ": " + friendName);
					}
					createRelationship(user, friend, GraphConstants.RelTypes.KNOWS);
				} else {
					logger.info("Users are already connected.");
				}
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

	public List<String> getRelatedNodesIds(Node node, RelationshipType relType, String relatedNodeIdPropertyName) {
		try (Transaction tx = graphDb.beginTx()) {
			List<String> ids = new ArrayList<String>();
			Iterable<Relationship> nodeRelationships = node.getRelationships(relType);
			Iterator<Relationship> it = nodeRelationships.iterator();
			while(it.hasNext()) {
				Relationship relationship = (Relationship)it.next();
				Node otherNode = relationship.getOtherNode(node);
				String id = (String)otherNode.getProperty(relatedNodeIdPropertyName);
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
	
	public Node getUserNode(String UID, String name, String prolongedToken) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node user = graphDb.findNode(userLabel, GraphConstants.User.UID, UID);
			tx.success();
			if (user == null) {
				logger.info("User not found. Creating new user.");
				return createUserNode(UID, name, prolongedToken);
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
	
	public Node createUserNode(String UID, String name, String prolongedToken) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Node node = graphDb.createNode();
			node.addLabel(userLabel);
			node.setProperty(GraphConstants.User.UID, UID);
			node.setProperty(GraphConstants.User.USER_NAME, name);
			if(prolongedToken != null) {
				node.setProperty(FacebookService.MAP_PROLONGED_TOKEN, prolongedToken);
			}
			tx.success();
			logger.info("User node created successfully (" + name + ")");

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
			logger.info("node created: " + label.name());
//			logger.info(String.format("Created node: %s [%s: %s]" + label.name(),
//					parameters.length > 0 ? parameters[0][0] : "", parameters.length > 0 ? parameters[0][1] : ""));

			return node;
		}
	}

	Relationship createRelationship(Node from, Node to, RelationshipType type) {
		try ( Transaction tx = graphDb.beginTx() ) {
			Relationship relationship = from.createRelationshipTo(to, type);
			tx.success();
			logger.info("Relationship of type " + type + " created");
			return relationship;
		}
	}

	public List<Product> getAllProducts() {
		return getAllProducts(null);
	}

	public Map<String, List<Product>> getAllProductsByCategories(String uid) {
		List<Product> products = getAllProducts(uid);
		Map<String, List<Product>> map = new HashMap<String, List<Product>>();
		
		for(Product p : products) {
			if(map.containsKey(p.getCategory())) {
				map.get(p.getCategory().toLowerCase()).add(p);
			} else {
				List<Product> list = new ArrayList<Product>();
				list.add(p);
				map.put(p.getCategory(), list);
			}
		}
		
		return map;
	}
	
	public List<Product> getAllProducts(String uid) {
		try(Transaction tx = graphDb.beginTx()) {
			ResourceIterator iterator = graphDb.findNodes(productLabel);

			Node user = null;
			Map<Integer, String> userProductRatings = new HashMap<>();
			
			if(uid != null) {
				user = graphDb.findNode(userLabel, GraphConstants.User.UID, uid);				
			}
			
			if(user != null) {
				Iterable<Relationship> ratings = user.getRelationships(GraphConstants.RelTypes.RATES);
				Iterator<Relationship> it = ratings.iterator();
				while(it.hasNext()) {
					Relationship rating = it.next();
					Node product = rating.getOtherNode(user);
					userProductRatings.put(Integer.parseInt(product.getProperty(GraphConstants.Product.PRODUCT_ID).toString()),
							rating.getProperty(GraphConstants.Rates.RATING_VALUE).toString());
//					logger.info("Setting userProductRating...");
				}
			}

 			Node pNode;
			List<Product> products = new ArrayList<Product>();
			while(iterator.hasNext()) {
				pNode = (Node)iterator.next();
				Product product = new Product();
				product.setId(Integer.parseInt(pNode.getProperty(GraphConstants.Product.PRODUCT_ID).toString()));
				product.setImageUrl(pNode.getProperty(GraphConstants.Product.PRODUCT_IMG_URL).toString());
				product.setNameEn(pNode.getProperty(GraphConstants.Product.PRODUCT_NAME_EN).toString());
				if(pNode.hasProperty(GraphConstants.Product.PRODUCT_NAME_PL)) {
					product.setNamePl(pNode.getProperty(GraphConstants.Product.PRODUCT_NAME_PL).toString());
				}
				if(pNode.hasProperty(GraphConstants.Product.PRODUCT_DESC_PL)) {
					product.setDescriptionPl(pNode.getProperty(GraphConstants.Product.PRODUCT_DESC_PL).toString());
				}
				if(pNode.hasProperty(GraphConstants.Product.PRODUCT_DESC_EN)) {
					product.setDescriptionEn(pNode.getProperty(GraphConstants.Product.PRODUCT_DESC_EN).toString());
				}
				if(pNode.hasProperty(GraphConstants.Product.PRODUCT_PRICE_EUR)) {
					product.setPrice(Double.parseDouble(pNode.getProperty(GraphConstants.Product.PRODUCT_PRICE_EUR).toString()));
				}
				if(pNode.hasProperty(GraphConstants.Product.PRODUCT_PROD_URL)) {
					product.setProductUrl(pNode.getProperty(GraphConstants.Product.PRODUCT_PROD_URL).toString());
				}

				if(user != null) {
					if(userProductRatings.containsKey(product.getId())) {
						product.setRating(userProductRatings.get(product.getId()));
					}
				}

				Iterable<Relationship> relationships = pNode.getRelationships(GraphConstants.RelTypes.HAS_CATEGORY);								
				Relationship rel = relationships.iterator().next();
				if(rel != null) {
					product.setCategory(rel.getOtherNode(pNode).getProperty(GraphConstants.ProductCategory.PRODUCT_CATEGORY_NAME).toString().toLowerCase());
				}

				products.add(product);

//				logger.info("Product added.");
			}

			logger.info("Fetched all products.");
			tx.success();

			return products;
		}
	}

	/**
	 * Not efficient. Iterating over all RATES relationships during each rating.
	 * It would be better to insert ratings once at the end. But the user might not save it and we would have nothing.
	 *
	 * @param uid
	 * @param productId
	 * @param score
	 */
	public void setProductRating(String uid, String productId, String score) {
		try(Transaction tx = graphDb.beginTx()) {
			Node user = graphDb.findNode(userLabel, GraphConstants.User.UID, uid);
			Node product = graphDb.findNode(productLabel, GraphConstants.Product.PRODUCT_ID, productId);
			
			Relationship rating = null;
			Iterable ratings = user.getRelationships(GraphConstants.RelTypes.RATES);
			Iterator iterator = ratings.iterator();
			while(iterator.hasNext()) {
				Relationship r = (Relationship)iterator.next();
				Node productRated = r.getOtherNode(user);
				
				if(productRated.getProperty(GraphConstants.Product.PRODUCT_ID).equals(product.getProperty(GraphConstants.Product.PRODUCT_ID))) {
					rating = r;
					break;
				}
			}

			if(rating != null) {
				if(score.equals("null")) {
					rating.delete();
					logger.info("Product had already been rated. User rating deleted. UID: " + uid + " | Product ID: " + productId + " | Score: " + score);
				} else {
					rating.setProperty(GraphConstants.Rates.RATING_VALUE, score);
					logger.info("Product had already been rated. User rating updated in DB. UID: " + uid + " | Product ID: " + productId + " | Score: " + score);
				}
			} else {			
				if(score.equals("null")) {
					logger.info("User rating is NULL. No action taken. UID: " + uid + " | Product ID: " + productId + " | Score: " + score);
				} else {
					rating = user.createRelationshipTo(product, GraphConstants.RelTypes.RATES);
					rating.setProperty(GraphConstants.Rates.RATING_VALUE, score);
					logger.info("User rating stored in DB. UID: " + uid + " | Product ID: " + productId + " | Score: " + score);
				}
			}

			tx.success();
		}
	}
	
}
