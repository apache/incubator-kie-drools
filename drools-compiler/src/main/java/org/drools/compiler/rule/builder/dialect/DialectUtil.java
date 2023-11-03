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
package org.drools.compiler.rule.builder.dialect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaCatchBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaTryBlockDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.memorycompiler.resources.ResourceReader;

import static org.drools.util.ClassUtils.findClass;

public final class DialectUtil {

    private static final Pattern NON_ALPHA_REGEX = Pattern.compile("[\\W]");

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     */
    public static String getUniqueLegalName(final String packageName,
                                            final String name,
                                            final int seed,
                                            final String ext,
                                            final String prefix,
                                            final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        final String newName = prefix + "_" + normalizeRuleName( name );
        if (ext.equals("java")) {
            return newName + Math.abs(seed);
        }

        final String fileName = packageName.replace('.', '/') + "/" + newName;

        if (src == null || !src.isAvailable(fileName + "." + ext)) {
            return newName;
        }

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        while (true) {

            counter++;
            final String actualName = fileName + "_" + counter + "." + ext;

            //MVEL:test null to Fix failing test on MVELConsequenceBuilderTest.testImperativeCodeError()
            if (!src.isAvailable(actualName)) {
                break;
            }
        }
        // we have duplicate file names so append counter
        return newName + "_" + counter;
    }

    public static List<JavaBlockDescr> buildBlockDescrs(List<JavaBlockDescr> descrs,
                                                        JavaContainerBlockDescr parentBlock) {
        for (JavaBlockDescr block : parentBlock.getJavaBlockDescrs()) {
            if (block instanceof JavaContainerBlockDescr) {
                buildBlockDescrs(descrs, (JavaContainerBlockDescr) block);
            } else {
                descrs.add(block);
            }
        }
        return descrs;
    }

    private static void addWhiteSpaces(String original, StringBuilder consequence, int start, int end) {
        for (int i = start; i < end; i++) {
            switch (original.charAt(i)) {
                case '\n':
                case '\r':
                case '\t':
                case ' ':
                    consequence.append(original.charAt(i));
                    break;
                default:
                    consequence.append(" ");
            }
        }
    }

    private static void stripTryDescr(String originalCode,
                                      StringBuilder consequence,
                                      JavaTryBlockDescr block,
                                      int offset) {

        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getTextStart() - offset);
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd() - offset);

        for (JavaCatchBlockDescr catchBlock : block.getCatches()) {

            addWhiteSpaces(originalCode, consequence, consequence.length(),
                    catchBlock.getTextStart() - offset);
            addWhiteSpaces(originalCode, consequence, consequence.length(),
                    catchBlock.getEnd() - offset);
        }

        if (block.getFinal() != null) {
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getTextStart() - offset);
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getEnd() - offset);
        }
    }

    private static void stripBlockDescr(String originalCode,
                                        StringBuilder consequence,
                                        JavaBlockDescr block,
                                        int offset) {

        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd() - offset);
    }

    public static Class<?> findClassByName(RuleBuildContext context, String className) {
        if (className == null) {
            return null;
        }

        String namespace = context.getRuleDescr().getNamespace();
        TypeDeclarationContext packageBuilder = context.getKnowledgeBuilder();

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className.indexOf('.') < 0 ? namespace + "." + className : className, false, packageBuilder.getRootClassLoader());
        } catch (ClassNotFoundException e) { }

        if (clazz != null) {
            return clazz;
        }

        Set<String> imports = new HashSet<>();
        List<PackageDescr> pkgDescrs = packageBuilder.getPackageDescrs(namespace);
        if (pkgDescrs == null) {
            return null;
        }
        for (PackageDescr pkgDescr : pkgDescrs) {
            for (ImportDescr importDescr : pkgDescr.getImports()) {
                imports.add(importDescr.getTarget());
            }
        }
        return findClass(className, imports, packageBuilder.getRootClassLoader());
    }

    static String normalizeRuleName(String name) {
        String normalized = name.replace(' ', '_');
        if (!NON_ALPHA_REGEX.matcher(normalized).find()) {
            return normalized;
        }
        StringBuilder sb = new StringBuilder(normalized.length());
        for (char ch : normalized.toCharArray()) {
            if (ch == '$') {
                sb.append("_dollar_");
            } else if (Character.isJavaIdentifierPart(ch)) {
                sb.append(ch);
            } else {
                sb.append("$u").append((int)ch).append("$");
            }
        }
        return sb.toString();
    }
}
