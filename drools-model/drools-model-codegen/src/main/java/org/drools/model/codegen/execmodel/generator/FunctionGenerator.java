package org.drools.model.codegen.execmodel.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import org.drools.drl.ast.descr.FunctionDescr;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class FunctionGenerator {

    public static MethodDeclaration toFunction(FunctionDescr desc) {

        List<Parameter> parameters = new ArrayList<>();

        List<String> parameterTypes = desc.getParameterTypes();
        for (int i = 0; i < parameterTypes.size(); i++) {
            String type = parameterTypes.get(i);
            String name = desc.getParameterNames().get(i);
            parameters.add(new Parameter(toClassOrInterfaceType(type), name));
        }

        NodeList<Modifier> modifiers = NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier());
        MethodDeclaration methodDeclaration = new MethodDeclaration(modifiers, desc.getName(), toClassOrInterfaceType(desc.getReturnType()), nodeList(parameters));

        BlockStmt block = DrlxParseUtil.parseBlock("try {} catch (Exception e) { throw new RuntimeException(e); }");
        TryStmt tryStmt = (TryStmt) block.getStatement( 0 );
        tryStmt.setTryBlock( DrlxParseUtil.parseBlock(desc.getBody() ) );

        methodDeclaration.setBody( block );

        return methodDeclaration;
    }
}
