package com.sp.socialcommerce.neo4j;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ObjectProcessorImpl /*implements IObjectProcessor*/ {

    private static final Logger logger = LoggerFactory.getLogger(ObjectProcessorImpl.class);

    /* List of parameters of object for which node parameters will be created */
    String[] simpleParams;

    /* List of parameters of object for which connections to other nodes will be created. */
    Map<String, RelationshipType> referenceParams;

    String keyParam;
    Label nodeLabel;
    RelationshipType relType;

    public ObjectProcessorImpl(String keyParam, String[] simpleParams, /*String[] referenceParams, RelationshipType[] relTypes,*/
                               Map<String, RelationshipType> referenceParams, Label nodeLabel, RelationshipType relType) {
        this.keyParam = keyParam;
        this.simpleParams = simpleParams;
        this.referenceParams = referenceParams;
        this.nodeLabel = nodeLabel;
        this.relType = relType;
    }

    /*@Override
    public void run(GSObject object, GraphDBManager dbManager, Node nodeFrom) throws GSKeyNotFoundException {

        String keyValue = (String)object.get(keyParam);

        Node objectNode = dbManager.getNode(nodeLabel, keyParam, keyValue);

        if (objectNode == null) {

            String[][] nodeParameters = new String[simpleParams.length + 1][2];

            nodeParameters[0] = new String[]{keyParam, keyValue};

            int counter = 1;
            for (String simpleParam : simpleParams) {
                nodeParameters[counter] = new String[]{simpleParam, object.get(simpleParam).toString()};
                counter++;
            }

            objectNode = dbManager.createNode(nodeLabel, nodeParameters);
            createReferenceRelationships(object, objectNode, dbManager);

        } else {
            logger.info(String.format("Node of label %s with %s=%s already exists.", nodeLabel.name(), keyParam, keyValue));
        }


        // Create relationship
        if(!dbManager.getRelatedNodesIds(nodeFrom, relType, keyParam).contains(keyValue))
           dbManager.createRelationship(nodeFrom, objectNode, relType);

    }

    private void createReferenceRelationships(GSObject object, Node node, GraphDBManager dbManager) throws GSKeyNotFoundException {
        int counter = 0;
        Iterator<Map.Entry<String, RelationshipType>> iterator = referenceParams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, RelationshipType> entry = iterator.next();
            final String referenceParam = entry.getKey();

            String referenceValue = (String)object.get(referenceParam);
            Label refLabel = new Label() {
                @Override
                public String name() {
                    return Character.toUpperCase(referenceParam.charAt(0)) + referenceParam.substring(1);
                }
            };
            Node refNode = dbManager.getNode(refLabel, GraphConstants.GENERIC_NODE_NAME_PARAM, referenceValue);

            if(refNode == null) {
                refNode = dbManager.createNode(refLabel, new String[][]{{GraphConstants.GENERIC_NODE_NAME_PARAM, referenceValue}});
            }

//            node.createRelationshipTo(refNode, entry.getValue());
            dbManager.createRelationship(node, refNode, entry.getValue());
            counter++;
        }
    }*/
}
