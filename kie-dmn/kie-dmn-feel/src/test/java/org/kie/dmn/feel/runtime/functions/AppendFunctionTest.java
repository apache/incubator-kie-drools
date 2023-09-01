package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AppendFunctionTest {

    private AppendFunction appendFunction;

    @Before
    public void setUp() {
        appendFunction = new AppendFunction();
    }

    @Test
    public void invokeInvalidParams() {
        FunctionTestUtil.assertResultError(appendFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(appendFunction.invoke((List) null, new Object[]{}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(appendFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyParams() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(Collections.emptyList(), new Object[]{}), Collections.emptyList());
    }

    @Test
    public void invokeAppendNothing() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(List.of("test"), new Object[]{}), List.of("test"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(Arrays.asList("test", "test2"), new Object[]{}), Arrays.asList("test", "test2"));
    }

    @Test
    public void invokeAppendSomething() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(Collections.emptyList(), new Object[]{"test"}), List.of("test"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(List.of("test"), new Object[]{"test2"}), Arrays.asList("test", "test2"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(List.of("test"), new Object[]{"test2", "test3"}), Arrays.asList("test", "test2", "test3"));
    }
}