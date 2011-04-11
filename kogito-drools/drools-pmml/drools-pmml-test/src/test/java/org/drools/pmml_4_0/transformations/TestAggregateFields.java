package org.drools.pmml_4_0.transformations;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
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
public class TestAggregateFields extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_derived_fields_aggregate.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testAggregate() throws Exception {

        getKSession().getWorkingMemoryEntryPoint("in_Limit").insert(18);

        WorkingMemoryEntryPoint ep = getKSession().getWorkingMemoryEntryPoint("in_Age");
            ep.insert(10);
            ep.insert(20);
            ep.insert(30);
            ep.insert(40);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Summa"),true,false, null,90.0);

    }





}
