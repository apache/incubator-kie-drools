package org.drools.pmml_4_0.transformations;


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
public class TestFunctionsWithNested extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_functions_nested_transformation.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testFunctions() throws Exception {
        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(10.0);


        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName, "MappedAge"), true, false, null,
                10+20 //from constants
                +10  //from aggregate
                +45.5 // from mapvalues
                +2.3 // from discretize
                +0.1 // from normcontinuous
                +1 // from normdiscrete
                );

    }





}
