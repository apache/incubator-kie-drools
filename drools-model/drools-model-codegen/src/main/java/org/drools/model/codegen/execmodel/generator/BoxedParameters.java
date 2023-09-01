package org.drools.model.codegen.execmodel.generator;

import java.util.Collection;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;

public class BoxedParameters {

    RuleContext context;

    public BoxedParameters(RuleContext context) {
        this.context = context;
    }

    // Types in the executable model are promoted to boxed to type check the Java DSL.
    // We add such promoted types as _<PARAMETER_NAME> (with the underscore prefix)
    // and then we downcast to the original unboxed type in the body of the function (methodBody)
    public NodeList<Parameter> getBoxedParametersWithUnboxedAssignment(Collection<String> declarationUsedInRHS,
                                                                       BlockStmt methodBody) {

        NodeList<Parameter> parameters = NodeList.nodeList();

        for (String parameterName : declarationUsedInRHS) {
            DeclarationSpec declaration = context.getDeclarationByIdWithException(parameterName);

            Parameter boxedParameter;
            Type boxedType = declaration.getBoxedType();

            if (declaration.isBoxed()) {
                String boxedParameterName = "_" + parameterName;
                boxedParameter = new Parameter(boxedType, boxedParameterName);
                Expression unboxedTypeDowncast = new VariableDeclarationExpr(new VariableDeclarator(declaration.getRawType(),
                                                                                                    parameterName,
                                                                                                    new NameExpr(boxedParameterName)));
                methodBody.addStatement(0, unboxedTypeDowncast);
            } else {
                boxedParameter = new Parameter(boxedType, parameterName);
            }
            parameters.add(boxedParameter);
        }
        return parameters;
    }
}
