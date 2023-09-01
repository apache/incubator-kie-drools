package org.kie.dmn.feel.parser.feel11;

import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.codegen.feel11.ProcessedFEELUnit;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.impl.CompilerContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.parser.feel11.FEELParserTest.assertLocation;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class FEELParserSeverityTest {

    private static final Logger LOG = LoggerFactory.getLogger(FEELParserSeverityTest.class);

    @Test
    public void testUnexistentOperatorGTGT() {
        // RHDM-1119 
        String inputExpression = "1 >> 2";
        ASTNode number = parseSeverity(inputExpression, FEELEvent.Severity.WARN);

        assertThat(number).isInstanceOf(InfixOpNode.class);
        assertThat(number.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertLocation(inputExpression, number);
    }

    @Test
    public void testUnexistentOperatorGTGTGT() {
        // RHDM-1119 
        String inputExpression = "1 >>> 2";
        ASTNode number = parseSeverity(inputExpression, FEELEvent.Severity.WARN);

        assertThat(number).isInstanceOf(InfixOpNode.class);
        assertThat(number.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertLocation(inputExpression, number);
    }

    @Test
    public void testUnexistentOperatorEQEQ() {
        // RHDM-1119 
        String inputExpression = "1 == 2";
        ASTNode number = parseSeverity(inputExpression, FEELEvent.Severity.WARN);

        assertThat(number).isInstanceOf(InfixOpNode.class);
        assertThat(number.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertLocation(inputExpression, number);
    }

    @Test
    public void testUnexistentOperatorInvokeLTLT() {
        // RHDM-1119 
        String inputExpression = "{ m: <<18 }.m(16)";
        ASTNode number = parseSeverity(inputExpression, FEELEvent.Severity.WARN);

        assertThat(number).isInstanceOf(FunctionInvocationNode.class);
        assertThat(number.getResultType()).isInstanceOf(Type.class);
        assertLocation(inputExpression, number);
    }

    private ASTNode parseSeverity(String input, FEELEvent.Severity severity) {
        FEELEventListenersManager mgr = new FEELEventListenersManager();
        CompilerContext ctx = new CompilerContextImpl(mgr);
        final FEELEventListener listener = mock(FEELEventListener.class);
        mgr.addListener(listener);
        mgr.addListener(e -> LOG.info("{}", e));

        ProcessedExpression processedExpression = new ProcessedExpression(input, ctx, ProcessedFEELUnit.DefaultMode.Interpreted, Collections.emptyList());

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(listener, atLeastOnce()).onEvent(captor.capture());
        assertThat(captor.getValue().getSeverity()).isEqualTo(severity);

        return processedExpression.getInterpreted().getASTNode();
    }
}
