package org.kie.dmn.feel.gwt.functions.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;

public class FunctionProviderGenerator extends Generator {

    @Override
    public String generate(final TreeLogger logger,
                           final GeneratorContext context,
                           final String requestedClass) {
        try {
            final TypeOracle typeOracle = context.getTypeOracle();
            final JClassType functionType = typeOracle.findType(requestedClass);

            assertFEELFunctionProviderClass(functionType);
            getFileCreator(logger, context).write();

            return FileCreator.PACKAGE_NAME + "." + FileCreator.GENERATED_CLASS_FQCN;
        } catch (final Exception e) {
            return null;
        }
    }

    FileCreator getFileCreator(final TreeLogger logger,
                               final GeneratorContext context) {
        return new FileCreator(context, logger);
    }

    @SuppressWarnings("ConstantConditions")
    void assertFEELFunctionProviderClass(final JClassType functionType) {
        // JClassType#getClass cannot be mocked (GWT API)
        assert FEELFunctionProvider.class.equals(functionType.getClass());
    }
}
