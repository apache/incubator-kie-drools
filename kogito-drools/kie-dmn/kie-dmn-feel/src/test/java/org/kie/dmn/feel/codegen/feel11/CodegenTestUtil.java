package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.util.ClassLoaderUtil;

public class CodegenTestUtil {

    private CodegenTestUtil() {
        // only static methods for util class.
    }

    public static EvaluationContext newEmptyEvaluationContext() {
        return new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null);
    }
}
