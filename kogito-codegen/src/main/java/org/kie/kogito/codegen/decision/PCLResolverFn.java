/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.decision;

import java.util.function.Function;

/**
 * Some Kogito CodeGen *Generator, like the DMN DecisionCodegen, might need to understand if certain class is available on the project's classpath dependencies
 * in a Drools' Project ClassLoader (PCL) fashion.<br/>
 * <br/>
 * For example: we might use internally of Kogito CodeGen the Eclipse MP for OAS, which contains also annotations.
 * However, a Kogito-maven-plugin (SB) based project, might opt NOT to use the Eclipse MP for OAS for its (generated) classes, and use io.swagger annotation instead.<br/>
 * <br/>
 * This function is meant to help understand which classes are available on the (kogito) project, Drools' PCL-style
 */
public interface PCLResolverFn extends Function<String, Boolean> {

}
