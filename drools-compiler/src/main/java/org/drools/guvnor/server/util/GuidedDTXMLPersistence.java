package org.drools.guvnor.server.util;

import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.AttributeCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GuidedDTXMLPersistence {

    private XStream                     xt;
    private static GuidedDTXMLPersistence INSTANCE = new GuidedDTXMLPersistence();

    private GuidedDTXMLPersistence() {
    	xt = new XStream(new DomDriver());
    	xt.alias("decision-table", GuidedDecisionTable.class);
    	xt.alias("attribute-column", AttributeCol.class);
    	xt.alias("condition-column", ConditionCol.class);
    	xt.alias("set-field-col", ActionSetFieldCol.class);
    	xt.alias("retract-fact-column", ActionRetractFactCol.class);
    	xt.alias("insert-fact-column", ActionInsertFactCol.class);
    }

    public static GuidedDTXMLPersistence getInstance() {
    	return INSTANCE;
    }

    public String marshal(GuidedDecisionTable dt) {
    	return xt.toXML(dt);
    }

    public GuidedDecisionTable unmarshal(String xml) {
    	if (xml == null || xml.trim().equals("")) {
    		return new GuidedDecisionTable();
    	}
    	return (GuidedDecisionTable) xt.fromXML(xml);
    }


}
