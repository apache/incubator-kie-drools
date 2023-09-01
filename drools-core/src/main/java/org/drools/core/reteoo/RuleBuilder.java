package org.drools.core.reteoo;


import java.util.Collection;
import java.util.List;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;
import org.drools.base.rule.WindowDeclaration;

public interface RuleBuilder {

    List<TerminalNode> addRule(RuleImpl rule, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);

    void addEntryPoint(String id, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);

    WindowNode addWindowNode(WindowDeclaration window, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);
}
