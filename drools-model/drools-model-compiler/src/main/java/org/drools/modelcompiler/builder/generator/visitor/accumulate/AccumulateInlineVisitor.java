/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addSemicolon;
import static org.drools.modelcompiler.builder.generator.visitor.accumulate.AccumulateVisitor.collectNamesInBlock;

public class AccumulateInlineVisitor {

    protected final RuleContext context;
    protected final PackageModel packageModel;

    public AccumulateInlineVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void inlineAccumulate(AccumulateDescr descr, PatternDescr basePattern, MethodCallExpr accumulateDSL, Set<String> externalDeclrs, BaseDescr input) {
        // LEGACY: Accumulate with inline custom code
        AccumulateInline accumulateInline = new AccumulateInline(context, packageModel, descr, basePattern);
        context.pushExprPointer(accumulateDSL::addArgument);
        try {
            if ( input instanceof PatternDescr ) {
                try {
                    accumulateInline.visitAccInlineCustomCode( accumulateDSL, externalDeclrs, (( PatternDescr ) input).getIdentifier() );
                } catch (UnsupportedInlineAccumulate e) {
                    new LegacyAccumulate( context, descr, basePattern, accumulateInline.getUsedExternalDeclarations() ).build();
                } catch (MissingSemicolonInlineAccumulateException e) {
                    context.addCompilationError( new InvalidExpressionErrorResult( e.getMessage() ) );
                }
            } else if ( input instanceof AndDescr ) {
                BlockStmt actionBlock = parseBlockAddSemicolon( descr.getActionCode() );
                Collection<String> allNamesInActionBlock = collectNamesInBlock( actionBlock, context );

                final Optional<BaseDescr> bindingUsedInAccumulate =
                        (( AndDescr ) input).getDescrs()
                                .stream()
                                .filter( b -> allNamesInActionBlock.contains( (( PatternDescr ) b).getIdentifier() ) )
                                .findFirst();

                if ( bindingUsedInAccumulate.isPresent() ) {
                    BaseDescr binding = bindingUsedInAccumulate.get();
                    try {
                        accumulateInline.visitAccInlineCustomCode( accumulateDSL, externalDeclrs, (( PatternDescr ) binding).getIdentifier() );
                    } catch (UnsupportedInlineAccumulate e) {
                        new LegacyAccumulate( context, descr, basePattern, accumulateInline.getUsedExternalDeclarations() ).build();
                    }
                }
            } else {
                throw new UnsupportedOperationException( "I was expecting input to be of type PatternDescr. " + input );
            }
        } finally {
            context.popExprPointer();
        }
    }

    private BlockStmt parseBlockAddSemicolon(String block) {
        return StaticJavaParser.parseBlock(String.format("{%s}", addSemicolon(block)));
    }


}
