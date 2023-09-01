package org.drools.core.marshalling;

import java.io.ObjectInput;
import java.util.Map;

import org.drools.core.common.ActivationsFilter;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.phreak.PhreakTimerNode.Scheduler;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.base.rule.EntryPointId;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.Tuple;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

public interface MarshallerReaderContext extends ObjectInput {

    void addTimerNodeScheduler( int nodeId, TupleKey key, Scheduler scheduler );
    Scheduler removeTimerNodeScheduler( int nodeId, TupleKey key );

    InternalWorkingMemory getWorkingMemory();
    InternalRuleBase getKnowledgeBase();
    Map<Long, InternalFactHandle> getHandles();
    Map<Integer, LeftTuple> getTerminalTupleMap();
    ActivationsFilter getFilter();
    Map<Integer, BaseNode> getSinks();
    Map<Long, PropagationContext> getPropagationContexts();
    Map<Integer, Object> getNodeMemories();
    ObjectMarshallingStrategyStore getResolverStrategyFactory();
    ClassLoader getClassLoader();
    Map<Integer, ObjectMarshallingStrategy> getUsedStrategies();
    Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContexts();

    Object getParameterObject();
    void setParameterObject( Object parameterObject );

    Object getReaderForInt(int i);
    void setReaderForInt(int i, Object reader);

    InternalFactHandle createAccumulateHandle(EntryPointId entryPointId, ReteEvaluator reteEvaluator, LeftTuple leftTuple, Object result, int nodeId);
    InternalFactHandle createAsyncNodeHandle( Tuple leftTuple, ReteEvaluator reteEvaluator, Object object, int nodeId, ObjectTypeConf objectTypeConf );
    QueryElementFactHandle createQueryResultHandle( Tuple leftTuple, Object[] objects, int nodeId );
    InternalFactHandle createQueryHandle(Tuple leftTuple, ReteEvaluator reteEvaluator, int nodeId );
}
