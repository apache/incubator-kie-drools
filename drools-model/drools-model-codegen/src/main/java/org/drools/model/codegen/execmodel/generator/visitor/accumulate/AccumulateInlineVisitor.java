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
package org.drools.model.codegen.execmodel.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.base.util.Drools.hasMvel;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.addSemicolon;
import static org.drools.model.codegen.execmodel.generator.visitor.accumulate.AccumulateVisitor.collectNamesInBlock;

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
            Optional<String> boundIdentifier = findBoundIdentifier(descr, input);
            if ( boundIdentifier.isPresent() ) {
                try {
                    accumulateInline.visitAccInlineCustomCode( accumulateDSL, externalDeclrs, boundIdentifier.get() );
                } catch (UnsupportedInlineAccumulate e) {
                    if (hasMvel()) {
                        new LegacyAccumulate( context, descr, basePattern, accumulateInline.getUsedExternalDeclarations() ).build();
                    } else {
                        throw new RuntimeException("Legacy accumulate can be used only with drools-mvel on classpath");
                    }
                } catch (MissingSemicolonInlineAccumulateException e) {
                    context.addCompilationError( new InvalidExpressionErrorResult( e.getMessage() ) );
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

    private Optional<String> findBoundIdentifier(AccumulateDescr descr, BaseDescr input) {
        BlockStmt actionBlock = parseBlockAddSemicolon( descr.getActionCode() );
        Collection<String> allNamesInActionBlock = collectNamesInBlock( actionBlock, context );

        if (allNamesInActionBlock.size() == 1) {
            return Optional.of(allNamesInActionBlock.iterator().next());
        }

        if (input instanceof PatternDescr) {
            return Optional.of((( PatternDescr ) input).getIdentifier());
        }

        return (( AndDescr ) input).getDescrs()
                        .stream()
                        .filter( b -> (( PatternDescr ) b).getAllBoundIdentifiers().containsAll( allNamesInActionBlock ) )
                        .findFirst().map( binding -> (( PatternDescr ) binding).getIdentifier() );

    }
}
