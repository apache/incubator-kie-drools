package org.drools.model.functions;

import java.io.Serializable;

public interface Block9<A, B, C, D, E, F, G, H, I> extends Serializable {
    void execute( A a, B b, C c, D d, E e, F f, G g, H h, I i ) throws Exception;

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block9 block;

        public Impl(Block9 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) throws Exception {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4], objs[5], objs[6], objs[7], objs[8]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }

}
