package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNContextImpl;

public final class DMNFactory {

    public static DMNContext newContext() {
        return new DMNContextImpl();
    }

    public static DMNCompiler newCompiler() { return new DMNCompilerImpl(); }

    public static DMNCompiler newCompiler(DMNCompilerConfiguration dmnCompilerConfig) {
        return new DMNCompilerImpl(dmnCompilerConfig);
    }

    public static DMNCompilerConfiguration newCompilerConfiguration() {
        return new DMNCompilerConfigurationImpl();
    }

    private DMNFactory() {
        // Constructing instances is not allowed for this Factory
    }
}
