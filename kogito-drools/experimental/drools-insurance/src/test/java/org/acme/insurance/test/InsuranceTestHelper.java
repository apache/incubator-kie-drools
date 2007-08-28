package org.acme.insurance.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.agent.RuleAgent;
import org.drools.compiler.PackageBuilder;

public class InsuranceTestHelper {
    private RuleBase        rulebase;
    private StatefulSession session;

    public StatefulSession getSession() {
        try {
            //rulebase = loadRuleBaseFromRuleAgent();
            rulebase = loadRuleBaseFromDRL();

            session = rulebase.newStatefulSession();

            session.startProcess( "insuranceProcess" );
            session.fireAllRules();

            return session;

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public RuleBase loadRuleBaseFromRuleAgent() {
        RuleAgent agent = RuleAgent.newRuleAgent( "/brmsdeployedrules.properties" );
        RuleBase rulebase = agent.getRuleBase();
        return rulebase;
    }

    protected Object serializeIn(final byte[] bytes) throws IOException,
                                                    ClassNotFoundException {
        final ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        final Object obj = in.readObject();
        in.close();
        return obj;
    }

    protected byte[] serializeOut(final Object obj) throws IOException {
        // Serialize to a byte array
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        final byte[] bytes = bos.toByteArray();
        return bytes;
    }

    private RuleBase loadRuleBaseFromDRL() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( getTechnicalRules( "/approval/insurancefactor.drl" ) );
        builder.addPackageFromDrl( getTechnicalRules( "/approval/approval.drl" ) );
        builder.addPackageFromDrl( getTechnicalRules( "/approval/calculateInsurance.drl" ) );
        builder.addPackageFromDrl( getTechnicalRules( "/approval/marginalage.dslr" ),
                                   getTechnicalRules( "/approval/acme.dsl" ) );
        builder.addRuleFlow( getTechnicalRules( "/approval/insurance-process.rfm" ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        
        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        final byte[] ast = serializeOut( map );
        map = (Map) serializeIn( ast );
        ruleBase = (RuleBase) map.get( "x" );


        return ruleBase;
    }

    private Reader getTechnicalRules(String name) {

        InputStream stream = this.getClass().getResourceAsStream( name );

        return new InputStreamReader( stream );

    }

}
