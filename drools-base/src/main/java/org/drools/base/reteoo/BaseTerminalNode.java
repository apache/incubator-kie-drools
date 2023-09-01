package org.drools.base.reteoo;

import org.drools.base.common.NetworkNode;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.util.bitmask.BitMask;

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
