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

package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.lang.descr.FunctionDescr;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;

import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;

public class FunctionGenerator {

    public static MethodDeclaration toFunction(FunctionDescr desc) {

        List<Parameter> parameters = new ArrayList<>();

        List<String> parameterTypes = desc.getParameterTypes();
        for (int i = 0; i < parameterTypes.size(); i++) {
            String type = parameterTypes.get(i);
            String name = desc.getParameterNames().get(i);
            parameters.add(new Parameter(parseType(type), name));
        }

        NodeList<Modifier> modifiers = NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier());
        MethodDeclaration methodDeclaration = new MethodDeclaration(modifiers, desc.getName(), parseType(desc.getReturnType()), nodeList(parameters));

        BlockStmt block = DrlxParseUtil.parseBlock("try {} catch (Exception e) { throw new RuntimeException(e); }");
        TryStmt tryStmt = (TryStmt) block.getStatement( 0 );
        tryStmt.setTryBlock( DrlxParseUtil.parseBlock(desc.getBody() ) );

        methodDeclaration.setBody( block );

        return methodDeclaration;
    }
}
