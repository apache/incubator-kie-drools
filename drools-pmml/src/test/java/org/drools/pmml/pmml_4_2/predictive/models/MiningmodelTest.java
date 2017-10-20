package org.drools.pmml.pmml_4_2.predictive.models;

import static org.junit.Assert.*;

import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class MiningmodelTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_mining_model_simple.xml";


	@Test
	public void test() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
	}

}
