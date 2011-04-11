package org.drools.pmml_4_0.global;


import junit.framework.Assert;
import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.runtime.ClassObjectFilter;
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
public class TestDataDictionary extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "test_data_dic.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";


    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }

    @Test
    public void testDataTypes() throws Exception {

        FactType gender = getKbase().getFactType(packageName,"Gender");
        FactType noclaims = getKbase().getFactType(packageName,"NoOfClaims");
        FactType scrambled = getKbase().getFactType(packageName,"Scrambled");
        FactType domicile = getKbase().getFactType(packageName,"Domicile");
        FactType agecar = getKbase().getFactType(packageName,"AgeOfCar");
        FactType amklaims = getKbase().getFactType(packageName,"AmountOfClaims");


        Object g = gender.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.Gender", g.getClass().getName());
        gender.set(g,"value","M");
            Assert.assertEquals("M", gender.get(g, "value"));


        Object n = noclaims.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.NoOfClaims", n.getClass().getName());
        noclaims.set(n, "value", "> 3");
            Assert.assertEquals("> 3", noclaims.get(n, "value"));

        Object s = scrambled.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.Scrambled", s.getClass().getName());
        scrambled.set(s, "value", 1);
            Assert.assertEquals(1, scrambled.get(s, "value"));

        Object d = domicile.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.Domicile", d.getClass().getName());
        domicile.set(d, "value", "SomeWhere");
            Assert.assertEquals("SomeWhere", domicile.get(d, "value"));

        Object a = agecar.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.AgeOfCar", a.getClass().getName());
        agecar.set(a, "value", 24.3);
            Assert.assertEquals(24.3, agecar.get(a, "value"));

        Object k = amklaims.newInstance();
            Assert.assertEquals("org.drools.pmml_4_0.test.AmountOfClaims", k.getClass().getName());
        amklaims.set(k, "value", 9);
            Assert.assertEquals(9, amklaims.get(k, "value"));


        try {
            agecar.set(a,"value","Not a String field");
            Assert.fail();
        } catch (ClassCastException cce) {
            Assert.assertEquals(24.3, agecar.get(a, "value"));
        }

            Assert.assertFalse((Boolean) agecar.get(a, "valid"));
        agecar.set(a, "valid", true);
            Assert.assertTrue((Boolean) agecar.get(a, "valid"));

            Assert.assertFalse((Boolean) agecar.get(a, "missing"));
        agecar.set(a,"missing",true);
            Assert.assertTrue((Boolean) agecar.get(a, "missing"));

        //    assertFalse((Boolean) agecar.get(a, "cyclic"));
        //agecar.set(a,"cyclic",true);
        //    assertTrue((Boolean) agecar.get(a,"cyclic"));





    }



    @Test
    public void testEntryPoints() throws Exception {

        FactType gender = getKbase().getFactType(packageName,"Gender");
        FactType noclaims = getKbase().getFactType(packageName,"NoOfClaims");
        FactType scrambled = getKbase().getFactType(packageName,"Scrambled");
        FactType domicile = getKbase().getFactType(packageName,"Domicile");
        FactType agecar = getKbase().getFactType(packageName,"AgeOfCar");
        FactType amklaims = getKbase().getFactType(packageName,"AmountOfClaims");

        Assert.assertEquals(7, getKSession().getWorkingMemoryEntryPoints().size());

        WorkingMemoryEntryPoint gender_ep = getKSession().getWorkingMemoryEntryPoint("in_Gender");
            Assert.assertNotNull(gender_ep);
        WorkingMemoryEntryPoint noclaims_ep = getKSession().getWorkingMemoryEntryPoint("in_NoOfClaims");
            Assert.assertNotNull(noclaims_ep);
        WorkingMemoryEntryPoint scrambled_ep = getKSession().getWorkingMemoryEntryPoint("in_Scrambled");
            Assert.assertNotNull(scrambled_ep);
        WorkingMemoryEntryPoint domicile_ep = getKSession().getWorkingMemoryEntryPoint("in_Domicile");
            Assert.assertNotNull(domicile_ep);
        WorkingMemoryEntryPoint agecar_ep = getKSession().getWorkingMemoryEntryPoint("in_AgeOfCar");
            Assert.assertNotNull(agecar_ep);
        WorkingMemoryEntryPoint amklaims_ep = getKSession().getWorkingMemoryEntryPoint("in_AmountOfClaims");
            Assert.assertNotNull(amklaims_ep);

        gender_ep.insert("M");
        noclaims_ep.insert("> 4");
        scrambled_ep.insert(4);
        domicile_ep.insert("way out");
        agecar_ep.insert(new Double("3.4"));
        amklaims_ep.insert(9);

        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals(6, getKSession().getObjects().size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(gender.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(noclaims.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(scrambled.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(domicile.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(agecar.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new ClassObjectFilter(amklaims.getFactClass())).size());


        checkFirstDataFieldOfTypeStatus(amklaims,true,false, null,9);

        checkFirstDataFieldOfTypeStatus(domicile,false,false,null,"way out");


    }








}
