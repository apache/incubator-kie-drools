package org.drools.pmml_4_0.predictive;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Test;



public class TestTargetsAndOutputs extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "test_target_and_output.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";




    @Test
    public void testTarget1() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",2.6);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.4);

    }






@Test
    public void testOutputFeatures() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",2.6);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.4);

    }






}
