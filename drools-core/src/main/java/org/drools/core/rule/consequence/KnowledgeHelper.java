package org.drools.core.rule.consequence;

import java.io.Serializable;

import org.drools.base.rule.consequence.ConsequenceContext;
import org.drools.core.WorkingMemory;

/**
 * KnowledgeHelper implementation types are injected into consequenses
 * instrumented at compile time and instances passed at runtime. It provides
 * convenience methods for users to interact with the WorkingMemory.
 * <p>
 * Of particular interest is the update method as it allows an object to
 * be modified without having to specify the facthandle, because they are not
 * passed to the consequence at runtime. To achieve this the implementation will
 * need to lookup the fact handle of the object form the WorkingMemory.
 */
public interface KnowledgeHelper extends ConsequenceContext, Serializable {
    
    void setActivation(InternalMatch internalMatch);

    default void restoreActivationOnConsequenceFailure(InternalMatch internalMatch) { }


    WorkingMemory getWorkingMemory();

    InternalMatch getMatch();


    ClassLoader getProjectClassLoader();

}
