package org.drools.compiler.builder.impl;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;

public interface InternalKnowledgeBaseProvider {
    InternalKnowledgeBase getKnowledgeBase();
}
