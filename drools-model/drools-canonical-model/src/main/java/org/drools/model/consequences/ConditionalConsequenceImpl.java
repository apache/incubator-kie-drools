/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.consequences;

import org.drools.model.ConditionalConsequence;
import org.drools.model.Consequence;
import org.drools.model.view.ExprViewItem;

public class ConditionalConsequenceImpl implements ConditionalConsequence {

    private final ExprViewItem expr;
    private final Consequence thenConsequence;
    private final ConditionalConsequence elseConsequence;

    public ConditionalConsequenceImpl( ExprViewItem expr, Consequence thenConsequence, ConditionalConsequence elseConsequence ) {
        this.expr = expr;
        this.thenConsequence = thenConsequence;
        this.elseConsequence = elseConsequence;
    }

    @Override
    public ExprViewItem getExpr() {
        return expr;
    }

    @Override
    public Consequence getThen() {
        return thenConsequence;
    }

    @Override
    public ConditionalConsequence getElse() {
        return elseConsequence;
    }
}
