package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Likes;
import com.restfb.types.NamedFacebookType;
import com.sp.socialcommerce.facebook.FacebookService;
import org.neo4j.graphdb.Node;

import java.util.List;
import java.util.Map;

public class PagesProcessor implements IUserResponseProcessor {


    @Override
    public void run(GSResponse response, GraphDBManager dbManager, Node user) {

    }

    @Override
    public void run(Map<String, Object> responseMap, GraphDBManager dbManager, Node user) {

        logger.info("Inside PagesProcessor.");

        if(!responseMap.containsKey(FacebookService.MAP_USER_LIKES)) {
            logger.error("ResponseMap doesn't contain user likes!");
            return;
        }

        List<JsonArray> pagesList = (List<JsonArray>)responseMap.get(FacebookService.MAP_USER_LIKES);

        List<String> existingUserPagesIds = dbManager.getRelatedNodesIds(user, GraphConstants.RelTypes.LIKES, GraphConstants.Page.PAGE_ID);

        String id, name, category;
        for(JsonArray pages : pagesList) {
            logger.info(" >> pages: " + pages.toString());

            for (int i = 0; i < pages.length(); i++) {
                JsonObject pageObject = pages.getJsonObject(i);
                id = pageObject.getString("id");
                name = pageObject.getString("name");
                category = pageObject.getString("category");

                if (!existingUserPagesIds.contains(id)) {
                    Node page = dbManager.getNode(dbManager.pageLabel, GraphConstants.Page.PAGE_ID, id);
                    if (page == null) {
                        String[][] pageProperties = {
                                {GraphConstants.Page.PAGE_ID, id},
                                {GraphConstants.Page.PAGE_NAME, name}
                        };
                        page = dbManager.createNode(dbManager.pageLabel, pageProperties);

                        Node pageCategory = dbManager.getNode(dbManager.pageCategoryLabel, GraphConstants.PageCategory.PAGE_CATEGORY_NAME, category);
                        if(pageCategory == null) {
                            String[][] pageCategoryProperties = { {GraphConstants.PageCategory.PAGE_CATEGORY_NAME, category} };
                            pageCategory = dbManager.createNode(dbManager.pageCategoryLabel, pageCategoryProperties);
                        }

                        dbManager.createRelationship(page, pageCategory, GraphConstants.RelTypes.HAS_CATEGORY);
                        logger.info("Page " + name + " and its category (" + category + ") created.");
                    } else {
                        logger.info("Page " + name + " already exists.");
                    }

                    dbManager.createRelationship(user, page, GraphConstants.RelTypes.LIKES);
                } else {
                    logger.info("User is already connected to page: " + name);
                }
            }
        }
        logger.info("pages processing ended.");
    }
}
