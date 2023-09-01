package org.kie.dmn.validation.dtanalysis;

import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.impl.FEELImpl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DMNDTAnalyserValueFromNodeVisitorTest {

    private final FEELImpl tFEEL = (FEELImpl) org.kie.dmn.feel.FEEL.newInstance();

    /**
     * None of these are valid FEEL expression ootb and cannot be used to determine discrete value in the domain
     */
    @Test
    public void smokeTest() {
        DMNDTAnalyserValueFromNodeVisitor ut = new DMNDTAnalyserValueFromNodeVisitor(Collections.emptyList());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("date()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("date and time()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("time()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("number()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("string()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("duration()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("years and months duration()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("x()")));
    }

    private FunctionInvocationNode compile(String fnInvFEEL) {
        return (FunctionInvocationNode) tFEEL.compileExpression(fnInvFEEL, tFEEL.newCompilerContext()).getInterpreted().getASTNode();
    }
}
