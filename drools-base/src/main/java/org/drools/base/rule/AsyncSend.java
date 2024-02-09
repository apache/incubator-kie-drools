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
package org.drools.base.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.accessor.DataProvider;

public class AsyncSend extends ConditionalElement implements PatternSource {

    private final String messageId;
    private final DataProvider dataProvider;
    private final Pattern resultPattern;

    public AsyncSend( Pattern resultPattern, String messageId, DataProvider dataProvider ) {
        this.resultPattern = resultPattern;
        this.messageId = messageId;
        this.dataProvider = dataProvider;
    }

    public String getMessageId() {
        return messageId;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
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
        return true;
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.writeExternal -> TODO" );

    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException( "org.drools.core.rule.AsyncReceive.readExternal -> TODO" );

    }

    public Pattern getResultPattern() {
        return this.resultPattern;
    }

    public Class<?> getResultClass() {
        return ((ClassObjectType)resultPattern.getObjectType()).getClassType();
    }
}
