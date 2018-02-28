package org.drools.model.functions;

import java.io.Serializable;

public interface Block5<A, B, C, D, E> extends Serializable {
    void execute( A a, B b, C c, D d, E e );

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block5 block;

        public Impl(Block5 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }

}
