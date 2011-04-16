/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.pmml_4_0.informer;


import org.drools.ClassObjectFilter;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.informer.DomainModelAssociation;
import org.drools.informer.InvalidAnswer;
import org.drools.informer.Note;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class TestQuestionnaire extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_miningSchema.xml";
    private static final String source2 = "test_ann_iris_prediction.xml";
    private static final String sourceMix = "test_ann_mixed_inputs.xml";


    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testOverride() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.2);
        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        FactType nump = getKbase().getFactType("org.drools.pmml_4_0.test","PetalNumber");
        FactType lenp = getKbase().getFactType("org.drools.pmml_4_0.test","PetalLength");


        Collection c = getKSession().getObjects(new ClassObjectFilter(DomainModelAssociation.class));
        Iterator iter = c.iterator();
        assertEquals(2,c.size());
        DomainModelAssociation dma1 = (DomainModelAssociation) iter.next();
        if (dma1.getObject().getClass().equals(nump.getFactClass())) {
            assertEquals(5,nump.get(dma1.getObject(),"value"));
        } else if (dma1.getObject().getClass().equals(lenp.getFactClass())) {
            assertEquals(2.2,lenp.get(dma1.getObject(),"value"));
        }

        DomainModelAssociation dma2 = (DomainModelAssociation) iter.next();
        if (dma2.getObject().getClass().equals(nump.getFactClass())) {
            assertEquals(5,nump.get(dma2.getObject(),"value"));
        } else if (dma2.getObject().getClass().equals(lenp.getFactClass())) {
            assertEquals(2.2,lenp.get(dma2.getObject(),"value"));
        }


        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.5);
        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(6);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        c = getKSession().getObjects(new ClassObjectFilter(DomainModelAssociation.class));
        iter = c.iterator();
        assertEquals(3,c.size());
        while (iter.hasNext()) {
            DomainModelAssociation dma = (DomainModelAssociation) iter.next();

            if (dma.getObject().getClass().equals(nump.getFactClass())) {
                assertEquals(6,nump.get(dma.getObject(),"value"));
            } else if (dma.getObject().getClass().equals(lenp.getFactClass())) {
                Object val = lenp.get(dma.getObject(),"value");
                System.out.println("Check " + val);
                assertTrue((val.equals(new Double(2.2)) || val.equals(new Double(2.5))));
            }
        }





    }







    @Test
    public void testGenerateInputsByAnswer() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        Answer ans1 = new Answer("IRIS_MLP_PetalLength","2.5");
        Answer ans2 = new Answer("IRIS_MLP_PetalNumber","5");

        getKSession().insert(ans1);
        getKSession().insert(ans2);

        getKSession().fireAllRules();

        FactType nump = getKbase().getFactType("org.drools.pmml_4_0.test","PetalNumber");
        FactType lenp = getKbase().getFactType("org.drools.pmml_4_0.test","PetalLength");

        Collection c = getKSession().getObjects(new ClassObjectFilter(nump.getFactClass()));
        assertEquals(2,c.size());

        Iterator i1 = c.iterator();
        while (i1.hasNext()) {
            Object o = i1.next();
            if (nump.get(o,"context") != null)
                assertEquals(5, nump.get(o,"value"));
        }


        Collection d = getKSession().getObjects(new ClassObjectFilter(lenp.getFactClass()));
        assertEquals(2,d.size());

        Iterator i2 = d.iterator();
        while (i2.hasNext()) {
            Object o = i2.next();
            if (lenp.get(o,"context") != null)
                assertEquals(2.5, lenp.get(o,"value"));
        }


        System.err.println(reportWMObjects(getKSession()));

    }






    @Test
    public void testAnswerUpdate() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        FactType nump = getKbase().getFactType("org.drools.pmml_4_0.test","PetalNumber");

        getKSession().fireAllRules();

        Answer ans1 = new Answer("IRIS_MLP_PetalLength","2.5");
        Answer ans2 = new Answer("IRIS_MLP_PetalNumber","-7");

        getKSession().insert(ans1);
        getKSession().insert(ans2);

        getKSession().fireAllRules();


        Collection c = getKSession().getObjects(new ClassObjectFilter(nump.getFactClass()));
        assertEquals(2,c.size());

        Iterator i1 = c.iterator();
        while (i1.hasNext()) {
            Object o = i1.next();
            if (nump.get(o,"context") != null)
                assertEquals(5, nump.get(o,"value"));               // due to invalid as missing, and missing replaced by 5
        }



        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        Answer ans3 = new Answer("IRIS_MLP_PetalNumber","6");


        getKSession().insert(ans3);


        getKSession().fireAllRules();
        System.err.println(reportWMObjects(getKSession()));

        c = getKSession().getObjects(new ClassObjectFilter(nump.getFactClass()));
        assertEquals(2,c.size());

        i1 = c.iterator();
        while (i1.hasNext()) {
            Object o = i1.next();
            if (nump.get(o,"context") != null)
                assertEquals(6, nump.get(o,"value"));
        }





    }





    @Test
    public void testInvalidValues() throws Exception {
        setKSession(getModelSession(new String[] {source2},VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(5);

        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        FactType type = getKbase().getFactType(packageName,"PetalNumber");
        checkFirstDataFieldOfTypeStatus(type,false,false,"Neupre",5);

        assertEquals(1, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());

        assertEquals(0,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

    }






    @Test

    public void testMultipleModels() throws Exception {
        setKSession(getModelSession(new String[] {source,source2},VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
        FactType petalNumType = getKbase().getFactType(packageName,"PetalNumber");
        FactType out = getKbase().getFactType(packageName,"OutSepLen");
        FactType sepalType = getKbase().getFactType(packageName,"SEPALLEN");



        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);

        getKSession().getWorkingMemoryEntryPoint("in_PETALWID").insert(10);
        getKSession().getWorkingMemoryEntryPoint("in_SEPALWID").insert(33);
        getKSession().getWorkingMemoryEntryPoint("in_SPECIES").insert("virginica");


        getKSession().fireAllRules();

//        System.err.println(reportWMObjects(getKSession()));



        checkFirstDataFieldOfTypeStatus(petalNumType,false,false,"Neupre",4);
        assertEquals(1, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals(2,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());



        System.out.println("\n\n\n\n\n\n\n\n Before 2 \n");


        Answer ans1 = new Answer("Neupre_PetalNumber","40");
        getKSession().insert(ans1);
        getKSession().fireAllRules();


//        System.err.println(reportWMObjects(getKSession()));



        checkFirstDataFieldOfTypeStatus(petalNumType,true,false,"Neupre",40);

        assertEquals(0, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals(4,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neupre",60);
        checkFirstDataFieldOfTypeStatus(sepalType,true,false,"Neupre",60);




        System.out.println("\n\n\n\n\n\n\n\n Before 3 \n");


        Answer ans2 = new Answer("Neupre_PetalNumber","-4");
        getKSession().insert(ans2);

        getKSession().fireAllRules();


//        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(petalNumType,false,false,"Neupre",-4);

        assertEquals(1, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals(3,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neupre",60);
        assertEquals(0, getKSession().getObjects(new ClassObjectFilter(sepalType.getFactClass())).size());





        System.out.println("\n\n\n\n\n\n\n\n Now 3 \n");
//
//
//
//
//
        Answer ans3 = new Answer("Neupre_PetalNumber","44");
        getKSession().insert(ans3);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(petalNumType,true,false,"Neupre",44);

        assertEquals(0, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals(4,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neupre",61);
        checkFirstDataFieldOfTypeStatus(sepalType,true,false,"Neupre",61);

//               System.err.println(reportWMObjects(getKSession()));

//               System.out.println("\n\n\n\n\n\n\n\n Now 4 \n");
//

    }





    @Test
    public void testMixModel() throws Exception {
        setKSession(getModelSession(new String[] {sourceMix},VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();



        Answer ans1 = new Answer("Mixed_Gender","male");
        getKSession().insert(ans1);
        getKSession().getWorkingMemoryEntryPoint("in_Domicile").insert("rural");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_0"),true,false,"Mixed",1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_1"),true,false,"Mixed",0.0);

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_6"),true,false,"Mixed",0.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_7"),true,false,"Mixed",1.0);


        Answer ans2 = new Answer("Mixed_Scrambled","7");
        getKSession().insert(ans2);
        Answer ans3 = new Answer("Mixed_NoOfClaims","1");
        getKSession().insert(ans3);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_5"),true,false,"Mixed",7);

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_2"),true,false,"Mixed",0.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_3"),true,false,"Mixed",1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_4"),true,false,"Mixed",0.0);


        Answer ans4 = new Answer("Mixed_AgeOfCar","-3");
        getKSession().insert(ans4);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_8"),false,false,"Mixed",-3.0);


        Answer ans5 = new Answer("Mixed_AgeOfCar","4.0");
        getKSession().insert(ans5);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_8"),true,false,"Mixed",4.0);

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Out_Claims"),true,false,"Mixed",95.0);

    }



}
