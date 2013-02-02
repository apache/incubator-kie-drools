/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo;

import org.drools.common.NetworkNode;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

/**
 * A markup interface for terminal nodes
 */
public interface TerminalNode
    extends
    NetworkNode, LeftTupleSinkNode {
    
    LeftTupleSource getLeftTupleSource();
    
    LeftTupleSource unwrapTupleSource();
    
    void initInferredMask();
    
    long getDeclaredMask();
    void setDeclaredMask(long mask);

    long getInferredMask();
    void setInferredMask(long mask);
    
    public long getNegativeMask();
    
    public void setNegativeMask(long mask);
    
    public Rule getRule();

    GroupElement getSubRule();

    boolean isFireDirect();

    Declaration[] getDeclarations();

    Declaration[] getSalienceDeclarations();

    int getSequence();

    Declaration[] getTimerPeriodDeclarations();

    Declaration[] getTimerDelayDeclarations();
    
}
