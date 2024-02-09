/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator;

import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternSourceDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;
import org.drools.model.Window;
import org.drools.model.WindowDefinition;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseFail;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseResult;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.ParseResultVisitor;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.mvel.parser.DrlxParser;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.util.TypeResolver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toJavaParserType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toVar;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ENTRY_POINT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.WINDOW_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class WindowReferenceGenerator {

    final PackageModel packageModel;
    final TypeResolver typeResolver;

    public WindowReferenceGenerator(PackageModel packageModel, TypeResolver typeResolver) {
        this.packageModel = packageModel;
        this.typeResolver = typeResolver;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr, RuleContext context) {
        if (sourceDescr instanceof WindowReferenceDescr) {
            final WindowReferenceDescr source = ((WindowReferenceDescr) sourceDescr);
            final String windowVariable = toVar(source.getName());
            if (packageModel.getWindowReferences().containsKey(windowVariable)) {
                return Optional.of(new NameExpr(windowVariable));
            } else {
                context.addCompilationError(new DescrBuildError(context.getParentDescr(), source, null, "Unknown window " + source.getName()));
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public void addWindowReferences(TypeDeclarationContext typeDeclarationContext, BuildResultCollector results, Set<WindowDeclarationDescr> windowReferences ) {
        for (WindowDeclarationDescr descr : windowReferences) {
            addField(typeDeclarationContext, results, packageModel, descr);
        }
    }

    private void addField( TypeDeclarationContext typeDeclarationContext, BuildResultCollector results, PackageModel packageModel, WindowDeclarationDescr descr ) {

        final String windowName = toVar(descr.getName());

        final MethodCallExpr initializer = createDslTopLevelMethod(WINDOW_CALL);

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

        initializer.addArgument(new ClassExpr(toJavaParserType(initClass)));

        if (pattern.getSource() != null) {
            String epName = (( EntryPointDescr ) pattern.getSource()).getEntryId();
            MethodCallExpr entryPointCall = createDslTopLevelMethod(ENTRY_POINT_CALL);
            entryPointCall.addArgument( toStringLiteral(epName) );
            initializer.addArgument( entryPointCall );
        }

        parseConditions(typeDeclarationContext, results, packageModel, pattern, initClass).forEach(initializer::addArgument);

        packageModel.addAllWindowReferences(windowName, initializer);
    }

    private List<Expression> parseConditions(TypeDeclarationContext typeDeclarationContext, BuildResultCollector results, PackageModel packageModel, PatternDescr pattern, Class<?> patternType ) {
        List<? extends BaseDescr> descrs = pattern.getConstraint().getDescrs();
        if (descrs == null) {
            return Collections.emptyList();
        }
        return descrs.stream()
                .map( descr -> {
                    String expression = descr.toString();
                    RuleContext context = new RuleContext(typeDeclarationContext, results, packageModel, typeResolver, null);
                    DrlxParseResult drlxParseResult = ConstraintParser.defaultConstraintParser(context, packageModel).drlxParse(patternType, pattern.getIdentifier(), expression);
                    return drlxParseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<Expression>>() {
                        @Override
                        public Optional<Expression> onSuccess(DrlxParseSuccess drlxParseResult) {
                            return Optional.of(generateLambdaWithoutParameters( (( SingleDrlxParseSuccess ) drlxParseResult).getUsedDeclarations(), drlxParseResult.getExpr()));
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
        return (TemporalLiteralChunkExpr) te.getChunks().iterator().next();
    }

}