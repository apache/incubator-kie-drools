package org.drools.runtime.pipeline.impl;

import junit.framework.TestCase;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.PipelineFactory;

public class CallableTest extends TestCase {
    public void testCallable() {
        MockClass mock = new MockClass();
        Callable callable = new CallableImpl();
        Action action = PipelineFactory.newMvelAction( "this.set = true" );
        callable.setReceiver( action );
        action.setReceiver( callable );
        assertFalse( mock.isSet() );
        callable.call( mock,
                       new BasePipelineContext( Thread.currentThread().getContextClassLoader() ) );
        assertTrue( mock.isSet() );
    }

    public static class MockClass {
        private boolean set;

        public boolean isSet() {
            return set;
        }

        public void setSet(boolean set) {
            this.set = set;
        }

    }
}
