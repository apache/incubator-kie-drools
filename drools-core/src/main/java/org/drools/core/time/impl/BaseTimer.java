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
package org.drools.core.time.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.rule.ConditionalElement;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.RuleConditionElement;


public abstract class BaseTimer extends ConditionalElement{

    public Map<String, Declaration> getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map<String, Declaration> getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }

    public List<? extends RuleConditionElement> getNestedElements() {
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
        Arrays.sort(sortedDeclrs, SortDeclarations.instance);
        return sortedDeclrs;
    }
}
