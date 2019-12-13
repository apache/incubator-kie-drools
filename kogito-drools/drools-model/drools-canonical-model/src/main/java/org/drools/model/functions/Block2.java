package org.drools.model.functions;

import java.io.Serializable;

public interface Block2<T1, T2> extends Serializable {

    void execute(T1 arg1, T2 arg2) throws Exception;

    default BlockN asBlockN() {
        return new Impl(this);
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block2 block;

        public Impl(Block2 block) {
            this.block = block;
        }

        @Override
        public void execute(Object... objs) throws Exception {
            block.execute(objs[0], objs[1]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
