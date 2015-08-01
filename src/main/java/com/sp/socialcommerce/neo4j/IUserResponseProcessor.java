package com.sp.socialcommerce.neo4j;

import com.gigya.socialize.GSResponse;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface IUserResponseProcessor {

    static final Logger logger = LoggerFactory.getLogger(IUserResponseProcessor.class);

    public void run(/*GSResponse response*/ Map<String, Object> responseMap,
                    GraphDBManager dbManager, Node user);

    public void run(GSResponse response,
                    GraphDBManager dbManager, Node user);

}
