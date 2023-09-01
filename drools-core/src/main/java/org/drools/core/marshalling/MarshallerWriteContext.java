package org.drools.core.marshalling;

import java.io.ObjectOutput;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalRuleBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;

public interface MarshallerWriteContext extends ObjectOutput {

    InternalRuleBase getKnowledgeBase();

    ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore();

    Object getParameterObject();
    void setParameterObject( Object parameterObject );

    InternalWorkingMemory getWorkingMemory();

    Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContext();

    Map<ObjectMarshallingStrategy, Integer> getUsedStrategies();

    Map<Integer, BaseNode> getSinks();

    long getClockTime();
    void setClockTime( long clockTime );

    boolean isMarshalProcessInstances();

    boolean isMarshalWorkItems();

    Environment getEnvironment();

    Integer getStrategyIndex( ObjectMarshallingStrategy strategy);

    Object getWriterForClass(Class<?> c);
    void setWriterForClass(Class<?> c, Object writer);
}
