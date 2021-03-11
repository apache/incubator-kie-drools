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

package org.kie.dmn.feel.codegen.feel11;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ManualContextTest {

    public static final Logger LOG = LoggerFactory.getLogger(ManualContextTest.class);
    
    public class ManualContext implements CompiledFEELExpression {

        //        { an applicant : { 
        //            home address : {
        //                street name: \"broadway st\",
        //                city : \"New York\" 
        //            } 
        //           },
        //           street : an applicant.home address.street name
        //        }
        @Override
        public Map apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.openContext(feelExprCtx)
                                      .setEntry("an applicant", CompiledFEELSupport.openContext(feelExprCtx)
                                                                                   .setEntry("home address", CompiledFEELSupport.openContext(feelExprCtx)
                                                                                                                                .setEntry("street name", "broadway st")
                                                                                                                                .setEntry("city", "New York")
                                                                                                                                .closeContext())
                                                                                   .closeContext())
                                      .setEntry("street", ((java.util.Map) ((java.util.Map) feelExprCtx.getValue("an applicant")).get("home address")).get("street name"))
                                      .closeContext();
        }
    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualContext();
        LOG.debug("{}", compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);

        assertThat(result, is(instanceOf(Map.class)));
        assertThat(((Map) result).get("street"), is("broadway st"));
    }

}
