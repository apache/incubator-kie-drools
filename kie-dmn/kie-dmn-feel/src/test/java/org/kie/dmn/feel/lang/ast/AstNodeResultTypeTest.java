package org.kie.dmn.feel.lang.ast;

import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstNodeResultTypeTest {
    private static final Logger LOG = LoggerFactory.getLogger(AstNodeResultTypeTest.class);
    private static final FEEL feel = FEEL.newInstance();
    static {
        feel.addListener(evt -> { 
            if (evt.getSeverity() == FEELEvent.Severity.ERROR) { 
                LOG.error("{}", evt);
                if ( evt.getSourceException().getCause() != null ) {
                    Throwable c = evt.getSourceException().getCause();
                    while (c != null) {
                        LOG.error(" caused by: {} {}", c.getClass(), c.getMessage() != null ? c.getMessage() : "");
                        c = c.getCause();
                    }
                    LOG.error(" [stacktraces omitted.]");
                }
            } else if (evt.getSeverity() == FEELEvent.Severity.WARN) { 
                LOG.warn("{}", evt);
            }
        } );
    }
    
    @Test
    public void test1() {
        CompiledExpressionImpl cimpl = (CompiledExpressionImpl) feel.compile("1", feel.newCompilerContext());
        System.out.println( cimpl.getExpression().getResultType() );
    }
}
