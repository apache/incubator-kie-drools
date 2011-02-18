package org.drools.reteoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

import com.thoughtworks.xstream.XStream;

public class ReteooBuilderTest {

    private final boolean writeTree = false;

    /** Implementation specific subclasses must provide this. */
    protected RuleBase getRuleBase() throws Exception {
        return RuleBaseFactory.newRuleBase();
    }

    @Test
    public void testThreePatternsWithConstraints() throws Exception {
        //checkRuleBase( "ThreePatternsWithConstraints" );
    }

    @Test
    public void testOneAndTwoOrs() throws Exception {
        //checkRuleBase( "OneAndTwoOrs" );
    }

    @Test
    public void testOneAndTwoOrsPerson() throws Exception {
        //checkRuleBase( "OneAndTwoOrsPerson" );
    }

    private void writeRuleBase(final RuleBase ruleBase,
                               final String fileName) throws IOException {
        final XStream xstream = new XStream();

        final PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( "src/test/resources/org/drools/reteoo/" + fileName ) ) );

        xstream.toXML( ruleBase,
                       out );
    }

    private void checkRuleBase(final String name) throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_" + name + ".drl" ) ) );
        final Package pkg = builder.getPackage();

        final ReteooRuleBase ruleBase = (ReteooRuleBase) getRuleBase();
        ruleBase.addPackage( pkg );

        if ( this.writeTree ) {
            writeRuleBase( ruleBase,
                           name );
        }

        final XStream xstream = new XStream();

        final RuleBase goodRuleBase = (RuleBase) xstream.fromXML( getClass().getResourceAsStream( name ) );

        nodesEquals( ((ReteooRuleBase) goodRuleBase).getRete(),
                     (ruleBase).getRete() );
    }

    private void nodesEquals(final Object object1,
                             final Object object2) {
        assertEquals( object1 + " is not of the same type as " + object2,
                      object1.getClass(),
                      object2.getClass() );

        assertEquals( object1 + " is not equal to " + object2,
                      object1,
                      object2 );

        if ( object1 instanceof ObjectSource ) {
            final ObjectSource source1 = (ObjectSource) object1;
            final ObjectSource source2 = (ObjectSource) object2;

            final ObjectSink[] list1 = source1.sink.getSinks();
            final ObjectSink[] list2 = source2.sink.getSinks();

            assertEquals( object1.getClass() + " nodes have different number of sinks",
                          list1.length,
                          list2.length );

            for ( int i = 0, size = list1.length; i < size; i++ ) {
                nodesEquals( list1[i],
                             list2[i] );
            }
        } else if ( object1 instanceof LeftTupleSource ) {
            final LeftTupleSource source1 = (LeftTupleSource) object1;
            final LeftTupleSource source2 = (LeftTupleSource) object2;

            final LeftTupleSink[] list1 = source1.sink.getSinks();
            final LeftTupleSink[] list2 = source2.sink.getSinks();

            assertEquals( object1.getClass() + " nodes have different number of sinks",
                          list1.length,
                          list2.length );

            for ( int i = 0, size = list1.length; i < size; i++ ) {
                nodesEquals( list1[i],
                             list2[i] );
            }
        }

    }
}
