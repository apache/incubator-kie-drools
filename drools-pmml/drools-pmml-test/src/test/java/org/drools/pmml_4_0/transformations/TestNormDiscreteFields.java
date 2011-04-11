package org.drools.pmml_4_0.transformations;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class TestNormDiscreteFields extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_derived_fields_normDiscrete.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";


    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }




    @Test
    public void testNormDiscrete() throws Exception {

        FactType fld = getKbase().getFactType(packageName,"CatField");
        FactType val1 = getKbase().getFactType(packageName,"IsValue1");
        FactType val2 = getKbase().getFactType(packageName,"IsValue2");

        assertNotNull(getKSession().getWorkingMemoryEntryPoint("in_CatField"));

        //value is "missing" for age, so should be mapped by the mapMissingTo policy
        getKSession().getWorkingMemoryEntryPoint("in_CatField").insert("Value1");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value1");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 1.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 0.0);

        refreshKSession();

        getKSession().getWorkingMemoryEntryPoint("in_CatField").insert("Value2");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value2");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 0.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 1.0);

        refreshKSession();



        getKSession().getWorkingMemoryEntryPoint("in_CatField").insert("Value3");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value3");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 0.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 0.0);

        refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_CatField").insert("Value0");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, false, true, null, "Value0");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 2.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 2.0);

        refreshKSession();

    }








}
