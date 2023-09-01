package org.drools.modelcompiler.consequence;

import java.util.IdentityHashMap;
import java.util.Map;

import org.kie.api.runtime.rule.FactHandle;

public interface FactHandleLookup {

    void put( Object obj, FactHandle fh);

    FactHandle get( Object obj );

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
        public void put( Object obj, FactHandle fh ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FactHandle get( Object obj ) {
            return null;
        }

        @Override
        public void clear() { }
    }

    class Single implements FactHandleLookup {

        private Object obj;
        private FactHandle fh;

        @Override
        public void put( Object obj, FactHandle fh ) {
            this.obj = obj;
            this.fh = fh;
        }

        @Override
        public FactHandle get( Object obj ) {
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
        private FactHandle fh1;
        private FactHandle fh2;

        @Override
        public void put( Object obj, FactHandle fh ) {
            if ( this.obj1 == null) {
                this.obj1 = obj;
                this.fh1 = fh;
            } else {
                this.obj2 = obj;
                this.fh2 = fh;
            }
        }

        @Override
        public FactHandle get( Object obj ) {
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

        private final Map<Object, FactHandle> map = new IdentityHashMap<>();

        @Override
        public void put( Object obj, FactHandle fh ) {
            map.put( obj, fh );
        }

        @Override
        public FactHandle get( Object obj ) {
            return map.get( obj );
        }

        @Override
        public void clear() {
            map.clear();
        }
    }
}
