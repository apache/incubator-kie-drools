/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.kie.kogito.codegen.di;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;


public class SpringDependencyInjectionAnnotator implements DependencyInjectionAnnotator {

    @Override
    public void withApplicationComponent(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.stereotype.Component");
    }
    
    @Override
    public void withNamedApplicationComponent(NodeWithAnnotations<?> node, String name) {        
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.stereotype.Component"), new StringLiteralExpr(name)));
    }

    @Override
    public void withSingletonComponent(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.stereotype.Component");
    }
    
    @Override
    public void withNamedSingletonComponent(NodeWithAnnotations<?> node, String name) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.stereotype.Component"), new StringLiteralExpr(name)));
    }

    @Override
    public void withInjection(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.beans.factory.annotation.Autowired");
    }

    @Override
    public void withNamedInjection(NodeWithAnnotations<?> node, String name) {
        node.addAnnotation("org.springframework.beans.factory.annotation.Autowired");
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Qualifier"), new StringLiteralExpr(name)));
    }
    
    @Override
    public void withOptionalInjection(NodeWithAnnotations<?> node) {
        node.addAnnotation(new NormalAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Autowired"), NodeList.nodeList(new MemberValuePair("required", new BooleanLiteralExpr(false)))));
    }

    @Override
    public String multiInstanceInjectionType() {
        return List.class.getCanonicalName();
    }

    @Override
    public String applicationComponentType() {
        return "org.springframework.stereotype.Component";
    }

}
