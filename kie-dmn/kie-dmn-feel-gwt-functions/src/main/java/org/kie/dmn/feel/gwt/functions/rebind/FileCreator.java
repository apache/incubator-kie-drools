package org.kie.dmn.feel.gwt.functions.rebind;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.kie.dmn.feel.gwt.functions.api.FunctionDefinition;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.api.Parameter;
import org.kie.dmn.feel.gwt.functions.api.Type;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.types.BuiltInType;

class FileCreator {

    public static final String GENERATED_CLASS_FQCN =
            FEELFunctionProvider.class.getSimpleName() + "Impl";
    public static final String PACKAGE_NAME =
            FEELFunctionProvider.class.getPackage().getName();

    private final TreeLogger logger;
    private final SourceWriter sourceWriter;

    public FileCreator(final GeneratorContext context,
                       final TreeLogger logger) {
        this.logger = logger;
        final ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(PACKAGE_NAME,
                                                                                                  GENERATED_CLASS_FQCN);

        composerFactory.addImport(FEELFunctionProvider.class.getCanonicalName());
        composerFactory.addImport(FunctionDefinition.class.getCanonicalName());
        composerFactory.addImport(FunctionOverrideVariation.class.getCanonicalName());
        composerFactory.addImport(BuiltInType.class.getCanonicalName());
        composerFactory.addImport(Parameter.class.getCanonicalName());
        composerFactory.addImport(List.class.getCanonicalName());
        composerFactory.addImport(ArrayList.class.getCanonicalName());
        composerFactory.addImport(Type.class.getCanonicalName());

        composerFactory.addImplementedInterface(FEELFunctionProvider.class.getName());

        sourceWriter = composerFactory.createSourceWriter(context,
                                                          context.tryCreate(logger,
                                                                            PACKAGE_NAME,
                                                                            GENERATED_CLASS_FQCN));
    }

    void write() {
        writeGetDefinitions();

        sourceWriter.commit(logger);
    }

    private void writeGetDefinitions() {
        sourceWriter.println("public List<FunctionOverrideVariation> getDefinitions() {");
        sourceWriter.println("    ArrayList definitions = new ArrayList();");

        for (String template : new MethodTemplates().getAll()) {

            sourceWriter.println("definitions.add( %s );",
                                 template);
        }

//        for (final FEELFunction function : BuiltInFunctions.getFunctions()) {
//
////            getMethodddddddddds(function);
//
//            sourceWriter.println("definitions.add( new FunctionDefinition( \"%s\", %s, new FunctionOverrideVariation() ) );",
//                                 function.getName(),
//                                 getMethodReturnType());
//        }

        // TODO override variations
        sourceWriter.println("return definitions;");
        sourceWriter.println("}");
    }
//
//    private String getMethodReturnType() {
//        return "BuiltInType.BOOLEAN";
//
////        return "Type.BOOLEAN";
//    }
}
