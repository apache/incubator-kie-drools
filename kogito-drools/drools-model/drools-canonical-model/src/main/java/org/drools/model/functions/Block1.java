package org.drools.model.functions;

import java.io.Serializable;

public interface Block1<A> extends Serializable {
    void execute(A a);

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block1 block;
        private String lambdaFingerprint;

        public Impl(Block1 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute(objs[0]);
        }

        @Override
        protected Object getLambda() {
            return block;
        }
    }
}
