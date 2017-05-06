/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.model.v1_1.ItemDefinition;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class ItemDefinitionDependenciesTest {
    
    private static final String TEST_NS = "https://www.drools.org/";
    private static final DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();

    private ItemDefinition build(String name, ItemDefinition... components) {
        ItemDefinition res = new ItemDefinition();
        res.setName(name);
        for ( ItemDefinition ic : components ) {
            ItemDefinition c = new ItemDefinition();
            c.setName("_" + name + "-" + ic.getName());
            c.setTypeRef(new QName(TEST_NS, ic.getName()));
            res.getItemComponent().add(c);
        }
        return res;
    }
    
    private List<ItemDefinition> orderingStrategy(List<ItemDefinition> ins) {
        return new ItemDefinitionDependenciesSorter(TEST_NS).sort(ins);
    }
    
    @Test
    public void testGeneric() {
        ItemDefinition a = build("a");
        
        ItemDefinition b = build("b");
        
        ItemDefinition c = build("c", a, b);
        
        ItemDefinition d = build("d", c);
        
        List<ItemDefinition> originalList = Arrays.asList(new ItemDefinition[]{d,c,b,a});
        
        List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.subList(0, 2), containsInAnyOrder(a,b));
        assertThat(orderedList.subList(2, 4), contains(c,d));
    }
    
    @Test
    public void testGeneric2() {
        ItemDefinition z = build("z");
        
        ItemDefinition b = build("b");
        
        ItemDefinition a = build("a", z);
        
        List<ItemDefinition> originalList = Arrays.asList(new ItemDefinition[]{z,b,a});
        
        List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertTrue("Index of z < a", orderedList.indexOf(z) < orderedList.indexOf(a));
    }
    
    @Test
    public void test1() {
        ItemDefinition tCollateralRiskCategory = build("tCollateralRiskCategory");
        ItemDefinition tCreditRiskCategory     = build("tCreditRiskCategory");
        ItemDefinition tAffordabilityCategory  = build("tAffordabilityCategory");
        ItemDefinition tLoanRecommendation     = build("tLoanRecommendation");
        ItemDefinition tLoan                   = build("tLoan");
        ItemDefinition tAge                    = build("tAge");
        ItemDefinition temploementStatus       = build("temploementStatus");
        ItemDefinition tCreditScore            = build("tCreditScore");
        ItemDefinition tRiskCategory           = build("tRiskCategory");
        ItemDefinition tIncomeRisk             = build("tIncomeRisk");
        ItemDefinition tBorrowe                = build("tBorrowe", tAge, temploementStatus);
        ItemDefinition tPrequalification       = build("tPrequalification");
        
        List<ItemDefinition> originalList = Arrays.asList(new ItemDefinition[]{
                tCollateralRiskCategory,
                tCreditRiskCategory    ,
                tAffordabilityCategory ,
                tLoanRecommendation    ,
                tLoan                  ,
                tAge                   ,
                temploementStatus      ,
                tCreditScore           ,
                tRiskCategory          ,
                tIncomeRisk            ,
                tBorrowe               ,
                tPrequalification      
        });
        
        List<ItemDefinition> orderedList = orderingStrategy(originalList);
        
        assertTrue("Index of tAge < tBorrowe", orderedList.indexOf(tAge) < orderedList.indexOf(tBorrowe));
        assertTrue("Index of temploementStatus < tBorrowe", orderedList.indexOf(temploementStatus) < orderedList.indexOf(tBorrowe));
    }
    
    @Test
    public void test2() {
        ItemDefinition tMortgageType      = build("tMortgageType");
        ItemDefinition tObjective         = build("tObjective");
        ItemDefinition tRequested         = build("tRequested", tMortgageType, tObjective);
        ItemDefinition tProduct           = build("tProduct");
        ItemDefinition tProductCollection = build("tProductCollection", tProduct);
        ItemDefinition tConformanceType   = build("tConformanceType");
        ItemDefinition tLoanTypes         = build("tLoanTypes", tMortgageType, tConformanceType);
     
        List<ItemDefinition> originalList = Arrays.asList(new ItemDefinition[]{
               tRequested,
               tProduct,
               tProductCollection,
               tMortgageType,
               tObjective,
               tConformanceType,
               tLoanTypes   
        });
        
        List<ItemDefinition> orderedList = orderingStrategy(originalList);
        
        assertTrue("Index of tMortgageType < tRequested", orderedList.indexOf(tMortgageType) < orderedList.indexOf(tRequested));
        assertTrue("Index of tObjective < tRequested", orderedList.indexOf(tObjective) < orderedList.indexOf(tRequested));

        assertTrue("Index of tProduct < tProductCollection", orderedList.indexOf(tProduct) < orderedList.indexOf(tProductCollection));
        
        assertTrue("Index of tMortgageType < tLoanTypes", orderedList.indexOf(tMortgageType) < orderedList.indexOf(tLoanTypes));
        assertTrue("Index of tConformanceType < tLoanTypes", orderedList.indexOf(tConformanceType) < orderedList.indexOf(tLoanTypes));
    }
}
