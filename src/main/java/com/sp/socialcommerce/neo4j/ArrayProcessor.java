package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import org.neo4j.graphdb.Node;

import java.util.Iterator;

public class ArrayProcessor implements IUserResponseProcessor {

    private String responseKey;
    private IObjectProcessor objectProcessor;

    public ArrayProcessor(String responseKey, IObjectProcessor objectProcessor) {
        this.responseKey = responseKey;
        this.objectProcessor = objectProcessor;
    }

    @Override
    public void run(GSResponse response, GraphDBManager dbManager, Node user) {

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

}
