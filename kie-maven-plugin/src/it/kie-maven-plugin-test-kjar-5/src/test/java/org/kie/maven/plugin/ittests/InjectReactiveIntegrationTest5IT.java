package org.kie.maven.plugin.ittests;

import java.lang.reflect.Method;

import org.drools.core.phreak.ReactiveObject;
import org.junit.Test;
import org.kie.maven.plugin.BytecodeInjectReactive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InjectReactiveIntegrationTest5IT {

    private static Logger logger = LoggerFactory.getLogger(InjectReactiveIntegrationTest5IT.class);

    @Test
    public void testBasicBytecodeInjectionSelected() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        assertTrue(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.Adult")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.UsingADependencyClass")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.UsingSpecializedList")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.TMFile")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.TMFileSet")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("org.drools.compiler.xpath.tobeinstrumented.model.ImmutablePojo")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("to.instrument.Adult")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("to.instrument.UsingADependencyClass")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("to.instrument.UsingSpecializedList")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("to.instrument.TMFile")));
        assertTrue(looksLikeInstrumentedClass(cl.loadClass("to.instrument.TMFileSet")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.instrument.ImmutablePojo")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.Adult")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.UsingADependencyClass")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.UsingSpecializedList")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.TMFile")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.TMFileSet")));
        assertFalse(looksLikeInstrumentedClass(cl.loadClass("to.not.instrument.ImmutablePojo")));
    }

    private boolean looksLikeInstrumentedClass(Class<?> personClass) {
        boolean foundReactiveObjectInterface = false;
        for (Class<?> i : personClass.getInterfaces()) {
            if (i.getName().equals(ReactiveObject.class.getName())) {
                foundReactiveObjectInterface = true;
            }
        }
        // the ReactiveObject interface method are injected by the bytecode instrumenter, better check they are indeed available..
        boolean containsGetLeftTuple = checkContainsMethod(personClass,
                                                           "getLeftTuples");
        boolean containsAddLeftTuple = checkContainsMethod(personClass,
                                                           "addLeftTuple");
        boolean containsRemoveLeftTuple = checkContainsMethod(personClass,
                                                              "removeLeftTuple");

        boolean foundReactiveInjectedMethods = false;
        for (Method m : personClass.getMethods()) {
            if (m.getName().startsWith(BytecodeInjectReactive.DROOLS_PREFIX)) {
                foundReactiveInjectedMethods = true;
            }
        }
        return foundReactiveObjectInterface
                && containsGetLeftTuple && containsAddLeftTuple && containsRemoveLeftTuple
                && foundReactiveInjectedMethods;
    }

    private boolean checkContainsMethod(Class<?> personClass,
                                        Object methodName) {
        for (Method m : personClass.getMethods()) {
            if (m.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }
}
