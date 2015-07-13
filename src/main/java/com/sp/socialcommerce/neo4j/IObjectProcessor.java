package com.sp.socialcommerce.neo4j;


import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.Map;

public interface IObjectProcessor {

    public void run(GSObject object, GraphDBManager dbManager, Node nodeFrom) throws GSKeyNotFoundException;

}
