package org.drools.model.functions;

import java.io.Serializable;

public interface Block1<T1> extends Serializable {

    void execute(T1 arg1) throws Exception;

    default BlockN asBlockN() {
        return new Impl(this);
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block1 block;

        public Impl(Block1 block) {
            this.block = block;
        }

        @Override
        public void execute(Object... objs) throws Exception {
            block.execute(objs[0]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
