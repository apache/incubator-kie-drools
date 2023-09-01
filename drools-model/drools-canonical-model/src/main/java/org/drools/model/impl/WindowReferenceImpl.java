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
package org.drools.model.impl;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.drools.model.EntryPoint;
import org.drools.model.WindowReference;
import org.drools.model.functions.Predicate1;

import static org.drools.model.impl.NamesGenerator.generateName;

public class WindowReferenceImpl<T> extends AbstractWindow implements WindowReference<T>, ModelComponent {

    private final Class<T> patternType;
    private final Predicate1<T>[] predicates;
    private final String name;
    private final EntryPoint entryPoint;

    public WindowReferenceImpl( Type type, long value, Class<T> patternType, Predicate1<T>... predicates ) {
        this( type, value, null, patternType, predicates );
    }

    public WindowReferenceImpl( Type type, long value, Class<T> patternType, EntryPoint entryPoint, Predicate1<T>... predicates ) {
        this( type, value, null, patternType, entryPoint, predicates );
    }

    public WindowReferenceImpl( Type type, long value, TimeUnit timeUnit, Class<T> patternType, Predicate1<T>... predicates ) {
        this( type, value, timeUnit, patternType, null, predicates );
    }

    public WindowReferenceImpl( Type type, long value, TimeUnit timeUnit, Class<T> patternType, EntryPoint entryPoint, Predicate1<T>... predicates ) {
        super( type, value, timeUnit );
        this.patternType = patternType;
        this.predicates = predicates;
        this.entryPoint = entryPoint;
        this.name = generateName("window");
    }

    @Override
    public Class<T> getPatternType() {
        return patternType;
    }

    @Override
    public Predicate1<T>[] getPredicates() {
        return predicates;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof WindowReferenceImpl) ) return false;

        WindowReferenceImpl<?> that = ( WindowReferenceImpl<?> ) o;

        if ( !super.isEqualTo( that ) ) return false;
        if ( patternType != null ? !patternType.equals( that.patternType ) : that.patternType != null ) return false;
        if ( !Arrays.equals( predicates, that.predicates ) ) return false;
        return name != null ? name.equals( that.name ) : that.name == null;
    }
}
