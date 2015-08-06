package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.Node;

import java.util.Map;

public class FavoritesProcessor implements IUserResponseProcessor {

    @Override
    public void run(Map<String, Object> responseMap, GraphDBManager dbManager, Node user) {

    }

    /*@Override
    public void run(GSResponse response, GraphDBManager dbManager, Node user) {
        GSObject favorites = response.getObject(GraphConstants.Favorite.FAVORITES_KEY, null);
        logger.info("favorites: " + favorites);

        if(favorites != null) {
            for (String favCategory : favorites.getKeys()) {
                GSArray favoritesCategoryArray = favorites.getArray(favCategory, null);
                if(favoritesCategoryArray != null) {
                    List<String> existingUserFavoritesIds = dbManager.getRelatedNodesIds(user,
                            GraphConstants.RelTypes.FAVORITES, GraphConstants.Favorite.FAVORITE_ID);
                    Iterator<Object> iterator = favoritesCategoryArray.iterator();
                    while(iterator.hasNext()) {
                        try {
                            String id, name, category;
                            GSObject favoritesObject = (GSObject)iterator.next();
                            if(favoritesObject != null) {
                                id = (String)favoritesObject.get(GraphConstants.Favorite.FAVORITE_ID);
                                name = (String)favoritesObject.get(GraphConstants.Favorite.FAVORITE_NAME);
                                category = (String)favoritesObject.get(GraphConstants.FavoriteCategory.FAVORITE_CATEGORY_KEY);

                                if(!existingUserFavoritesIds.contains(id)) {
                                    Node favorite = dbManager.getNode(dbManager.favoriteLabel,
                                            GraphConstants.Favorite.FAVORITE_ID, id);

                                    if(favorite == null) {
                                        String[][] favoriteProperties = {
                                                {GraphConstants.Favorite.FAVORITE_ID, id},
                                                {GraphConstants.Favorite.FAVORITE_NAME, name}
                                        };
                                        favorite = dbManager.createNode(dbManager.favoriteLabel,
                                            favoriteProperties);

                                        Node favoriteCategory = dbManager.getNode(
                                                dbManager.favoriteCategoryLabel,
                                                GraphConstants.FavoriteCategory.FAVORITE_CATEGORY_NAME,
                                                category);

                                        if(favoriteCategory == null) {
                                            String[][] favoriteCategoryProperties = {
                                                    {GraphConstants.FavoriteCategory.FAVORITE_CATEGORY_NAME, category}
                                            };
                                            favoriteCategory = dbManager.createNode(dbManager.favoriteCategoryLabel, favoriteCategoryProperties);
                                        }

                                        dbManager.createRelationship(favorite, favoriteCategory, GraphConstants.RelTypes.HAS_CATEGORY);
                                    } else {
                                        logger.info("Favorite " + name + " already exists.");
                                    }

                                    dbManager.createRelationship(user, favorite, GraphConstants.RelTypes.FAVORITES);
                                } else {
                                    logger.info("User is already connected to favorite: " + name);
                                }
                            }
                        } catch (GSKeyNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            logger.warn("Favorites is NULL");
        }
    }*/
}
