package org.drools.model.functions;

import java.io.Serializable;

public interface Block3<T1, T2, T3> extends Serializable {

    void execute(T1 arg1, T2 arg2, T3 arg3) throws Exception;

    default BlockN asBlockN() {
        return new Impl(this);
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block3 block;

        public Impl(Block3 block) {
            this.block = block;
        }

        @Override
        public void execute(Object... objs) throws Exception {
            block.execute(objs[0], objs[1], objs[2]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
