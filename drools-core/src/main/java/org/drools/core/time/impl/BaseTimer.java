package org.drools.core.time.impl;

import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;

import java.util.Arrays;
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

    public abstract Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs);

    protected Declaration[] sortDeclarations(Map<String, Declaration> outerDeclrs, Declaration[] declrs) {
        if (declrs == null) {
            return null;
        }
        Declaration[] sortedDeclrs = Arrays.copyOf(declrs, declrs.length); // make copies as originals must not be changed
        for ( int i = 0; i < sortedDeclrs.length; i++  ) {
            sortedDeclrs[i] = outerDeclrs.get( sortedDeclrs[i].getIdentifier() );
        }
        Arrays.sort(sortedDeclrs, RuleTerminalNode.SortDeclarations.instance);
        return sortedDeclrs;
    }
}
