package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface IUserResponseProcessor {

    Logger logger = LoggerFactory.getLogger(IUserResponseProcessor.class);

    void run(Map<String, Object> responseMap,
                    GraphDBManager dbManager, Node user);

}
