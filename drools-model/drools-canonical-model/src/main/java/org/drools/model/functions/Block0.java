package org.drools.model.functions;

import java.io.Serializable;

public interface Block0 extends Serializable {
    void execute() throws Exception;

    default BlockN asBlockN() {
        return new Impl( this );
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block0 block;

        public Impl(Block0 block) {
            this.block = block;
        }

        @Override
        public void execute( Object... objs ) throws Exception {
            block.execute();
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
