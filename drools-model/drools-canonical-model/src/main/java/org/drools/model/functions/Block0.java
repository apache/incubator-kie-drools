package org.drools.model.functions;

import java.io.Serializable;

public interface Block0 extends Serializable {
    void execute();

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block0 block;

        public Impl(Block0 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) {
            block.execute();
        }

        @Override
        protected Object getLambda() {
            return block;
        }
    }
}
