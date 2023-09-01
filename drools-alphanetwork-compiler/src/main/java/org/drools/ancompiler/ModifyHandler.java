package org.drools.ancompiler;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.core.reteoo.Sink;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;

public class ModifyHandler extends PropagatorCompilerHandler {

    public ModifyHandler(String factClassName, boolean alphaNetContainsHashedField) {
        super(alphaNetContainsHashedField, factClassName);
    }

    @Override
    protected Statement propagateMethod(Sink sink) {
        Statement modifyStatement;
        if (sinkCanBeInlined(sink)) {
            modifyStatement = parseStatement("ALPHATERMINALNODE.collectObject();");
        } else {
            modifyStatement = parseStatement("ALPHATERMINALNODE.modifyObject(handle, modifyPreviousTuples, context, wm);");
        }
        replaceNameExpr(modifyStatement, "ALPHATERMINALNODE", getVariableName(sink));
        return modifyStatement;
    }

    @Override
    protected NodeList<Parameter> methodParameters() {
        return nodeList(new Parameter(factHandleType(), FACT_HANDLE_PARAM_NAME),
                        new Parameter(modifyPreviousTuplesType(), MODIFY_PREVIOUS_TUPLE_PARAM_NAME),
                        new Parameter(propagationContextType(), PROP_CONTEXT_PARAM_NAME),
                        new Parameter(reteEvaluatorType(), WORKING_MEMORY_PARAM_NAME));
    }

    @Override
    protected NodeList<Expression> arguments() {
        return nodeList(new NameExpr(FACT_HANDLE_PARAM_NAME),
                        new NameExpr(MODIFY_PREVIOUS_TUPLE_PARAM_NAME),
                        new NameExpr(PROP_CONTEXT_PARAM_NAME),
                        new NameExpr(WORKING_MEMORY_PARAM_NAME));
    }


    @Override
    protected String propagateMethodName() {
        return "propagateModifyObject";
    }
}
