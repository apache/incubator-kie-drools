/*
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
package org.drools.model.codegen.execmodel.util;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Comparator;

public class BodyDeclarationComparator implements Comparator<BodyDeclaration<?>> {

    public static final BodyDeclarationComparator INSTANCE = new BodyDeclarationComparator();

    private BodyDeclarationComparator() { }

    @Override
    public int compare(BodyDeclaration<?> o1, BodyDeclaration<?> o2) {
        if (o1 instanceof FieldDeclaration && o2 instanceof FieldDeclaration) {
            return 0;
        }
        if (o1 instanceof FieldDeclaration && !(o2 instanceof FieldDeclaration)) {
            return -1;
        }

        if (o1 instanceof ConstructorDeclaration && o2 instanceof ConstructorDeclaration) {
            return 0;
        }
        if (o1 instanceof ConstructorDeclaration && o2 instanceof MethodDeclaration) {
            return -1;
        }
        if (o1 instanceof ConstructorDeclaration && o2 instanceof FieldDeclaration) {
            return 1;
        }
        return 1;
    }

}
