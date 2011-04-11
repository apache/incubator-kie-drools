package org.drools.pmml_4_0.predictive;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class TestMiningSchema extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "test_miningSchema.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testSchemaWithValidValues() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.2);
            getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalLength"),
                                            true, false,"IRIS_MLP",2.2);
            refreshKSession();

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(5);
            getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalNumber"),
                                            true, false,"IRIS_MLP",5);
    }



    @Test
    public void testSchemaWithOutliers() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(0.24);
            getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalLength"),
                                            true, false,"IRIS_MLP",1.0);
           refreshKSession();

        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(999.9);
            getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalLength"),
                                            true, false,"IRIS_MLP",6.9);




    }


    @Test
    public void testSchemaWithInvalid() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        //invalid as missing
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(-37.0);
            getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalLength"),
                                            false,false,null,-37.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalLength"),
                                            true, false,"IRIS_MLP",3.95);
            refreshKSession();



        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(-1);
            getKSession().fireAllRules();

            System.err.println(reportWMObjects(getKSession()));

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalNumber"),
                                            false,false,null,-1);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalNumber"),
                                            true, false,"IRIS_MLP",5);

    }



    @Test
    public void testSchemaWithMissing() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


       getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(0);
            getKSession().fireAllRules();

            System.err.println(reportWMObjects(getKSession()));

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalNumber"),
                                            false,true,null,0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"PetalNumber"),
                                            true, false,"IRIS_MLP",5);

    }









}
