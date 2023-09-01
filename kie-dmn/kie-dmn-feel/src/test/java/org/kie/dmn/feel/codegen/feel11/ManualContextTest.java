package org.kie.dmn.feel.codegen.feel11;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(result).isInstanceOf(Map.class);
        assertThat(((Map<String, String>) result)).containsEntry("street", "broadway st");
    }

}
