package org.drools.core.reteoo;

import org.drools.core.common.NetworkNode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.util.bitmask.BitMask;

public interface BaseTerminalNode extends NetworkNode {
    Declaration[] getAllDeclarations();

    Declaration[] getRequiredDeclarations();

    Declaration[] getSalienceDeclarations();

    void initInferredMask();

    BitMask getDeclaredMask();

    void setDeclaredMask(BitMask mask);

    BitMask getInferredMask();

    void setInferredMask(BitMask mask);

    BitMask getNegativeMask();

    void setNegativeMask(BitMask mask);

    RuleImpl getRule();

    GroupElement getSubRule();

    boolean isFireDirect();

    int getSubruleIndex();
}
