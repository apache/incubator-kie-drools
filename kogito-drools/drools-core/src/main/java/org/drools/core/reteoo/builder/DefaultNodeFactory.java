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

package org.drools.core.reteoo.builder;


import org.drools.core.base.ValueType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TraitObjectTypeNode;
import org.drools.core.rule.From;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.Rule;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.ObjectType;

import java.io.Serializable;

public class DefaultNodeFactory implements NodeFactory, Serializable {


    public AlphaNode buildAlphaNode( int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new AlphaNode( id, constraint, objectSource, context );
    }

    public TerminalNode buildTerminalNode( int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context ) {
        return new RuleTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public ObjectTypeNode buildObjectTypeNode( int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context ) {
        if ( objectType.getValueType().equals( ValueType.TRAIT_TYPE ) ) {
            return new TraitObjectTypeNode( id, objectSource, objectType, context );
        } else {
            return new ObjectTypeNode( id, objectSource, objectType, context );
        }
    }
    public JoinNode buildJoinNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        return new JoinNode( id, leftInput, rightInput, binder, context );
    }

    public LeftInputAdapterNode buildLeftInputAdapterNode( int id, ObjectSource objectSource, BuildContext context ) {
        return new LeftInputAdapterNode( id, objectSource, context );
    }

    public TerminalNode buildQueryTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        return new QueryTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public QueryElementNode buildQueryElementNode( int id, LeftTupleSource tupleSource, QueryElement qe, boolean tupleMemoryEnabled, boolean openQuery, BuildContext context ) {
        return new QueryElementNode( id, tupleSource, qe, tupleMemoryEnabled, openQuery, context );
    }

    public BaseNode buildFromNode(int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from) {
        return new FromNode( id, dataProvider, tupleSource, alphaNodeFieldConstraints, betaConstraints, tupleMemoryEnabled, context, from );
    }

}
