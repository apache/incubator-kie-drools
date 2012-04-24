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


package org.drools.reteoo.builder;


import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.QueryElementNode;
import org.drools.reteoo.TerminalNode;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.ObjectType;

public interface NodeFactory {


    public AlphaNode buildAlphaNode( final int id,
                                     final AlphaNodeFieldConstraint constraint,
                                     final ObjectSource objectSource,
                                     final BuildContext context );

    public TerminalNode buildTerminalNode( int id,
                                           LeftTupleSource source,
                                           Rule rule,
                                           GroupElement subrule,
                                           int subruleIndex,
                                           BuildContext context );

    public ObjectTypeNode buildObjectTypeNode( int id,
                                               EntryPointNode objectSource,
                                               ObjectType objectType,
                                               BuildContext context );
    
    public JoinNode buildJoinNode( final int id,
                                   final LeftTupleSource leftInput,
                                   final ObjectSource rightInput,
                                   final BetaConstraints binder,
                                   final BuildContext context );

    public LeftInputAdapterNode buildLeftInputAdapterNode( int nextId,
                                                           ObjectSource objectSource,
                                                           BuildContext context );

    public TerminalNode buildQueryTerminalNode( int id,
                                                LeftTupleSource source,
                                                Rule rule,
                                                GroupElement subrule,
                                                int subruleIndex,
                                                BuildContext context );

    public QueryElementNode buildQueryElementNode( int nextId,
                                                   LeftTupleSource tupleSource,
                                                   QueryElement qe,
                                                   boolean tupleMemoryEnabled,
                                                   boolean openQuery,
                                                   BuildContext context );

    public BaseNode buildFromNode( int id,
                                   DataProvider dataProvider,
                                   LeftTupleSource tupleSource,
                                   AlphaNodeFieldConstraint[] alphaNodeFieldConstraints,
                                   BetaConstraints betaConstraints,
                                   boolean tupleMemoryEnabled,
                                   BuildContext context,
                                   From from );
}
