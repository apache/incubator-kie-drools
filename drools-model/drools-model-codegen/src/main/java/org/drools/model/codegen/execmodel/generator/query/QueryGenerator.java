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
package org.drools.model.codegen.execmodel.generator.query;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;

/**
 * Used to generate n-arity query model, copy and paste the result in the files in comment
 */
public class QueryGenerator {

    public static void main(String[] args) {
        int arity = 10;

        CompilationUnit patternDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzPatternDSL = patternDSL.addClass("PatternDSL");
        range(arity).forEach(arity1 -> new PatternDSLQueryGenerator(clazzPatternDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzPatternDSL));

        CompilationUnit flowDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzFlowDSL = flowDSL.addClass("FlowDSL");
        range(arity).forEach(arity1 -> new FlowDSLQueryGenerator(clazzFlowDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzFlowDSL));

        range(arity).forEach(QueryGenerator::generateQueryDef);
        range(arity).forEach(QueryGenerator::generateQueryDefImpl);
    }

    private static IntStream range(int arity) {
        return IntStream.range(1, arity + 1);
    }

    private static void generateQueryDefImpl(int arity) {
        QueryDefImplGenerator queryDefImplGenerator = new QueryDefImplGenerator(arity);
        CompilationUnit queryDefImpl = queryDefImplGenerator.generate();
        String generatedClass = new PrettyPrinter().print(queryDefImpl);
        try {
            Path queryimpl = Paths.get("/tmp/", "queryimpl", queryDefImplGenerator.getClassName() + ".java");
            Files.createDirectories(queryimpl.getParent());
            Files.write(queryimpl, generatedClass.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateQueryDef(int arity) {
        QueryDefGenerator queryDefGenerator = new QueryDefGenerator(arity);
        CompilationUnit queryDef = queryDefGenerator.generate();
        String generatedClass = new PrettyPrinter().print(queryDef);
        try {
            Path querydef = Paths.get("/tmp/", "querydef", queryDefGenerator.getClassName() + ".java");
            Files.createDirectories(querydef.getParent());
            Files.write(querydef, generatedClass.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
