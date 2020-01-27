/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.pmml_4_2.predictive.models;

import java.util.Collection;
import java.util.Iterator;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.kie.pmml.pmml_4_2.model.AbstractModel;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecisionTreeTest extends DroolsAbstractPMMLTest {

    private static final String DECISION_TREES_FOLDER = "org/kie/pmml/pmml_4_2/";

    private static final boolean VERBOSE = false;
    private static final String source1 = DECISION_TREES_FOLDER + "test_tree_simple.pmml";
    private static final String source2 = DECISION_TREES_FOLDER + "test_tree_missing.pmml";
    private static final String source3 = DECISION_TREES_FOLDER + "test_tree_handwritten.pmml";
    private static final String source4 = DECISION_TREES_FOLDER + "test_tree_from_wtavg.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";

    private static final String TREE_RETURN_NULL_NOTRUECHILD_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_null_notruechild_strategy.pmml";
    private static final String TREE_RETURN_LAST_NOTRUE_CHILD_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_last_notruechild_strategy.pmml";
    private static final String TREE_DEFAULT_CHILD_MISSING_STRATEGY =
            DECISION_TREES_FOLDER + "test_tree_default_child_missing_value_strategy.pmml";
    private static final String TREE_LAST_CHILD_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_last_missing_value_strategy.pmml";
    private static final String TREE_RETURN_NULL_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_null_missing_value_strategy.pmml";
    private static final String TREE_WEIGHTED_CONFIDENCE_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_weightedconfidence_missing_value_strategy.pmml";

    @After
    public void tearDown() {
//        getKSession().dispose();
    }

    @Test
    public void testTreeWithNumericValueOutcome() throws Exception {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Iris1",
                                                                                     ResourceFactory.newClassPathResource(source4),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("1234","Iris1");
        request.addRequestParam("petal_length", 1.75);
        request.addRequestParam("sepal_width", 2.1);

        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        Number sepal_length = resultHolder.getResultValue("Sepal_length", "value", Double.class).orElse(null);
        assertNotNull(sepal_length);
        assertEquals(5.005660, sepal_length.doubleValue(), 1e-6);
    }

    @Test
    public void testTreeFromMiningModel() throws Exception {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleMineTree1",
                                                                                     ResourceFactory.newClassPathResource("org/kie/pmml/pmml_4_2/test_tree_from_mm.pmml"),
                                                                                     null);
        PMMLRequestData request = new PMMLRequestData("1234", "SampleMineTree1");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");

        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        assertNotNull(resultHolder.getResultVariables());
        assertNotNull(resultHolder.getResultValue("Fld5", null));
        String value = resultHolder.getResultValue("Fld5", "value", String.class).orElse(null);
        assertEquals("tgtY",value);
    }

    @Test
    public void testSimpleTree() throws Exception {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(source1),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");

        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        Object obj = resultHolder.getResultValue("Fld5", null);
        assertNotNull(obj);

        String targetValue = resultHolder.getResultValue("Fld5", "value", String.class).orElse(null);
        assertEquals("tgtY",targetValue);
    }

    @Test
    public void testReturnNullNoTrueChildPredictionStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_RETURN_NULL_NOTRUECHILD_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld2", "value", String.class)).isEmpty();
    }

    @Test
    public void testReturnLastNoTrueChildPredictionStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_RETURN_LAST_NOTRUE_CHILD_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtX");
    }

    @Test
    public void testLastPredictionMissingValueStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_LAST_CHILD_MISSING_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtA");
    }

    @Test
    public void testNullPredictionMissingValueStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_RETURN_NULL_MISSING_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isNull();
    }

    @Test
    public void testDefaultChildMissingValueStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_DEFAULT_CHILD_MISSING_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtZ");
    }

    @Test
    public void testWeightedConfidenceMissingValueStrategy() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("TreeTest",
                                                                                     ResourceFactory.newClassPathResource(TREE_WEIGHTED_CONFIDENCE_MISSING_STRATEGY),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = helper.submitRequest(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtX");
    }

    protected Object getToken( KieSession kSession, String treeModelName ) {
        String className = AbstractModel.PMML_JAVA_PACKAGE_NAME + "." + treeModelName + "TreeToken";
        Collection objects = kSession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {

                return object.getClass().getName().equals(className);
            }
        });
        assertNotNull(objects);
        assertEquals( 1, objects.size());
        Iterator iter = objects.iterator();
        assert(iter.hasNext());
        return iter.next();
    }


    @Test
    public void testMissingTree() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source2);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Missing", res, null);
        assertNotNull(helper);

        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3",String.class,"optA"));

        PMML4Result resultHolder = helper.submitRequest(requestData);
        assertNotNull(resultHolder);

        AbstractTreeToken missingTreeToken = resultHolder.getResultValue("MissingTreeToken", null,AbstractTreeToken.class).orElse(null);
        assertNotNull(missingTreeToken);

        Double tokVal = resultHolder.getResultValue("MissingTreeToken", "confidence",Double.class).orElse(null);
        assertNotNull(tokVal);
        assertEquals(0.6,tokVal,0.0);

        String current = resultHolder.getResultValue("MissingTreeToken", "current", String.class).orElse(null);
        assertNotNull(current);
        assertEquals("null",current);

        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        
        String fld9Val = resultHolder.getResultValue("Fld9", "value", String.class).orElse(null);
        assertNotNull(fld9Val);
        assertEquals("tgtZ",fld9Val);

        requestData = new PMMLRequestDataBuilder("2345","Missing")
                .addParameter("fld2", 60.0, Double.class)
                .addParameter("fld3", "optA", String.class)
                .build();
        resultHolder = helper.submitRequest(requestData);
        assertNotNull(resultHolder);
        assertEquals("OK",resultHolder.getResultCode());
        
        assertNotNull(resultHolder.getResultValue("Fld9", null));
        fld9Val = resultHolder.getResultValue("Fld9", "value", String.class).orElse(null);
        assertEquals("tgtX",fld9Val);
    }


    @Test
    public void testMissingTreeWeighted1() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source2);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Missing", res, null);
        assertNotNull(helper);

        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3", String.class, "optA"));
        
        PMML4Result resultHolder = helper.submitRequest(requestData);
        
        AbstractTreeToken missingTreeToken = (AbstractTreeToken) resultHolder.getResultValue("MissingTreeToken", null);
        assertNotNull(missingTreeToken);
        assertEquals(0.8, missingTreeToken.getConfidence(), 0.0);
        assertEquals("null", missingTreeToken.getCurrent());
        assertEquals(50.0, missingTreeToken.getTotalCount(), 0.0);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        String value = (String)resultHolder.getResultValue("Fld9", "value");
        assertNotNull(value);
        assertEquals("tgtX",value);
        
    }



    @Test
    public void testMissingTreeWeighted2() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source2);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Missing", res, null);

        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3", String.class, "miss"));
        PMML4Result resultHolder = helper.submitRequest(requestData);

        AbstractTreeToken token = (AbstractTreeToken)resultHolder.getResultValue("MissingTreeToken", null);
        assertNotNull(token);
        assertEquals(0.6, token.getConfidence(), 1e-6);
        assertEquals("null", token.getCurrent());
        assertEquals(100.0, token.getTotalCount(), 0.0);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        String value = (String)resultHolder.getResultValue("Fld9", "value");
        assertNotNull(value);
        assertEquals("tgtX",value);
    }


}
