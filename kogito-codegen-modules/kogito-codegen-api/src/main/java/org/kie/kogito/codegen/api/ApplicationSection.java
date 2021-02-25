/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.api;

import com.github.javaparser.ast.CompilationUnit;

/**
 * A descriptor for a "section" of the root Application class.
 * It contains a factory method for the section (which is an object instance)
 * and the corresponding class.
 *
 * This is to allow the pattern:
 * app.$sectionname().$method()
 *
 * e.g.:
 * app.get(Processes.class).createMyProcess()
 */
public interface ApplicationSection {

    String sectionClassName();

    CompilationUnit compilationUnit();

}
