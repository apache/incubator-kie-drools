package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralChunkExpr;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.type.Type;
import org.drools.model.Window;
import org.drools.model.WindowDefinition;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.stream.Collectors.toList;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WINDOW_CALL;

public class WindowReferenceGenerator {

    final PackageModel packageModel;
    final TypeResolver typeResolver;

    public WindowReferenceGenerator(PackageModel packageModel, TypeResolver typeResolver) {
        this.packageModel = packageModel;
        this.typeResolver = typeResolver;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr) {
        if (sourceDescr instanceof WindowReferenceDescr) {
            final WindowReferenceDescr source = ((WindowReferenceDescr) sourceDescr);
            final String windowVariable = toVar(source.getName());
            if (packageModel.getWindowReferences().containsKey(windowVariable)) {
                return Optional.of(new NameExpr(windowVariable));
            }
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    public void addWindowReferences( KnowledgeBuilderImpl kbuilder, Set<WindowDeclarationDescr> windowReferences ) {
        for (WindowDeclarationDescr descr : windowReferences) {
            addField(kbuilder, packageModel, descr);
        }
    }

    private void addField( KnowledgeBuilderImpl kbuilder, PackageModel packageModel, WindowDeclarationDescr descr ) {

        final String windowName = toVar(descr.getName());

        final MethodCallExpr initializer = new MethodCallExpr(null, WINDOW_CALL);

        final PatternDescr pattern = descr.getPattern();
        ParsedBehavior behavior = pattern
                .getBehaviors()
                .stream()
                .map(this::parseTypeFromBehavior)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        final WindowDefinition.Type windowType = behavior.windowType;
        initializer.addArgument(new NameExpr(windowType.getDeclaringClass().getCanonicalName() + "." + windowType.toString()));

        initializer.addArgument(new IntegerLiteralExpr(behavior.duration.getValue()));
        final TimeUnit timeUnit = behavior.duration.getTimeUnit();
        initializer.addArgument(new NameExpr(timeUnit.getDeclaringClass().getCanonicalName() + "." + timeUnit.name()));

        final Class<?> initClass = DrlxParseUtil.getClassFromContext(typeResolver, pattern.getObjectType());

        final Type initType = JavaParser.parseType(initClass.getCanonicalName());
        initializer.addArgument(new ClassExpr(initType));

        parseConditions(kbuilder, packageModel, pattern, initClass).forEach(initializer::addArgument);

        packageModel.addAllWindowReferences(windowName, initializer);
    }

    private List<Expression> parseConditions( KnowledgeBuilderImpl kbuilder, PackageModel packageModel, PatternDescr pattern, Class<?> patternType ) {
        List<? extends BaseDescr> descrs = pattern.getConstraint().getDescrs();
        if (descrs == null) {
            return Collections.emptyList();
        }
        return descrs.stream()
                .map( descr -> {
                    String expression = descr.toString();
                    RuleContext context = new RuleContext(kbuilder, packageModel, null, typeResolver, true);
                    DrlxParseResult drlxParseResult = new ConstraintParser(context, packageModel).drlxParse(patternType, pattern.getIdentifier(), expression);
                    return drlxParseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<Expression>>() {
                        @Override
                        public Optional<Expression> onSuccess(DrlxParseSuccess drlxParseResult) {
                            return Optional.of(generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), drlxParseResult.getExpr()));
                        }

                        @Override
                        public Optional<Expression> onFail(DrlxParseFail failure) {
                            return Optional.empty();
                        }
                    });
                } )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .collect( toList() );
    }

    private ParsedBehavior parseTypeFromBehavior(BehaviorDescr descr) {
        final WindowDefinition.Type windowType = Window.Type.valueOf(descr.getSubType().toUpperCase());
        final TemporalLiteralChunkExpr duration = parseDuration(descr.getParameters().get(0));

        ParsedBehavior parsedBehavior = new ParsedBehavior();
        parsedBehavior.windowType = windowType;
        parsedBehavior.duration = duration;
        return parsedBehavior;
    }

    static class ParsedBehavior {

        Window.Type windowType;
        TemporalLiteralChunkExpr duration;
    }

    public static TemporalLiteralChunkExpr parseDuration(String duration) {
        TemporalLiteralExpr te = DrlxParser.parseTemporalLiteral(duration);
        return te.getChunks().iterator().next();
    }

}