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

import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.GlobalExtractor;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.model.Global;
import org.drools.model.Variable;
import org.kie.api.definition.type.Role;

import java.util.HashMap;
import java.util.Map;

public class RuleContext {

    private final KnowledgePackageImpl pkg;
    private final RuleImpl rule;

    private final Map<Variable, Declaration> queryDeclaration = new HashMap<>();
    private final Map<Variable, Declaration> innerDeclaration = new HashMap<>();

    private final Map<Variable, Pattern> patterns = new HashMap<>();

    private int patternIndex = -1;

    private Map<Class<?>, ClassObjectType> objectTypeCache = new HashMap<>();

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
        if ( variable.isFact() ) {
            Declaration declaration = innerDeclaration.get( variable );
            if (declaration == null) {
                declaration = queryDeclaration.get( variable );
            }
            if (declaration == null) {
                Pattern pattern = patterns.get( variable );
                declaration = pattern != null ? pattern.getDeclaration() : null;
            }
            return declaration;
        } else {
            Global global = (( Global ) variable);
            ClassObjectType objectType = getObjectType( global.getType().asClass() );
            InternalReadAccessor globalExtractor = new GlobalExtractor( global.getName(), objectType );
            return new Declaration( global.getName(), globalExtractor, new Pattern( 0, objectType ) );
        }
    }

    Declaration getQueryDeclaration( Variable variable ) {
        return queryDeclaration.get( variable );
    }

    void addQueryDeclaration(Variable variable, Declaration declaration) {
        queryDeclaration.put( variable, declaration );
    }

    void addInnerDeclaration(Variable variable, Declaration declaration) {
        innerDeclaration.put( variable, declaration );
    }

    public Object getBoundFact( Variable variable, Object[] objs ) {
        return objs[ patterns.get( variable ).getOffset() ];
    }

    ClassObjectType getObjectType( Class<?> patternClass ) {
        return objectTypeCache.computeIfAbsent( patternClass, c -> new ClassObjectType( c, isEvent( c ) ) );
    }

    private boolean isEvent( Class<?> patternClass ) {
        Role role = patternClass.getAnnotation( Role.class );
        return role != null && role.value() == Role.Type.EVENT;
    }
}
