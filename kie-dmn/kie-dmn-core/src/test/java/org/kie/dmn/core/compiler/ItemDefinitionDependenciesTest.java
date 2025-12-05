/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNVersion;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_6.TFunctionItem;
import org.kie.dmn.model.v1_6.TItemDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.compiler.ItemDefinitionDependenciesSorter.retrieveTypeRef;

class ItemDefinitionDependenciesTest {
    
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
        return new ItemDefinitionDependenciesSorter(TEST_NS).sort(ins, DMNVersion.getLatest());
    }

    @Test
    void generic() {
        final ItemDefinition a = build("a");
        
        final ItemDefinition b = build("b");

        final ItemDefinition c = build("c", a, b);

        final ItemDefinition d = build("d", c);
        
        final List<ItemDefinition> originalList = Arrays.asList(d, c, b, a);

        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.subList(0, 2)).contains(a,b);
        assertThat(orderedList.subList(2, 4)).contains(c,d);
    }

    @Test
    void generic2() {
        final ItemDefinition z = build("z");
        
        final ItemDefinition b = build("b");
        
        final ItemDefinition a = build("a", z);
        
        final List<ItemDefinition> originalList = Arrays.asList(z, b, a);
        
        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.indexOf(z) < orderedList.indexOf(a)).as("Index of z < a").isTrue();
    }

    @Test
    void ordering1() {
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

        assertThat(orderedList.indexOf(tAge) < orderedList.indexOf(tBorrowe)).as("Index of tAge < tBorrowe").isTrue();
        assertThat(orderedList.indexOf(temploementStatus) < orderedList.indexOf(tBorrowe)).as("Index of temploementStatus < tBorrowe").isTrue();
    }

    @Test
    void ordering2() {
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

        assertThat(orderedList.indexOf(tMortgageType) < orderedList.indexOf(tRequested)).as("Index of tMortgageType < tRequested").isTrue();
        assertThat(orderedList.indexOf(tObjective) < orderedList.indexOf(tRequested)).as("Index of tObjective < tRequested").isTrue();

        assertThat(orderedList.indexOf(tProduct) < orderedList.indexOf(tProductCollection)).as("Index of tProduct < tProductCollection").isTrue();

        assertThat(orderedList.indexOf(tMortgageType) < orderedList.indexOf(tLoanTypes)).as("Index of tMortgageType < tLoanTypes").isTrue();
        assertThat(orderedList.indexOf(tConformanceType) < orderedList.indexOf(tLoanTypes)).as("Index of tConformanceType < tLoanTypes").isTrue();
    }

    @Test
    void ordering3() {
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

        assertThat(orderedList.indexOf(tCategory) < orderedList.indexOf(tItem)).as("Index of tCategory < tItem").isTrue();
        assertThat(orderedList.indexOf(tItem) < orderedList.indexOf(tItemList)).as("Index of tItem < tItemList").isTrue();
        assertThat(orderedList.indexOf(tItemList) < orderedList.indexOf(tOrder)).as("Index of tItemList < tOrder").isTrue();

        assertThat(orderedList.indexOf(tTax) < orderedList.indexOf(tTaxList)).as("Index of tTax < tTaxList").isTrue();
    }

    @Test
    void ordering4() {
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

        assertThat(orderedList.indexOf(_TypeDecisionA1) < orderedList.indexOf(_TypeDecisionA2_x)).as("Index of _TypeDecisionA1 < _TypeDecisionA2_x").isTrue();
        assertThat(orderedList.indexOf(_TypeDecisionA2_x) < orderedList.indexOf(_TypeDecisionA3)).as("Index of _TypeDecisionA2_x < _TypeDecisionA3").isTrue();
        assertThat(orderedList.indexOf(_TypeDecisionA3) < orderedList.indexOf(_TypeDecisionB3)).as("Index of _TypeDecisionA3 < _TypeDecisionB3").isTrue();
        assertThat(orderedList.indexOf(_TypeDecisionA3) < orderedList.indexOf(_TypeDecisionC1)).as("Index of _TypeDecisionA3 < _TypeDecisionC1").isTrue();

        assertThat(orderedList.indexOf(_TypeDecisionB1) < orderedList.indexOf(_TypeDecisionB2_x)).as("Index of _TypeDecisionB1 < _TypeDecisionB2_x").isTrue();
        assertThat(orderedList.indexOf(_TypeDecisionB2_x) < orderedList.indexOf(_TypeDecisionB3)).as("Index of _TypeDecisionB2_x < _TypeDecisionB3").isTrue();
        assertThat(orderedList.indexOf(_TypeDecisionB3) < orderedList.indexOf(_TypeDecisionC1)).as("Index of _TypeDecisionB3 < _TypeDecisionC1").isTrue();
    }

    @Test
    void circular3() {
        final ItemDefinition fhirAge = build("fhirAge");
        addComponent(fhirAge, "fhirExtension");

        final ItemDefinition fhirExtension = build("fhirExtension", fhirAge);

        final ItemDefinition fhirT1 = build("fhirT1", fhirAge);

        final List<ItemDefinition> originalList = Arrays.asList(fhirT1, fhirAge, fhirExtension);

        final List<ItemDefinition> orderedList = orderingStrategy(originalList);

        assertThat(orderedList.subList(0, 2)).contains(fhirAge, fhirExtension);
        assertThat(orderedList.subList(2, 3)).contains(fhirT1);
    }

    @Test
    void testTypeRefWhenPresent() {
        QName expected = new QName(TEST_NS, "date");
        ItemDefinition item = new TItemDefinition();
        item.setTypeRef(expected);

        QName result = retrieveTypeRef(item, TEST_NS, DMNVersion.V1_2);
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void testTypeRefNull() {
        ItemDefinition item = new TItemDefinition();

        QName result = retrieveTypeRef(item, TEST_NS, DMNVersion.V1_2);
        assertThat(result).isNull();
    }

    @Test
    void testRetrieveTypeRefFromFunctionItem() {
        ItemDefinition id = new TItemDefinition();
        FunctionItem fi = new TFunctionItem();
        QName type = new QName(TEST_NS, "date");
        fi.setOutputTypeRef(type);
        id.setFunctionItem(fi);
        QName result = retrieveTypeRef(id, TEST_NS, DMNVersion.V1_3);
        assertThat(type).isEqualTo(result);
    }

    @Test
    void retrieveTypeRef_withUnsupportedDMNVersion() {
        ItemDefinition id = new TItemDefinition();
        FunctionItem fi = new TFunctionItem();
        QName type = new QName(TEST_NS, "date");
        fi.setOutputTypeRef(type);
        id.setFunctionItem(fi);
        QName result = retrieveTypeRef(id, TEST_NS, DMNVersion.V1_2);
        assertThat(result).isNull();
    }

    @Test
    public void testFunctionItemTypeRefDependency() {
        ItemDefinition functionReturningDateList = new TItemDefinition();
        ItemDefinition dateList = new TItemDefinition();
        FunctionItem functionItem = new TFunctionItem();
        functionItem.setOutputTypeRef(new QName("dateList"));

        functionReturningDateList.setName("functionReturningDateList");
        functionReturningDateList.setFunctionItem(functionItem);

        dateList.setName("dateList");
        dateList.setTypeRef(new QName(TEST_NS, "date"));

        ItemDefinitionDependenciesSorter sorter = new ItemDefinitionDependenciesSorter(TEST_NS);
        List<ItemDefinition> input = Arrays.asList(functionReturningDateList, dateList);
        List<ItemDefinition> sorted = sorter.sort(input, DMNVersion.V1_6);

        assertThat(sorted).hasSize(2);
        assertThat(sorted.indexOf(dateList)).isLessThan(sorted.indexOf(functionReturningDateList));
    }

}