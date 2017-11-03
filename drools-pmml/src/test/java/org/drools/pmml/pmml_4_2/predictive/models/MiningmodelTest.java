package org.drools.pmml.pmml_4_2.predictive.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;

import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.model.AbstractModel;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.drools.pmml.pmml_4_2.model.ParameterInfo;
import org.drools.pmml.pmml_4_2.model.tree.AbstractTreeToken;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

public class MiningmodelTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_mining_model_simple.xml";


	@Test
	public void testSelectFirstSegmentFirst() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
        kSession.addEventListener(new TestAgendaListener());
        FactType tgt = kSession.getKieBase().getFactType( "org.drools.pmml.pmml_4_2.mining.segment_1", "Fld5" );
        
        PMMLRequestData request = new PMMLRequestData("1234","SampleMine");
        request.addRequestParam("Fld1", 30.0);
        request.addRequestParam("Fld2", 60.0);
        request.addRequestParam("Fld3", "false");
        request.addRequestParam("Fld4", "optA");
        kSession.insert(request);
        
        kSession.fireAllRules();
        System.out.print(reportWMObjects( kSession ));
        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtY" );

        checkGeneratedRules();
	}
	
	@Test
	public void testSelectSecondSegmentFirst() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
		
        kSession.addEventListener(new TestAgendaListener());
        FactType tgt = kSession.getKieBase().getFactType( "org.drools.pmml.pmml_4_2.mining.segment_2", "Fld5" );

        PMMLRequestData requestData = new PMMLRequestData("1234","SampleMine");
        requestData.addRequestParam(new ParameterInfo<>("Fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("Fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("Fld3",String.class,"optA"));
        kSession.insert(requestData);

        kSession.fireAllRules();
        System.out.print(  reportWMObjects( kSession ));

        AbstractTreeToken token = (AbstractTreeToken)getToken(kSession,"Missing");
        assertEquals(0.6, token.getConfidence().doubleValue(),0.0);
        assertEquals("null", token.getCurrent());
        

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtZ" );

        checkGeneratedRules();
	}
	
	
	public class TestAgendaListener extends DefaultAgendaEventListener {
	    public void matchCancelled(MatchCancelledEvent event) {
	        System.out.println("Match cancelled - "+
	            event.getCause().name()+" - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
	    }

	    public void matchCreated(MatchCreatedEvent event) {
	        System.out.println("Match created - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
	    }

	    public void afterMatchFired(AfterMatchFiredEvent event) {
	        System.out.println("After match fired - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
	    }

	    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
	        System.out.println("Agenda group popped - "+event.getAgendaGroup().getName());
	    }

	    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
	        System.out.println("Agenda group pushed - "+event.getAgendaGroup().getName());
	    }

	    public void beforeMatchFired(BeforeMatchFiredEvent event) {
	        System.out.println("Before match fired - "+event.getMatch().getRule().getPackageName()+" : "+event.getMatch().getRule().getName());
	    }
		
	}

    protected Object getToken( KieSession kSession, String treeModelName ) {
        String className = AbstractModel.PMML_JAVA_PACKAGE_NAME + "." + treeModelName + "TreeToken";
        Collection objects = kSession.getObjects(new ObjectFilter() {
            
            @Override
            public boolean accept(Object object) {
                
                return object.getClass().getName().equals(className);
            }
        });
        assertNotNull(objects);
        assertEquals( 1, objects.size());
        Iterator iter = objects.iterator();
        assert(iter.hasNext());
        return iter.next();
    }

}
