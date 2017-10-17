package org.drools.modelcompiler.builder.generator;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
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

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.drlxParse;

public class WindowDeclarationGenerator {

    final PackageModel packageModel;
    final InternalKnowledgePackage pkg;

    public WindowDeclarationGenerator(PackageModel packageModel, InternalKnowledgePackage pkg) {
        this.packageModel = packageModel;
        this.pkg = pkg;
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

    public void addWindowDeclarations(Set<WindowDeclarationDescr> windowDeclarations) {
        for (WindowDeclarationDescr descr : windowDeclarations) {
            addField(packageModel, descr);
        }
    }

    public void addField(PackageModel packageModel, WindowDeclarationDescr descr) {

        final String windowName = toVar(descr.getName());

        final MethodCallExpr initializer = new MethodCallExpr(null, "window");

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

        final Class<?> initClass = DrlxParseUtil.getClassFromContext(pkg, pattern.getObjectType());

        final Type initType = JavaParser.parseType(initClass.getCanonicalName());
        initializer.addArgument(new ClassExpr(initType));

        parseCondition(packageModel, pattern, initClass).ifPresent(initializer::addArgument);

        packageModel.addAllWindowDeclarations(windowName, initializer);
    }

    public Optional<Expression> parseCondition(PackageModel packageModel, PatternDescr pattern, Class<?> patternType) {
        return Optional.ofNullable(pattern.getConstraint().getDescrs().iterator().next()).map(d -> {
            String expression = d.toString();
            RuleContext context = new RuleContext(pkg, packageModel.getExprIdGenerator(), Optional.empty());
            ModelGenerator.DrlxParseResult drlxParseResult = drlxParse(context, packageModel, patternType, pattern.getIdentifier(), expression);

            return generateLambdaWithoutParameters(drlxParseResult.usedDeclarations, drlxParseResult.expr);
        });
    }

    public ParsedBehavior parseTypeFromBehavior(BehaviorDescr descr) {
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