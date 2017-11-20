package org.drools.model.functions;

import java.io.Serializable;

public interface Block3<A, B, C> extends Serializable {
    void execute(A a, B b, C c);

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block3 block;

        public Impl(Block3 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute(objs[0], objs[1], objs[2]);
        }

        @Override
        protected Object getLambda() {
            return block;
        }
    }
}