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

public class NodeTypeEnums {
    public static final short JoinNode                = 0;
    public static final short NotNode                 = 1;
    public static final short ExistsNode              = 2;
    public static final short EvalConditionNode       = 3;
    public static final short FromNode                = 4;
    //public static final short CollectNode          = 5;   // no longer used, since accumulate nodes execute collect logic now
    public static final short AccumulateNode          = 6;
    public static final short RightInputAdaterNode    = 7;
    public static final short QueryTerminalNode       = 8;
    public static final short RuleTerminalNode        = 9;
    public static final short ForallNotNode           = 10;
    public static final short UnificationNode         = 11;
    public static final short QueryRiaFixerNode       = 12;
    public static final short WindowNode              = 13;
    public static final short ElseNode                = 14;
    public static final short AlphaNode               = 15;
    public static final short ObjectTypeNode          = 16;
    public static final short PropagationQueueingNode = 17;
    public static final short QueryElementNode        = 18;
}
