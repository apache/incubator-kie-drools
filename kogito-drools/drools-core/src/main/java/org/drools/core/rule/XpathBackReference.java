/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.spi.PatternExtractor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XpathBackReference {
    public static final String BACK_REFERENCE_HEAD = "$back$ref$";

    private final Pattern pattern;
    private final List<Class<?>> backReferenceClasses;

    private Map<String, Declaration> declarations = Collections.EMPTY_MAP;

    private MapAdapter declarationMap;

    public XpathBackReference( Pattern pattern, List<Class<?>> backReferenceClasses ) {
        this.pattern = pattern;
        this.backReferenceClasses = backReferenceClasses;
    }

    public void reset() {
        declarations.clear();
    }

    public List<Class<?>> getBackReferenceClasses() {
        return backReferenceClasses;
    }

    public Declaration getDeclaration(Pattern pattern, String id) {
        if (!id.startsWith( BACK_REFERENCE_HEAD )) {
            return null;
        }
        Declaration declaration = declarations.get(id);
        if (declaration != null) {
            return declaration;
        }

        int backRefPos = Integer.parseInt( id.substring( XpathBackReference.BACK_REFERENCE_HEAD.length() ) );
        int relativeOffset = backReferenceClasses.size() - 1 - backRefPos;
        declaration = new Declaration( id,
                                       new PatternExtractor( new ClassObjectType( backReferenceClasses.get(backRefPos) ) ),
                                       new RelativePattern( pattern, relativeOffset ),
                                       true );

        if (declarations == Collections.EMPTY_MAP) {
            declarations = new HashMap<String, Declaration>();
        }

        declarations.put( id, declaration );
        return declaration;
    }

    public Map<String, Declaration> getDeclarationMap() {
        if (declarationMap == null) {
            declarationMap = new MapAdapter();
        }
        return declarationMap;
    }

    public static class RelativePattern extends Pattern {
        private final Pattern pattern;
        private final int relativeOffset;

        public RelativePattern( Pattern pattern, int relativeOffset ) {
            this.pattern = pattern;
            this.relativeOffset = relativeOffset;
        }

        @Override
        public int getOffset() {
            return pattern.getOffset() + relativeOffset;
        }
    }

    public class MapAdapter implements Map<String, Declaration> {

        @Override
        public int size() {
            return pattern.getDeclarations().size() + declarations.size();
        }

        @Override
        public boolean isEmpty() {
            return pattern.getDeclarations().isEmpty() && declarations.isEmpty();
        }

        @Override
        public boolean containsKey( Object key ) {
            return pattern.getDeclarations().containsKey(key) || declarations.containsKey(key);
        }

        @Override
        public boolean containsValue( Object value ) {
            return pattern.getDeclarations().containsValue(value) || declarations.containsValue( value );
        }

        @Override
        public Declaration get( Object key ) {
            Declaration declaration = pattern.getDeclarations().get(key);
            if (declaration == null) {
                declaration = getDeclaration(pattern, (String)key);
            }
            return declaration;
        }

        @Override
        public Declaration put( String key, Declaration value ) {
            return pattern.getDeclarations().put( key, value );
        }

        @Override
        public Declaration remove( Object key ) {
            return pattern.getDeclarations().remove( key );
        }

        @Override
        public void putAll( Map<? extends String, ? extends Declaration> m ) {
            pattern.getDeclarations().putAll(m);
        }

        @Override
        public void clear() {
            pattern.getDeclarations().clear();
            declarations.clear();
        }

        @Override
        public Set<String> keySet() {
            return pattern.getDeclarations().keySet();
        }

        @Override
        public Collection<Declaration> values() {
            return pattern.getDeclarations().values();
        }

        @Override
        public Set<Entry<String, Declaration>> entrySet() {
            return pattern.getDeclarations().entrySet();
        }
    }
}
