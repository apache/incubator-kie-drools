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
package org.drools.compiler.builder.impl.processors;

import java.util.Collection;

import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class TypeDeclarationAnnotationNormalizer implements CompilationPhase {
    private final AnnotationNormalizer annotationNormalizer;
    private final PackageDescr packageDescr;

    public TypeDeclarationAnnotationNormalizer(
            AnnotationNormalizer annotationNormalizer,
            PackageDescr packageDescr) {
        this.annotationNormalizer = annotationNormalizer;
        this.packageDescr = packageDescr;
    }

    public void process() {
        for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
            annotationNormalizer.normalize(typeDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : typeDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }

        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            annotationNormalizer.normalize(enumDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : enumDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return annotationNormalizer.getResults();
    }

}
