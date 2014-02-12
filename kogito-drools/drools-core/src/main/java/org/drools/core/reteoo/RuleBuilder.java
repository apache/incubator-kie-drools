package org.drools.core.reteoo;


import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.WindowDeclaration;

import java.util.List;

public interface RuleBuilder {

    List<TerminalNode> addRule( RuleImpl rule, InternalKnowledgeBase kBase, ReteooBuilder.IdGenerator idGenerator );

    void addEntryPoint( String id, InternalKnowledgeBase kBase, ReteooBuilder.IdGenerator idGenerator );

    WindowNode addWindowNode( WindowDeclaration window, InternalKnowledgeBase kBase, ReteooBuilder.IdGenerator idGenerator );
}
