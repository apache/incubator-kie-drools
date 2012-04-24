package org.drools.reteoo;


import org.drools.common.InternalRuleBase;
import org.drools.rule.Rule;
import org.drools.rule.WindowDeclaration;

import java.util.List;

public interface RuleBuilder {

    List<TerminalNode> addRule( Rule rule, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );

    void addEntryPoint( String id, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );

    WindowNode addWindowNode( WindowDeclaration window, InternalRuleBase ruleBase, ReteooBuilder.IdGenerator idGenerator );
}
