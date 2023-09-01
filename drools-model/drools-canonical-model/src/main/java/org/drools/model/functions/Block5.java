package org.drools.model.functions;

import java.io.Serializable;

public interface Block5<T1, T2, T3, T4, T5> extends Serializable {

    void execute(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) throws Exception;

    default BlockN asBlockN() {
        return new Impl(this);
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block5 block;

        public Impl(Block5 block) {
            this.block = block;
        }

        @Override
        public void execute(Object... objs) throws Exception {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
