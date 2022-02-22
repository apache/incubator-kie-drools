package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.GlobalError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.core.addon.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.rule.Function;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.WindowDeclaration;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternDestinationDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.parser.DroolsError;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.drools.core.util.StringUtils.ucFirst;

public class PackageProcessor extends AbstractPackageProcessor{
    private KnowledgeBuilderConfigurationImpl configuration;
    private TypeDeclarationBuilder typeBuilder;

    public PackageProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
    }

    public void process() {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }

        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(pkgRegistry.getTypeResolver(), configuration.getLanguageLevel().useJavaAnnotations());

        TypeDeclarationAnnotationNormalizer typeDeclarationAnnotationNormalizer =
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr, pkgRegistry.getTypeResolver());

        EntryPointDeclarationProcessor entryPointDeclarationProcessor =
                new EntryPointDeclarationProcessor(pkgRegistry, packageDescr);

        AccumulateFunctionProcessor accumulateFunctionProcessor =
                new AccumulateFunctionProcessor(pkgRegistry, packageDescr);

        TypeDeclarationProcessor typeDeclarationProcessor =
                new TypeDeclarationProcessor(packageDescr, typeBuilder, pkgRegistry);

        WindowDeclarationProcessor windowDeclarationProcessor =
                new WindowDeclarationProcessor(pkgRegistry, packageDescr);

        FunctionProcessor functionProcessor =
                new FunctionProcessor(pkgRegistry, packageDescr, configuration);

        GlobalProcessor globalProcessor =
                new GlobalProcessor(pkgRegistry, packageDescr);

        RuleAnnotationNormalizer ruleAnnotationNormalizer =
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr);

        typeDeclarationAnnotationNormalizer.normalize();
        accumulateFunctionProcessor.process();
        entryPointDeclarationProcessor.process();
        typeDeclarationProcessor.process();
//        processOtherDeclarations(pkgRegistry, packageDescr);
//        processAccumulateFunctions(pkgRegistry, packageDescr); // ?????
        windowDeclarationProcessor.process();
        functionProcessor.process();
        globalProcessor.process();
        ruleAnnotationNormalizer.process();
    }

}


class TypeDeclarationAnnotationNormalizer {
    private final AnnotationNormalizer annotationNormalizer;
    final PackageDescr packageDescr;
    final TypeResolver typeResolver;
    private Collection<KnowledgeBuilderResult> results;

    public TypeDeclarationAnnotationNormalizer(AnnotationNormalizer annotationNormalizer, PackageDescr packageDescr, TypeResolver typeResolver) {
        this.annotationNormalizer = annotationNormalizer;

        this.packageDescr = packageDescr;
        this.typeResolver = typeResolver;
    }

    public void normalize() {
        for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
            annotationNormalizer.normalize(typeDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : typeDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }

        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            annotationNormalizer.normalize(enumDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : enumDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }
    }

}


abstract class AnnotationNormalizer {
    public static AnnotationNormalizer of(TypeResolver typeResolver, boolean isStrict) {
        if (isStrict) return new Strict(typeResolver);
        else return new NonStrict(typeResolver);
    }

    protected final TypeResolver typeResolver;
    protected Collection<KnowledgeBuilderResult> results;

    AnnotationNormalizer(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    abstract boolean isStrict();
    abstract AnnotationDescr doNormalize(AnnotationDescr descr);

    protected void normalize(AnnotatedBaseDescr annotationsContainer) {
        for (AnnotationDescr annotationDescr : annotationsContainer.getAnnotations()) {
            annotationDescr.setResource(annotationsContainer.getResource());
            annotationDescr.setStrict(isStrict());
            if (annotationDescr.isDuplicated()) {
                this.results.add(new AnnotationDeclarationError(annotationDescr,
                        "Duplicated annotation: " + annotationDescr.getName()));
            }
            doNormalize(annotationDescr);
        }
        annotationsContainer.indexByFQN(isStrict());
    }


    static class Strict extends AnnotationNormalizer {
        public Strict(TypeResolver typeResolver) {
            super(typeResolver);
        }

        @Override boolean isStrict() { return true; }

        AnnotationDescr doNormalize(AnnotationDescr annotationDescr) {
            try {
                Class<?> annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
                annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());
                return annotationDescr;
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                this.results.add(new AnnotationDeclarationError(annotationDescr,
                        "Unknown annotation: " + annotationDescr.getName()));
            }
            return null;
        }
    }

    static class NonStrict extends AnnotationNormalizer {

        public NonStrict(TypeResolver typeResolver) {
            super(typeResolver);
        }

        @Override boolean isStrict() { return false; }

        AnnotationDescr doNormalize(AnnotationDescr annotationDescr) {
            Class<?> annotationClass = null;
            try {
                annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                String className = normalizeAnnotationNonStrictName(annotationDescr.getName());
                try {
                    annotationClass = typeResolver.resolveType(className, TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
                } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                    // non-strict annotation, ignore error
                }
            }
            if (annotationClass != null) {
                annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());

                for (String key : annotationDescr.getValueMap().keySet()) {
                    try {
                        Method m = annotationClass.getMethod(key);
                        Object val = annotationDescr.getValue(key);
                        if (val instanceof Object[] && !m.getReturnType().isArray()) {
                            this.results.add(new AnnotationDeclarationError(annotationDescr,
                                    "Wrong cardinality on property " + key));
                            return annotationDescr;
                        }
                        if (m.getReturnType().isArray() && !(val instanceof Object[])) {
                            val = new Object[]{val};
                            annotationDescr.setKeyValue(key, val);
                        }

                        if (m.getReturnType().isArray()) {
                            int n = Array.getLength(val);
                            for (int j = 0; j < n; j++) {
                                if (Class.class.equals(m.getReturnType().getComponentType())) {
                                    String className = Array.get(val, j).toString().replace(".class", "");
                                    Array.set(val, j, typeResolver.resolveType(className).getName() + ".class");
                                } else if (m.getReturnType().getComponentType().isAnnotation()) {
                                    Array.set(val, j, doNormalize((AnnotationDescr) Array.get(val, j)));
                                }
                            }
                        } else {
                            if (Class.class.equals(m.getReturnType())) {
                                String className = annotationDescr.getValueAsString(key).replace(".class", "");
                                annotationDescr.setKeyValue(key, typeResolver.resolveType(className));
                            } else if (m.getReturnType().isAnnotation()) {
                                annotationDescr.setKeyValue(key,
                                        doNormalize((AnnotationDescr) annotationDescr.getValue(key)));
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        this.results.add(new AnnotationDeclarationError(annotationDescr,
                                "Unknown annotation property " + key));
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        this.results.add(new AnnotationDeclarationError(annotationDescr,
                                "Unknown class " + annotationDescr.getValue(key) + " used in property " + key +
                                        " of annotation " + annotationDescr.getName()));
                    }
                }
            }
            return annotationDescr;
        }


        private String normalizeAnnotationNonStrictName(String name) {
            if ("typesafe".equalsIgnoreCase(name)) {
                return "TypeSafe";
            }
            return ucFirst(name);
        }
    }
}

class RuleAnnotationNormalizer {
    AnnotationNormalizer annotationNormalizer;
    private PackageDescr packageDescr;

    public RuleAnnotationNormalizer(AnnotationNormalizer annotationNormalizer, PackageDescr packageDescr) {
        this.annotationNormalizer = annotationNormalizer;
        this.packageDescr = packageDescr;
    }

    public void process() {
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            annotationNormalizer.normalize(ruleDescr);
            traverseAnnotations(ruleDescr.getLhs());
        }
    }

    private void traverseAnnotations(BaseDescr descr) {
        if (descr instanceof AnnotatedBaseDescr) {
            annotationNormalizer.normalize((AnnotatedBaseDescr) descr);
        }
        if (descr instanceof ConditionalElementDescr) {
            for (BaseDescr baseDescr : ((ConditionalElementDescr) descr).getDescrs()) {
                traverseAnnotations(baseDescr);
            }
        }
        if (descr instanceof PatternDescr && ((PatternDescr) descr).getSource() != null) {
            traverseAnnotations(((PatternDescr) descr).getSource());
        }
        if (descr instanceof PatternDestinationDescr) {
            traverseAnnotations(((PatternDestinationDescr) descr).getInputPattern());
        }
    }
}

class AccumulateFunctionProcessor {
    private final TypeResolver typeResolver;
    private final PackageDescr packageDescr;
    private final PackageRegistry pkgRegistry;

    public AccumulateFunctionProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this.pkgRegistry = pkgRegistry;
        this.typeResolver = pkgRegistry.getTypeResolver();
        this.packageDescr = packageDescr;
    }

    protected void process() {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports()) {
            AccumulateFunction af = loadAccumulateFunction(
                    aid.getFunctionName(),
                    aid.getTarget());
            pkgRegistry.getPackage().addAccumulateFunction(aid.getFunctionName(), af);
        }
    }

    @SuppressWarnings("unchecked")
    private AccumulateFunction loadAccumulateFunction(
                                                      String identifier,
                                                      String className) {
        try {
            Class<? extends AccumulateFunction> clazz = (Class<? extends AccumulateFunction>) typeResolver.resolveType(className);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                    e);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                    e);
        }
    }
}

class EntryPointDeclarationProcessor {
    PackageRegistry pkgRegistry;
    PackageDescr packageDescr;
    public EntryPointDeclarationProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
    }

    void process() {
        for (EntryPointDeclarationDescr epDescr : packageDescr.getEntryPointDeclarations()) {
            pkgRegistry.getPackage().addEntryPointId(epDescr.getEntryPointId());
        }
    }

}

class TypeDeclarationProcessor {
    private final PackageDescr packageDescr;
    private final TypeDeclarationBuilder typeBuilder;
    private final PackageRegistry pkgRegistry;
    private final List<AbstractClassTypeDeclarationDescr> unsortedDescrs;
    Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
    List<TypeDefinition> unresolvedTypes = new ArrayList<>();
    private Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public TypeDeclarationProcessor(PackageDescr packageDescr, TypeDeclarationBuilder typeBuilder, PackageRegistry pkgRegistry) {
        this.packageDescr = packageDescr;
        this.typeBuilder = typeBuilder;
        this.pkgRegistry = pkgRegistry;
        unsortedDescrs = new ArrayList<>();
        unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
        unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
    }


    public void process() {
        typeBuilder.processTypeDeclarations(packageDescr, pkgRegistry, unsortedDescrs, unresolvedTypes, unprocesseableDescrs);
        for (AbstractClassTypeDeclarationDescr descr : unprocesseableDescrs.values()) {
            this.results.add(new TypeDeclarationError(descr, "Unable to process type " + descr.getTypeName()));
        }
    }
}

class WindowDeclarationProcessor {

    PackageRegistry pkgRegistry;
    PackageDescr packageDescr;
    private Collection<KnowledgeBuilderResult> results;
    private DroolsAssemblerContext kBuilder;

    public WindowDeclarationProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {

        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
    }

    protected void process() {
        for (WindowDeclarationDescr wd : packageDescr.getWindowDeclarations()) {
            WindowDeclaration window = new WindowDeclaration(wd.getName(), packageDescr.getName());
            // TODO: process annotations

            // process pattern
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
            RuleDescr dummy = new RuleDescr(wd.getName() + " Window Declaration");
            dummy.setResource(packageDescr.getResource());
            dummy.addAttribute(new AttributeDescr("dialect", "java"));
            RuleBuildContext context = new RuleBuildContext(kBuilder,
                    dummy,
                    ctr,
                    pkg,
                    ctr.getDialect(pkgRegistry.getDialect()));
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder(wd.getPattern().getClass());
            if (builder != null) {
                final Pattern pattern = (Pattern) builder.build(context,
                        wd.getPattern(),
                        null);

                if (pattern.getXpathConstraint() != null) {
                    context.addError(new DescrBuildError(wd,
                            context.getParentDescr(),
                            null,
                            "OOpath expression " + pattern.getXpathConstraint() + " not allowed in window declaration\n"));
                }

                window.setPattern(pattern);
            } else {
                throw new RuntimeException(
                        "BUG: assembler not found for descriptor class " + wd.getPattern().getClass());
            }

            if (!context.getErrors().isEmpty()) {
                for (DroolsError error : context.getErrors()) {
                    this.results.add(error);
                }
            } else {
                pkgRegistry.getPackage().addWindowDeclaration(window);
            }
        }
    }
}

class FunctionProcessor {

    private Collection<KnowledgeBuilderResult> results;
    private final PackageRegistry pkgRegistry;
    private final PackageDescr packageDescr;
    private KnowledgeBuilderConfiguration configuration;

    FunctionProcessor(PackageRegistry pkgRegistry,
                      PackageDescr packageDescr,
                      KnowledgeBuilderConfiguration configuration) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.configuration = configuration;
    }

    protected void process() {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get(function.getName());
            if (existingFunc != null && function.getNamespace().equals(existingFunc.getNamespace())) {
                this.results.add(
                        new DuplicateFunction(function,
                                this.configuration));
            }
        }

        for (final FunctionImportDescr functionImport : packageDescr.getFunctionImports()) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport(functionImport);
            pkgRegistry.getPackage().addStaticImport(importEntry);
        }
    }
}

class GlobalProcessor {
    protected static final transient Logger logger = LoggerFactory.getLogger(GlobalProcessor.class);


    Collection<KnowledgeBuilderResult> results;
    private KnowledgeBaseImpl kBase;
    private Map<String, Class<?>> globals;
    PackageRegistry pkgRegistry; PackageDescr packageDescr;

    public GlobalProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
    }

    protected void process() {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        Set<String> existingGlobals = new HashSet<>(pkg.getGlobals().keySet());

        for (final GlobalDescr global : packageDescr.getGlobals()) {
            final String identifier = global.getIdentifier();
            existingGlobals.remove(identifier);
            String className = global.getType();

            // JBRULES-3039: can't handle type name with generic params
            while (className.indexOf('<') >= 0) {
                className = className.replaceAll("<[^<>]+?>", "");
            }

            try {
                Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(className);
                if (clazz.isPrimitive()) {
                    this.results.add(new GlobalError(global, " Primitive types are not allowed in globals : " + className));
                    return;
                }
                pkg.addGlobal(identifier, clazz);
                this.globals.put(identifier, clazz);
                if (kBase != null) {
                    kBase.addGlobal(identifier, clazz);
                }
            } catch (final ClassNotFoundException e) {
                this.results.add(new GlobalError(global, e.getMessage()));
                logger.warn("ClassNotFoundException occured!", e);
            }
        }

//        for (String toBeRemoved : existingGlobals) {
//            if (filterAcceptsRemoval(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved)) {
//                pkg.removeGlobal(toBeRemoved);
//                if (kBase != null) {
//                    kBase.removeGlobal(toBeRemoved);
//                }
//            }
//        }
    }

//
//    private boolean filterAcceptsRemoval(ResourceChange.Type type, String namespace, String name) {
//        return assetFilter != null && KnowledgeBuilderImpl.AssetFilter.Action.REMOVE.equals(assetFilter.accept(type, namespace, name));
//    }

}