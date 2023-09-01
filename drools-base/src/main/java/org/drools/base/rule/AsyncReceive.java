package org.drools.base.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ClassObjectType;

public class AsyncReceive extends ConditionalElement implements PatternSource {

    private final String messageId;
    private final Pattern resultPattern;

    public AsyncReceive( Pattern resultPattern, String messageId ) {
        this.resultPattern = resultPattern;
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public Map<String, Declaration> getInnerDeclarations() {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.getInnerDeclarations -> TODO" );

    }

    @Override
    public Map<String, Declaration> getOuterDeclarations() {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.getOuterDeclarations -> TODO" );

    }

    @Override
    public Declaration resolveDeclaration( String identifier ) {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.resolveDeclaration -> TODO" );

    }

    @Override
    public ConditionalElement clone() {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.clone -> TODO" );

    }

    @Override
    public List<? extends RuleConditionElement> getNestedElements() {
        return Collections.emptyList();
    }

    @Override
    public boolean isPatternScopeDelimiter() {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.isPatternScopeDelimiter -> TODO" );

    }

    @Override
    public boolean requiresLeftActivation() {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.requiresLeftActivation -> TODO" );

    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.writeExternal -> TODO" );

    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.readExternal -> TODO" );

    }

    public Class<?> getResultClass() {
        return ((ClassObjectType)resultPattern.getObjectType()).getClassType();
    }
}
