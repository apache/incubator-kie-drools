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
package org.drools.mvel;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.mvel2.CompiledJavaEvaluator;
import org.drools.mvelcompiler.CompiledResult;
import org.drools.mvelcompiler.ConstraintCompiler;
import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.kie.memorycompiler.KieMemoryCompiler;

public class Evaluator {

    public Serializable compileEvaluateWithDroolsMvelCompiler(Object compiledExpression,
                                                              Map<String, Object> vars,
                                                              ClassLoader classLoader)  {
        CompiledJavaEvaluator evaluator = compileWithDroolsMvelCompiler(compiledExpression, vars, classLoader);

        return (Serializable) evaluator.eval(vars);
    }

    public CompiledJavaEvaluator compileWithDroolsMvelCompiler(Object compiledExpression, Map<String, Object> vars,
			ClassLoader classLoader) {
		CompiledResult input = compileWithMvelCompiler(compiledExpression, vars, classLoader);

        CompilationUnit evaluatorSource = createEvaluatorClass((String) compiledExpression, input, vars);

        String javaFQN = evaluatorFullQualifiedName(evaluatorSource);

        Map<String, Class<?>> compiledClasses = compileEvaluatorClass(classLoader, evaluatorSource, javaFQN);

        Class<?> evaluatorDefinition = compiledClasses.get(javaFQN);
        CompiledJavaEvaluator evaluator = createEvaluatorInstance(evaluatorDefinition);
		return evaluator;
	}

    private String evaluatorFullQualifiedName(CompilationUnit evaluatorCompilationUnit) {
        ClassOrInterfaceDeclaration evaluatorClass = evaluatorCompilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new RuntimeException("class expected"));

        String evaluatorClassName = evaluatorClass.getNameAsString();
        Name packageName = evaluatorCompilationUnit.getPackageDeclaration().map(PackageDeclaration::getName)
                .orElseThrow(() -> new RuntimeException("No package in template"));
        return String.format("%s.%s", packageName, evaluatorClassName);
    }

    private CompiledJavaEvaluator createEvaluatorInstance(Class<?> evaluatorDefinition) {
        CompiledJavaEvaluator evaluator;
        try {
            evaluator = (CompiledJavaEvaluator) evaluatorDefinition.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return evaluator;
    }

    private Map<String, Class<?>> compileEvaluatorClass(ClassLoader classLoader, CompilationUnit evaluatorCompilationUnit, String javaFQN) {
        Map<String, String> sources = Collections.singletonMap(
                javaFQN,
                PrintUtil.printNode(evaluatorCompilationUnit)
        );
        return KieMemoryCompiler.compile(sources, classLoader);
    }

    private CompilationUnit createEvaluatorClass(String originalExpression,
                                                        CompiledResult input,
                                                        Map<String, Object> vars) {
        return new EvaluatorGenerator().createEvaluatorClass(originalExpression, input, vars);
    }

    private static CompiledResult compileWithMvelCompiler(Object compiledExpression, Map<String, Object> vars, ClassLoader classLoader) {
        String expressionString = compiledExpression.toString();

        Set<String> imports = new HashSet<>();
        imports.add("java.util.List");
        imports.add("java.util.ArrayList");
        imports.add("java.util.HashMap");
        imports.add("java.util.Map");
        imports.add("java.math.BigDecimal");
        imports.add("org.drools.Address");

        TypeResolver classTypeResolver = new ClassTypeResolver(imports, classLoader);
        MvelCompilerContext context = new MvelCompilerContext(classTypeResolver);

        for (Map.Entry<String, Object> o : vars.entrySet()) {

            Object value1 = o.getValue();
            if (value1 != null) {
                context.addDeclaration(o.getKey(), value1.getClass());
            }
        }

        MvelCompiler mvelCompiler = new MvelCompiler(context);
        ConstraintCompiler constraintCompiler = new ConstraintCompiler(context);

        if (isAStatement(expressionString)) {
            String expressionStringWithBraces = String.format("{%s}", expressionString);
            return mvelCompiler.compileStatement(expressionStringWithBraces);
        } else {
            return constraintCompiler.compileExpression(expressionString);
        }
    }

    private static boolean isAStatement(String expressionString) {
        boolean hasSemiColon = expressionString.contains(";");
        List<String> statementOperators = Arrays.asList("+=", "-=", "/=", "*=");

        for(String s :statementOperators) {
            if(expressionString.contains(s)) {
                return true;
            }
        }
        return hasSemiColon;
    }
}
