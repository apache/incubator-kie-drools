package org.kie.dmn.core.alphasupport;

import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation;
import org.kie.dmn.core.compiler.alphanetbased.TableContext;

// All implementations are used only for templating purposes and should never be called
public class AlphaNodeCreationTemplate {

    private AlphaNetworkCreation alphaNetworkCreation;

    public AlphaNodeCreationTemplate(org.kie.dmn.core.compiler.alphanetbased.NetworkBuilderContext ctx) {
        alphaNetworkCreation = new AlphaNetworkCreation(ctx);
    }
// ref: https://github.com/kiegroup/drools/blame/cde68c4b3aee560259387373bea27b607a811c72/kie-dmn/kie-dmn-core/src/main/java/org/kie/dmn/core/compiler/DMNEvaluatorCompiler.java#L710-L713
    boolean testRxCx(TableContext x) {
        return UnaryTestRXCX.getInstance().getUnaryTests()
                .stream()
                .anyMatch(t -> {
                    Boolean result = t.apply(x.getEvalCtx(), x.getValue(99999));
                    return result != null && result;
                });
    }
}
