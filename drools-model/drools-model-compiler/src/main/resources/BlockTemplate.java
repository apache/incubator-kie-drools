package org.drools.model.functions;

import java.io.Serializable;

public interface BlockTemplate<A, B, C, D, E, F, G, H, I, J, K, L, M, O> extends Serializable {
    void execute(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, O o) throws Exception;

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final BlockTemplate block;

        public Impl(BlockTemplate block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) throws Exception {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4], objs[5], objs[6], objs[7], objs[8], objs[9], objs[10], objs[11], objs[12], objs[13]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }

}
