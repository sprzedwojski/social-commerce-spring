package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import org.neo4j.graphdb.Node;

import java.util.List;

public class PagesProcessor implements IUserResponseProcessor {


    @Override
    public void run(GSResponse response, GraphDBManager dbManager, Node user) {
        GSArray pages = response.getArray(GraphConstants.LIKES_KEY, null);

        try {
            if (pages != null) {
//				logger.info("likes: " + pages.toString());

                List<String> existingUserPagesIds = dbManager.getRelatedNodesIds(user, GraphConstants.RelTypes.LIKES, GraphConstants.Page.PAGE_ID);

                GSObject row = null;
                String id, name, category;
                for(int i=0; i<pages.length(); i++) {
                    row = pages.getObject(i);
                    id = (String)row.get(GraphConstants.Page.PAGE_ID);
                    category = (String)row.get(GraphConstants.PageCategory.PAGE_CATEGORY_KEY);
                    name = (String)row.get(GraphConstants.Page.PAGE_NAME);
//					logger.info(" >> page: " + id + ", " + name + ", " + category);

                    if(!existingUserPagesIds.contains(id)) {
                        Node page = dbManager.getNode(dbManager.pageLabel, GraphConstants.Page.PAGE_ID, id);
                        if(page == null) {
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
            } else {
                logger.warn("pages is NULL");
            }
        } catch (GSKeyNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("pages processing ended.");
    }
}
