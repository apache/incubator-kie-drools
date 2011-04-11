package org.drools.pmml_4_0.transformations;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class TestAliasedFields extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_derived_fields_alias.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testKonst() throws Exception {
        FactType konst = getKbase().getFactType(packageName, "Konst");
        assertNotNull(konst);

        getKSession().fireAllRules();

        System.out.println(reportWMObjects(getKSession()));

        assertEquals(1, getKSession().getObjects().size());

        checkFirstDataFieldOfTypeStatus(konst,true,false, null,8);


    }


     @Test
    public void testAlias() throws Exception {
        FactType alias = getKbase().getFactType(packageName, "AliasAge");
        assertNotNull(alias);

        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(33);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(alias,true,false, null,33);

        refreshKSession();

        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(-1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(alias,true,true, null,-1);

    }





}
