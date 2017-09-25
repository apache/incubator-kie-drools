/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.model.Variable;

import java.util.HashMap;
import java.util.Map;

public class RuleContext {

    private final KnowledgePackageImpl pkg;
    private final RuleImpl rule;

    private final Map<Variable, Declaration> queryDeclaration = new HashMap<>();

    private final Map<Variable, Pattern> patterns = new HashMap<>();

    private int patternIndex = -1;

    RuleContext( KnowledgePackageImpl pkg, RuleImpl rule ) {
        this.pkg = pkg;
        this.rule = rule;
    }

    public KnowledgePackageImpl getPkg() {
        return pkg;
    }

    public RuleImpl getRule() {
        return rule;
    }

    int getNextPatternIndex() {
        return ++patternIndex;
    }

    void registerPattern( Variable variable, Pattern pattern ) {
        patterns.put( variable, pattern );
    }

    Pattern getPattern( Variable variable ) {
        return patterns.get( variable );
    }

    Declaration getDeclaration( Variable variable ) {
        Declaration declaration = queryDeclaration.get( variable );
        if (declaration == null) {
            Pattern pattern = patterns.get( variable );
            declaration = pattern != null ? pattern.getDeclaration() : null;
        }
        return declaration;
    }

    Declaration getQueryDeclaration( Variable variable ) {
        return queryDeclaration.get( variable );
    }

    void addQueryDeclaration(Variable variable, Declaration declaration) {
        queryDeclaration.put( variable, declaration );
    }

    public Object getBoundFact( Variable variable, Object[] objs ) {
        return objs[ patterns.get( variable ).getOffset() ];
    }
}
