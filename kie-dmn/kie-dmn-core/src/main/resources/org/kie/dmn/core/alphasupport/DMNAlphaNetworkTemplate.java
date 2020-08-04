package org.kie.dmn.core.alphasupport;

import java.lang.Override;
import java.util.Map;

import org.drools.ancompiler.CompiledNetworkSource;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.reteoo.AlphaNode;
import org.drools.ancompiler.CompiledNetwork;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteDumper;
import org.drools.model.Index;
import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation;
import org.kie.dmn.core.compiler.alphanetbased.DMNCompiledAlphaNetwork;
import org.kie.dmn.core.compiler.alphanetbased.NetworkBuilderContext;
import org.kie.dmn.core.compiler.alphanetbased.ResultCollector;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.core.compiler.alphanetbased.TableContext;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.dmn.core.compiler.alphanetbased.AlphaNetDMNEvaluatorCompiler;
import static org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation.createIndex;

import static org.drools.core.util.MapUtils.mapValues;

// All implementations are used only for templating purposes and should never be called
public class DMNAlphaNetworkTemplate implements DMNCompiledAlphaNetwork {

    protected final ResultCollector resultCollector = new ResultCollector();
    protected CompiledNetwork compiledNetwork;

    protected final NetworkBuilderContext ctx = new NetworkBuilderContext(resultCollector);
    protected final AlphaNetworkCreation alphaNetworkCreation = new AlphaNetworkCreation(ctx);

    @Override
    public void initRete() {
        // Alpha network creation statements
        {

        }

        Index index3 = createIndex(String.class, x -> (String) x.getValue(0), "dummy");
        AlphaNode alphaDummy = alphaNetworkCreation.createAlphaNode(ctx.otn, x -> false, index3);
        alphaNetworkCreation.addResultSink(alphaDummy, "DUMMY");
    }

    @Override
    public void setCompiledAlphaNetwork(CompiledNetwork compiledAlphaNetwork) {
        this.compiledNetwork = compiledAlphaNetwork;
    }

    @Override
    public CompiledNetwork createCompiledAlphaNetwork(AlphaNetDMNEvaluatorCompiler compiler) {
        return compiler.createCompiledAlphaNetwork(ctx.otn);
    }

    @Override
    public Object evaluate(EvaluationContext evalCtx) {
        resultCollector.clearResults();
        TableContext ctx = new TableContext(evalCtx, "PROPERTY_NAMES");
        compiledNetwork.propagateAssertObject(new DefaultFactHandle(ctx), null, null);
        return resultCollector.getWithHitPolicy();
    }

    @Override
    public ResultCollector getResultCollector() {
        return resultCollector;
    }
}
