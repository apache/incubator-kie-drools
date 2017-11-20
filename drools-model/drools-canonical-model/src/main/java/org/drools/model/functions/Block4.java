package org.drools.model.functions;

import java.io.Serializable;

public interface Block4<A, B, C, D> extends Serializable {
    void execute( A a, B b, C c, D d );

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block4 block;

        public Impl(Block4 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute(objs[0], objs[1], objs[2], objs[3]);
        }

        @Override
        protected Object getLambda() {
            return block;
        }
    }
}