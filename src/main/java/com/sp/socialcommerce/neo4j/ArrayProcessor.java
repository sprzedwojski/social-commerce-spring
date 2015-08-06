package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.Node;

import java.util.Map;

public class ArrayProcessor /*implements IUserResponseProcessor*/ {

    private String responseKey;
    private IObjectProcessor objectProcessor;

    public ArrayProcessor(String responseKey, IObjectProcessor objectProcessor) {
        this.responseKey = responseKey;
        this.objectProcessor = objectProcessor;
    }

/*    @Override
    public void run(Map<String, Object> responseMap, GraphDBManager dbManager, Node user) {

    }*/

/*
    @Override
    public void run(GSResponse response,
                    GraphDBManager dbManager, Node user) {

        GSArray array = response.getArray(responseKey, new GSArray());
        logger.info(responseKey + ": " + array.toString());

        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            GSObject object = (GSObject)iterator.next();
            logger.info("Processing object: " + object.toString());
            try {
                objectProcessor.run(object, dbManager, user);
            } catch (GSKeyNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
*/

}
