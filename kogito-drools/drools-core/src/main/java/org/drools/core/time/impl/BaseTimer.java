package org.drools.core.time.impl;

import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public abstract class BaseTimer extends ConditionalElement{

    public Map getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }

    public List getNestedElements() {
        return Collections.EMPTY_LIST;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
}
