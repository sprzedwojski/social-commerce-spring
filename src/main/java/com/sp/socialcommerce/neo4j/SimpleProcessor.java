package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.Map;

public class SimpleProcessor implements IUserResponseProcessor {

    String responseKey, nameProperty;
    RelationshipType relationshipType;
    Label label;

    public SimpleProcessor(String responseKey, String nameProperty, Label label, RelationshipType relationshipType) {
        this.responseKey = responseKey;
        this.relationshipType = relationshipType;
        this.nameProperty = nameProperty;
        this.label = label;
    }

    @Override
    public void run(Map<String, Object> responseMap, GraphDBManager dbManager, Node user) {

        if(!responseMap.containsKey(responseKey)) {
            logger.error("ResponseMap doesn't contain " + responseKey);
            return;
        }

        String name = (String)responseMap.get(responseKey);
        logger.info(responseKey + ": " + name);

        if(name != null) {
            Node node = dbManager.getNode(label, nameProperty, name);
            if (node == null) {
                String[][] nodeProperties = { {nameProperty, name} };
                node = dbManager.createNode(label, nodeProperties);
            }

            if (!dbManager.hasNodeRelationshipType(user, relationshipType)) {
                dbManager.createRelationship(user, node, relationshipType);
            } else {
                logger.info(relationshipType + " relationship already exists.");
            }
        }
    }
}
