package org.drools.model.functions;

import java.io.Serializable;

public interface Block6<A, B, C, D, E, F> extends Serializable {
    void execute(A a, B b, C c, D d, E e, F f);

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block6 block;

        public Impl(Block6 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4], objs[5]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }

}
