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
package org.drools.impact.analysis.parser.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.util.ClassUtils;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.right.ConsequenceAction;
import org.drools.impact.analysis.model.right.InsertAction;
import org.drools.impact.analysis.model.right.InsertedProperty;
import org.drools.impact.analysis.model.right.ModifiedMapProperty;
import org.drools.impact.analysis.model.right.ModifiedProperty;
import org.drools.impact.analysis.model.right.ModifyAction;
import org.drools.model.codegen.execmodel.generator.Consequence;
import org.drools.model.codegen.execmodel.generator.TypedDeclarationSpec;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.util.StringUtils.ucFirst;
import static org.drools.impact.analysis.parser.impl.ParserUtil.getLiteralString;
import static org.drools.impact.analysis.parser.impl.ParserUtil.getLiteralValue;
import static org.drools.impact.analysis.parser.impl.ParserUtil.isLiteral;
import static org.drools.impact.analysis.parser.impl.ParserUtil.literalToValue;
import static org.drools.impact.analysis.parser.impl.ParserUtil.literalType;
import static org.drools.impact.analysis.parser.impl.ParserUtil.objectCreationExprToValue;
import static org.drools.impact.analysis.parser.impl.ParserUtil.stripEnclosedAndCast;

public class RhsParser {

    private final PackageRegistry pkgRegistry;

    public RhsParser( PackageRegistry pkgRegistry ) {
        this.pkgRegistry = pkgRegistry;
    }

    public void parse( RuleDescr ruleDescr, RuleContext context, Rule rule ) {
        BlockStmt ruleVariablesBlock = new BlockStmt();
        MethodCallExpr consequenceExpr = new Consequence(context).createCall( ruleDescr.getConsequence().toString(), ruleVariablesBlock, false );

        consequenceExpr.findAll(MethodCallExpr.class).stream()
                .filter( m -> m.getScope().map( s -> s.toString().equals( "drools" ) ).orElse( false ) )
                .map( m -> processStatement( context, consequenceExpr, m, ruleVariablesBlock ) )
                .filter( Objects::nonNull )
                .forEach( a -> rule.getRhs().addAction( a ) );
    }

    private ConsequenceAction processStatement( RuleContext context, MethodCallExpr consequenceExpr, MethodCallExpr statement, BlockStmt ruleVariablesBlock ) {
        ConsequenceAction.Type type = decodeAction( statement.getNameAsString() );
        if (type == null) {
            return null;
        }
        if (type == ConsequenceAction.Type.INSERT) {
            return processInsert(context, consequenceExpr, statement, ruleVariablesBlock);
        }
        if (type == ConsequenceAction.Type.MODIFY) {
            return processModify(context, consequenceExpr, statement, ruleVariablesBlock);
        }
        return processAction(context, consequenceExpr, statement, type);
    }

    private ConsequenceAction processAction( RuleContext context, MethodCallExpr consequenceExpr, MethodCallExpr statement, ConsequenceAction.Type type ) {
        Class<?> actionClass = getActionClass(context, consequenceExpr, statement);
        return new ConsequenceAction(type, actionClass);
    }

    private Class<?> getActionClass(RuleContext context, MethodCallExpr consequenceExpr, MethodCallExpr statement) {
        Expression actionArg = statement.getArgument(0);
        Class<?> actionClass = null;
        if (actionArg.isNameExpr()) {
            actionClass = context.getTypedDeclarationById(actionArg.toString()).map(TypedDeclarationSpec::getDeclarationClass)
                    .orElseGet(() -> getClassFromAssignment(consequenceExpr, actionArg));
        } else if (actionArg.isLiteralExpr()) {
            actionClass = literalType(actionArg.asLiteralExpr());
        } else if (actionArg.isObjectCreationExpr()) {
            try {
                actionClass = pkgRegistry.getTypeResolver().resolveType(actionArg.asObjectCreationExpr().getType().asString());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return actionClass;
    }

    private Class<?> getClassFromAssignment( MethodCallExpr consequenceExpr, Expression actionArg ) {
        String className = getClassNameFromAssignment( consequenceExpr, actionArg )
                .orElseGet( () -> getClassNameFromCreation( consequenceExpr, actionArg ) );
        try {
            return pkgRegistry.getTypeResolver().resolveType( className );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
    }

    private Optional<String> getClassNameFromAssignment( MethodCallExpr consequenceExpr, Expression actionArg ) {
        return consequenceExpr.findAll( AssignExpr.class ).stream()
                .filter( assign -> assign.getTarget().isVariableDeclarationExpr() &&
                         (( VariableDeclarationExpr ) assign.getTarget()).getVariable( 0 ).toString().equals( actionArg.toString() ) )
                .findFirst()
                .map( assignExpr -> assignExpr.getTarget().asVariableDeclarationExpr().getVariable( 0 ).getType().asString() );
    }

    private String getClassNameFromCreation( MethodCallExpr consequenceExpr, Expression actionArg ) {
        return consequenceExpr.findAll( VariableDeclarator.class ).stream()
                .filter( varDecl -> varDecl.getName().toString().equals( actionArg.toString() ) &&
                         varDecl.getInitializer().filter( ObjectCreationExpr.class::isInstance ).isPresent() )
                .map( varDecl -> ( ObjectCreationExpr ) varDecl.getInitializer().get() )
                .map( objCreat -> objCreat.getType().getNameAsString() )
                .findFirst()
                .orElseGet( () -> getClassNameFromDeclaration(consequenceExpr, actionArg) );
    }

    private String getClassNameFromDeclaration(MethodCallExpr consequenceExpr, Expression actionArg) {
        return consequenceExpr.findAll( VariableDeclarator.class ).stream()
                .filter( varDecl -> varDecl.getName().toString().equals( actionArg.toString() ))
                .map( varDecl -> varDecl.getTypeAsString() )
                .findFirst()
                .orElseThrow( () -> new RuntimeException("Unknown variable: " + actionArg) );
    }

    private InsertAction processInsert( RuleContext context, MethodCallExpr consequenceExpr, MethodCallExpr statement, BlockStmt ruleVariablesBlock ) {
        Class<?> actionClass = getActionClass(context, consequenceExpr, statement);
        InsertAction action = new InsertAction(actionClass);
        Expression insertedArgument = statement.getArgument(0);
        String insertedId = insertedArgument.toString();

        // Process setters
        List<MethodCallExpr> insertedExprs = consequenceExpr.findAll(MethodCallExpr.class).stream()
                .filter(m -> m.getScope().map(s -> s.toString().equals(insertedId) || s.toString().equals("(" + insertedId + ")")).orElse(false))
                .collect(Collectors.toList());
        for (MethodCallExpr expr : insertedExprs) {
            String methodName = expr.getNameAsString();
            String property = ClassUtils.setter2property(methodName);
            if (property != null) {
                Object value = getLiteralValue(context, expr.getArgument(0));
                action.addInsertedProperty(new InsertedProperty(property, value));
            }
        }

        // Process literal insert
        if (isLiteral(actionClass) && insertedArgument.isLiteralExpr()) {
            action.addInsertedProperty(new InsertedProperty("this", literalToValue(insertedArgument.asLiteralExpr())));
        }
        return action;
    }

    private ModifyAction processModify( RuleContext context, MethodCallExpr consequenceExpr, MethodCallExpr statement, BlockStmt ruleVariablesBlock ) {
        String modifiedId = statement.getArgument( 0 ).toString();
        Class<?> modifiedClass = context.getTypedDeclarationById(modifiedId ).orElseThrow(() -> new RuntimeException("Unknown declaration: " + modifiedId) ).getDeclarationClass();

        ModifyAction action = new ModifyAction(modifiedClass);

        if (statement.getArguments().size() > 1) {
            String maskId = statement.getArgument( 1 ).toString();
            AssignExpr maskAssignExpr = ruleVariablesBlock.findAll( AssignExpr.class ).stream()
                    .filter( assign -> (( VariableDeclarationExpr ) assign.getTarget()).getVariable( 0 ).toString().equals( maskId ) )
                    .findFirst().orElseThrow( () -> new RuntimeException("Unknown mask: " + maskId) );

            MethodCallExpr maskMethod = (( MethodCallExpr ) maskAssignExpr.getValue());

            List<MethodCallExpr> modifyingExprs = consequenceExpr.findAll(MethodCallExpr.class).stream()
                    .filter( m -> m.getScope().map( s -> s.toString().equals( modifiedId ) || s.toString().equals( "(" + modifiedId + ")" ) ).orElse( false ) )
                    .collect( Collectors.toList());

            for (int i = 1; i < maskMethod.getArguments().size(); i++) {
                String property = maskMethod.getArgument( i ).asStringLiteralExpr().asString();
                String setter = "set" + ucFirst(property);
                MethodCallExpr setterExpr = modifyingExprs.stream()
                        .filter( m -> m.getNameAsString().equals( setter ) )
                        .filter( m -> m.getArguments().size() == 1 )
                        .findFirst().orElse( null );

                Object value = null;
                if (setterExpr != null) {
                    Expression arg = setterExpr.getArgument( 0 );
                    arg = stripEnclosedAndCast(arg);
                    if (arg.isLiteralExpr()) {
                        value = literalToValue( arg.asLiteralExpr() );
                    } else if (arg.isNameExpr()) {
                        value = ((ImpactAnalysisRuleContext)context).getBindVariableLiteralMap().get(arg.asNameExpr().getName().asString());
                    } else if (arg.isObjectCreationExpr()) {
                        value = objectCreationExprToValue((ObjectCreationExpr)arg, context);
                    }
                }

                Method accessor = ClassUtils.getAccessor(modifiedClass, property);
                if (accessor != null && Map.class.isAssignableFrom(accessor.getReturnType())) {
                    String mapName = property;
                    List<MethodCallExpr> mapPutExprs = consequenceExpr.findAll(MethodCallExpr.class).stream()
                                                                      .filter(m -> isMapPutExpr(m, modifiedId, accessor.getName()))
                                                                      .collect(Collectors.toList());
                    mapPutExprs.stream().forEach(expr -> {
                        String mapKey = getLiteralString(context, expr.getArgument(0));
                        Object mapValue = getLiteralValue(context, expr.getArgument(1));
                        action.addModifiedProperty(new ModifiedMapProperty(mapName, mapKey, mapValue));
                    });

                } else {
                    action.addModifiedProperty(new ModifiedProperty(property, value));
                }
            }
        }

        return action;
    }

    private boolean isMapPutExpr(MethodCallExpr mce, String modifiedId, String accessorName) {
        if (!mce.getName().asString().equals("put")) {
            return false;
        }
        return mce.getScope()
                  .filter(Expression::isMethodCallExpr)
                  .map(Expression::asMethodCallExpr)
                  .filter(scopeMce -> scopeMce.getName().asString().equals(accessorName))
                  .flatMap(scopeMce -> scopeMce.getScope())
                  .filter(parentScope -> parentScope.toString().equals(modifiedId) || parentScope.toString().equals("(" + modifiedId + ")"))
                  .isPresent();
    }

    private ConsequenceAction.Type decodeAction(String name) {
        switch (name) {
            case "insert":
                return ConsequenceAction.Type.INSERT;
            case "delete":
                return ConsequenceAction.Type.DELETE;
            case "update":
                return ConsequenceAction.Type.MODIFY;
        }
        return null;
    }
}
