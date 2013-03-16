package org.drools.core.reteoo;


import org.drools.core.common.InternalRuleBase;
import org.drools.core.rule.Rule;
import org.drools.core.rule.WindowDeclaration;

import java.util.List;

public interface RuleBuilder {

    List<TerminalNode> addRule( Rule rule, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );

    void addEntryPoint( String id, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );

    WindowNode addWindowNode( WindowDeclaration window, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );
}
