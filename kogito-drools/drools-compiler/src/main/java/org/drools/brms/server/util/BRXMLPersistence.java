package org.drools.brms.server.util;

import org.drools.brms.client.modeldriven.brl.ActionFieldValue;
import org.drools.brms.client.modeldriven.brl.ActionInsertFact;
import org.drools.brms.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.brms.client.modeldriven.brl.ActionRetractFact;
import org.drools.brms.client.modeldriven.brl.ActionSetField;
import org.drools.brms.client.modeldriven.brl.ActionUpdateField;
import org.drools.brms.client.modeldriven.brl.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.brms.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brl.DSLSentence;
import org.drools.brms.client.modeldriven.brl.FactPattern;
import org.drools.brms.client.modeldriven.brl.RuleAttribute;
import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.client.modeldriven.brl.SingleFieldConstraint;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class persists the rule model to XML and back.
 * 
 * This is the 'brl' xml format (Business Rule Language).
 * 
 * @author Michael Neale
 */
public class BRXMLPersistence implements BRLPersistence {

    private XStream                     xt;
    private static final BRLPersistence INSTANCE = new BRXMLPersistence();
    public static String FILEEXTENSION = ".brl";

    private BRXMLPersistence() {
        this.xt = new XStream( new DomDriver() );

        this.xt.alias( "rule",
                  RuleModel.class );
        this.xt.alias( "fact",
                  FactPattern.class );
        this.xt.alias( "retract",
                  ActionRetractFact.class );
        this.xt.alias( "assert",
                  ActionInsertFact.class );
        this.xt.alias( "modify",
                  ActionUpdateField.class );
        this.xt.alias( "setField",
                  ActionSetField.class );
        this.xt.alias( "dslSentence",
                  DSLSentence.class );
        this.xt.alias( "compositePattern",
                  CompositeFactPattern.class );
        this.xt.alias( "attribute",
                  RuleAttribute.class );

        this.xt.alias( "fieldValue",
                  ActionFieldValue.class );
        this.xt.alias( "connectiveConstraint",
                  ConnectiveConstraint.class );
        this.xt.alias( "fieldConstraint",
                  SingleFieldConstraint.class );

        this.xt.alias( "compositeConstraint", 
                       CompositeFieldConstraint.class );
        
        this.xt.alias( "assertLogical",
                  ActionInsertLogicalFact.class );

    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see org.drools.brms.server.util.BRLPersistence#toXML(org.drools.brms.client.modeldriven.brl.RuleModel)
     */
    public String marshal(final RuleModel model) {
        return this.xt.toXML( model );
    }

    /* (non-Javadoc)
     * @see org.drools.brms.server.util.BRLPersistence#toModel(java.lang.String)
     */
    public RuleModel unmarshal(final String xml) {
        if ( xml == null ) {
            return new RuleModel();
        }
        if ( xml.trim().equals( "" ) ) {
            return new RuleModel();
        }
        return (RuleModel) this.xt.fromXML( xml );
    }

}
