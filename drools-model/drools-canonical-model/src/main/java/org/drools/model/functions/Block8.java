package org.drools.model.functions;

import java.io.Serializable;

public interface Block8<A, B, C, D, E, F, G, H> extends Serializable {
    void execute( A a, B b, C c, D d, E e, F f, G g, H h ) throws Exception;

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block8 block;

        public Impl(Block8 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) throws Exception {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4], objs[5], objs[6], objs[7]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }

}
