package org.kie.dmn.feel.gwt.functions.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;

public class FunctionProviderGenerator
        extends Generator {

    @Override
    public String generate(final TreeLogger logger,
                           final GeneratorContext context,
                           final String requestedClass) throws UnableToCompleteException {
        final TypeOracle typeOracle = context.getTypeOracle();
        final JClassType functionType = typeOracle.findType(requestedClass);
        assert FEELFunctionProvider.class.equals(functionType.getClass());

        new FileCreator(context,
                        logger).write();

        return FileCreator.PACKAGE_NAME + "." + FileCreator.GENERATED_CLASS_FQCN;
    }
}
