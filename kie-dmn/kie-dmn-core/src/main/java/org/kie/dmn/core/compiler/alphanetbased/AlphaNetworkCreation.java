package org.kie.dmn.core.compiler.alphanetbased;

import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.BuildUtils;
import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.index.AlphaIndexImpl;

public class AlphaNetworkCreation {

    private static final BuildUtils buildUtils = new BuildUtils();

    private final BuildContext buildContext;

    public AlphaNetworkCreation(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public int getNextId() {
        return buildContext.getNextNodeId();
    }

    public <T extends Class<?>> void  addResultSink(ObjectSource source,
                                                    int row,
                                                    String columnName,
                                                    String outputEvaluationClass) {
        DMNResultCollectorAlphaSink objectSink = new DMNResultCollectorAlphaSink(getNextId(),
                                                                                 source,
                                                                                 buildContext,
                                                                                 row,
                                                                                 columnName,
                                                                                 outputEvaluationClass
        );
        source.addObjectSink(objectSink);
    }

    public CanBeInlinedAlphaNode shareAlphaNode(CanBeInlinedAlphaNode candidateAlphaNode) {
        return buildUtils.attachNode(buildContext, candidateAlphaNode);
    }

    public static <I> AlphaIndexImpl<PropertyEvaluator, I> createIndex(Class<I> indexedClass, Function1<PropertyEvaluator, I> leftExtractor, I rightValue) {
        return new AlphaIndexImpl<>(indexedClass, Index.ConstraintType.EQUAL, 1, leftExtractor, rightValue);
    }
}
