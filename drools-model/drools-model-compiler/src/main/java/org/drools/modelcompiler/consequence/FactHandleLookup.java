/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.consequence;

import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.common.InternalFactHandle;

public interface FactHandleLookup {

    void put( Object obj, InternalFactHandle fh );

    InternalFactHandle get( Object obj );

    void clear();

    static FactHandleLookup create(int arity) {
        switch (arity) {
            case 0: return new Empty();
            case 1: return new Single();
            case 2: return new Double();
            default: return new Multi();
        }
    }

    class Empty implements FactHandleLookup {

        @Override
        public void put( Object obj, InternalFactHandle fh ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalFactHandle get( Object obj ) {
            return null;
        }

        @Override
        public void clear() { }
    }

    class Single implements FactHandleLookup {

        private Object obj;
        private InternalFactHandle fh;

        @Override
        public void put( Object obj, InternalFactHandle fh ) {
            this.obj = obj;
            this.fh = fh;
        }

        @Override
        public InternalFactHandle get( Object obj ) {
            return this.obj == obj ? fh : null;
        }

        @Override
        public void clear() {
            this.obj = null;
            this.fh = null;
        }
    }

    class Double implements FactHandleLookup {

        private Object obj1;
        private Object obj2;
        private InternalFactHandle fh1;
        private InternalFactHandle fh2;

        @Override
        public void put( Object obj, InternalFactHandle fh ) {
            if ( this.obj1 == null) {
                this.obj1 = obj;
                this.fh1 = fh;
            } else {
                this.obj2 = obj;
                this.fh2 = fh;
            }
        }

        @Override
        public InternalFactHandle get( Object obj ) {
            return this.obj1 == obj ? fh1 : ( this.obj2 == obj ? fh2 : null );
        }

        @Override
        public void clear() {
            this.obj1 = null;
            this.obj2 = null;
            this.fh1 = null;
            this.fh2 = null;
        }
    }

    class Multi implements FactHandleLookup {

        private final Map<Object, InternalFactHandle> map = new IdentityHashMap<>();

        @Override
        public void put( Object obj, InternalFactHandle fh ) {
            map.put( obj, fh );
        }

        @Override
        public InternalFactHandle get( Object obj ) {
            return map.get( obj );
        }

        @Override
        public void clear() {
            map.clear();
        }
    }
}
