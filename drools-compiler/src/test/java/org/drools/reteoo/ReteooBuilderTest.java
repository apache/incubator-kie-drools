package org.drools.reteoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.reteoo.ObjectSink;
import org.drools.rule.And;
import org.drools.rule.Package;
import org.drools.visualize.ReteooJungViewer;

import com.thoughtworks.xstream.XStream;

public class ReteooBuilderTest extends TestCase {

    private boolean writeTree = false;
    
    private boolean showRete = false;

    /** Implementation specific subclasses must provide this. */
    protected RuleBase getRuleBase() throws Exception {
        return RuleBaseFactory.newRuleBase();
    }

    public void testThreeColumnsWithCosntraints() throws Exception {
        checkRuleBase( "ThreeColumnsWithCosntraints" );
    }

    public void testOneAndTwoOrs() throws Exception {
        checkRuleBase( "OneAndTwoOrs" );
    }
    
    public void testX() throws Exception {
        checkRuleBase( "OneAndTwoOrsPerson" );
    }        

    private void writeRuleBase(RuleBase ruleBase,
                               String fileName) throws IOException {
        XStream xstream = new XStream();

        PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( "src/test/resources/org/drools/reteoo/" + fileName ) ) );

        xstream.toXML( ruleBase,
                       out );
    }

    private void checkRuleBase(String name) throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_" + name + ".drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBaseImpl ruleBase = (RuleBaseImpl) getRuleBase();
        ruleBase.addPackage( pkg );

        if ( showRete ) {
            final ReteooJungViewer viewer = new ReteooJungViewer( ruleBase );
    
            javax.swing.SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    viewer.showGUI();
                }
            } );
    
            while ( viewer.isRunning() ) {
                Thread.yield();
                Thread.sleep( 100 );
            }        
        }
        
        if ( writeTree ) {
            writeRuleBase( ruleBase,
                           name );
        }

        XStream xstream = new XStream();

        RuleBase goodRuleBase = (RuleBase) xstream.fromXML( new BufferedReader( new FileReader( "src/test/resources/org/drools/reteoo/" + name ) ) );

        nodesEquals( ((RuleBaseImpl) goodRuleBase).getRete(),
                     ((RuleBaseImpl) ruleBase).getRete() );
    }

    private void nodesEquals(Object object1,
                             Object object2) {
        assertEquals( object1 + " is not of the same type as " + object2,
                      object1.getClass(),
                      object2.getClass() );

        assertEquals( object1 + " is not equal to " + object2,
                      object1,
                      object2 );

        List list1 = null;
        List list2 = null;

        if ( object1 instanceof ObjectSource ) {
            ObjectSource source1 = (ObjectSource) object1;
            ObjectSource source2 = (ObjectSource) object2;

            list1 = source1.getObjectSinksAsList();
            list2 = source2.getObjectSinksAsList();
        } else if ( object1 instanceof TupleSource ) {
            TupleSource source1 = (TupleSource) object1;
            TupleSource source2 = (TupleSource) object2;

            list1 = source1.getTupleSinks();
            list2 = source2.getTupleSinks();
        }

        assertEquals( object1.getClass() + " nodes have different number of sinks",
                      list1.size(),
                      list2.size() );

        for ( int i = 0, size = list1.size(); i < size; i++ ) {
            nodesEquals( list1.get( i ),
                         list2.get( i ) );
        }
    }
}
