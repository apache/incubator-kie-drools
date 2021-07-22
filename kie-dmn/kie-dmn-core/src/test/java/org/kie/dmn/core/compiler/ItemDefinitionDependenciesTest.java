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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_1.TItemDefinition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;


public class ItemDefinitionDependenciesTest {
    
    private static final String TEST_NS = "https://www.drools.org/";

    private ItemDefinition build(final String name, final ItemDefinition... components) {
        final ItemDefinition res = new TItemDefinition();
        res.setName(name);
        for ( final ItemDefinition ic : components ) {
            addComponent(res, ic.getName());
        }
        return res;
    }
    
    private void addComponent(ItemDefinition i, String componentName) {
        final ItemDefinition c = new TItemDefinition();
        c.setName("_" + i.getName() + "-" + componentName);
        c.setTypeRef(new QName(TEST_NS, componentName));
        i.getItemComponent().add(c);
    }

    private List<ItemDefinition> orderingStrategy(final List<ItemDefinition> ins) {
        return new ItemDefinitionDependenciesSorter(TEST_NS).sort(ins);
    }
    
    @Test
    public void testGeneric() {
        final ItemDefinition a = build("a");
        
        final ItemDefinition b = build("b");
        
        final ItemDefinition c = build("c", a, b);
        
        final ItemDefinition d = build("d", c);
        
        final List<ItemDefinition> originalList = Arrays.asList(d, c, b, a);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.subList(0, 2), containsInAnyOrder(a,b));
        assertThat(orderedList.subList(2, 4), contains(c,d));
    }
    
    @Test
    public void testGeneric2() {
        final ItemDefinition z = build("z");
        
        final ItemDefinition b = build("b");
        
        final ItemDefinition a = build("a", z);
        
        final List<ItemDefinition> originalList = Arrays.asList(z, b, a);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertTrue("Index of z < a", orderedList.indexOf(z) < orderedList.indexOf(a));
    }
    
    @Test
    public void testOrdering1() {
        final ItemDefinition tCollateralRiskCategory = build("tCollateralRiskCategory");
        final ItemDefinition tCreditRiskCategory     = build("tCreditRiskCategory");
        final ItemDefinition tAffordabilityCategory  = build("tAffordabilityCategory");
        final ItemDefinition tLoanRecommendation     = build("tLoanRecommendation");
        final ItemDefinition tLoan                   = build("tLoan");
        final ItemDefinition tAge                    = build("tAge");
        final ItemDefinition temploementStatus       = build("temploementStatus");
        final ItemDefinition tCreditScore            = build("tCreditScore");
        final ItemDefinition tRiskCategory           = build("tRiskCategory");
        final ItemDefinition tIncomeRisk             = build("tIncomeRisk");
        final ItemDefinition tBorrowe                = build("tBorrowe", tAge, temploementStatus);
        final ItemDefinition tPrequalification       = build("tPrequalification");
        
        final List<ItemDefinition> originalList = Arrays.asList(tCollateralRiskCategory,
                                                                tCreditRiskCategory,
                                                                tAffordabilityCategory,
                                                                tLoanRecommendation,
                                                                tLoan,
                                                                tAge,
                                                                temploementStatus,
                                                                tCreditScore,
                                                                tRiskCategory,
                                                                tIncomeRisk,
                                                                tBorrowe,
                                                                tPrequalification);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);
        
        assertTrue("Index of tAge < tBorrowe", orderedList.indexOf(tAge) < orderedList.indexOf(tBorrowe));
        assertTrue("Index of temploementStatus < tBorrowe", orderedList.indexOf(temploementStatus) < orderedList.indexOf(tBorrowe));
    }
    
    @Test
    public void testOrdering2() {
        final ItemDefinition tMortgageType      = build("tMortgageType");
        final ItemDefinition tObjective         = build("tObjective");
        final ItemDefinition tRequested         = build("tRequested", tMortgageType, tObjective);
        final ItemDefinition tProduct           = build("tProduct");
        final ItemDefinition tProductCollection = build("tProductCollection", tProduct);
        final ItemDefinition tConformanceType   = build("tConformanceType");
        final ItemDefinition tLoanTypes         = build("tLoanTypes", tMortgageType, tConformanceType);
     
        final List<ItemDefinition> originalList = Arrays.asList(tRequested,
                                                                tProduct,
                                                                tProductCollection,
                                                                tMortgageType,
                                                                tObjective,
                                                                tConformanceType,
                                                                tLoanTypes);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);
        
        assertTrue("Index of tMortgageType < tRequested", orderedList.indexOf(tMortgageType) < orderedList.indexOf(tRequested));
        assertTrue("Index of tObjective < tRequested", orderedList.indexOf(tObjective) < orderedList.indexOf(tRequested));

        assertTrue("Index of tProduct < tProductCollection", orderedList.indexOf(tProduct) < orderedList.indexOf(tProductCollection));
        
        assertTrue("Index of tMortgageType < tLoanTypes", orderedList.indexOf(tMortgageType) < orderedList.indexOf(tLoanTypes));
        assertTrue("Index of tConformanceType < tLoanTypes", orderedList.indexOf(tConformanceType) < orderedList.indexOf(tLoanTypes));
    }
    
    @Test
    public void testOrdering3() {
        final ItemDefinition tNumberList = build("tNumberList");
        final ItemDefinition tTax        = build("tTax");
        final ItemDefinition tStateModel = build("tStateModel");
        final ItemDefinition tTaxList    = build("tTaxList", tTax);
        final ItemDefinition tCategory   = build("tCategory");
        final ItemDefinition tItem       = build("tItem", tCategory);
        final ItemDefinition tItemList   = build("tItemList", tItem);
        final ItemDefinition tOrder      = build("tOrder", tItemList);
     
        final List<ItemDefinition> originalList = Arrays.asList(tOrder,
                                                                tItem,
                                                                tCategory,
                                                                tNumberList,
                                                                tItemList,
                                                                tTax,
                                                                tStateModel,
                                                                tTaxList);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);
        
        assertTrue("Index of tCategory < tItem", orderedList.indexOf(tCategory) < orderedList.indexOf(tItem));
        assertTrue("Index of tItem < tItemList", orderedList.indexOf(tItem) < orderedList.indexOf(tItemList));
        assertTrue("Index of tItemList < tOrder", orderedList.indexOf(tItemList) < orderedList.indexOf(tOrder));
        
        assertTrue("Index of tTax < tTaxList", orderedList.indexOf(tTax) < orderedList.indexOf(tTaxList));
    }

    @Test
    public void testOrdering4() {
        final ItemDefinition _TypeDecisionA1   = build("TypeDecisionA1");
        final ItemDefinition _TypeDecisionA2_x = build("TypeDecisionA2.x", _TypeDecisionA1);
        final ItemDefinition _TypeDecisionA3   = build("TypeDecisionA3", _TypeDecisionA2_x);
        final ItemDefinition _TypeDecisionB1   = build("TypeDecisionB1");
        final ItemDefinition _TypeDecisionB2_x = build("TypeDecisionB2.x", _TypeDecisionB1);
        final ItemDefinition _TypeDecisionB3   = build("TypeDecisionB3", _TypeDecisionB2_x, _TypeDecisionA3);
        final ItemDefinition _TypeDecisionC1   = build("TypeDecisionC1", _TypeDecisionA3, _TypeDecisionB3);
        final ItemDefinition _TypeDecisionC4   = build("TypeDecisionC4");

        final List<ItemDefinition> originalList = Arrays.asList(_TypeDecisionA1,
                                                                _TypeDecisionA2_x,
                                                                _TypeDecisionA3,
                                                                _TypeDecisionB1,
                                                                _TypeDecisionB2_x,
                                                                _TypeDecisionB3,
                                                                _TypeDecisionC1,
                                                                _TypeDecisionC4);

        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertTrue("Index of _TypeDecisionA1 < _TypeDecisionA2_x", orderedList.indexOf(_TypeDecisionA1) < orderedList.indexOf(_TypeDecisionA2_x));
        assertTrue("Index of _TypeDecisionA2_x < _TypeDecisionA3", orderedList.indexOf(_TypeDecisionA2_x) < orderedList.indexOf(_TypeDecisionA3));
        assertTrue("Index of _TypeDecisionA3 < _TypeDecisionB3", orderedList.indexOf(_TypeDecisionA3) < orderedList.indexOf(_TypeDecisionB3));
        assertTrue("Index of _TypeDecisionA3 < _TypeDecisionC1", orderedList.indexOf(_TypeDecisionA3) < orderedList.indexOf(_TypeDecisionC1));

        assertTrue("Index of _TypeDecisionB1 < _TypeDecisionB2_x", orderedList.indexOf(_TypeDecisionB1) < orderedList.indexOf(_TypeDecisionB2_x));
        assertTrue("Index of _TypeDecisionB2_x < _TypeDecisionB3", orderedList.indexOf(_TypeDecisionB2_x) < orderedList.indexOf(_TypeDecisionB3));
        assertTrue("Index of _TypeDecisionB3 < _TypeDecisionC1", orderedList.indexOf(_TypeDecisionB3) < orderedList.indexOf(_TypeDecisionC1));
    }

    @Test
    public void testCircular3() {
        final ItemDefinition fhirAge = build("fhirAge");
        addComponent(fhirAge, "fhirExtension");

        final ItemDefinition fhirExtension = build("fhirExtension", fhirAge);

        final ItemDefinition fhirT1 = build("fhirT1", fhirAge);

        final List<ItemDefinition> originalList = Arrays.asList(fhirT1, fhirAge, fhirExtension);

        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.subList(0, 2), containsInAnyOrder(fhirAge, fhirExtension));
        assertThat(orderedList.subList(2, 3), contains(fhirT1));
    }
}
