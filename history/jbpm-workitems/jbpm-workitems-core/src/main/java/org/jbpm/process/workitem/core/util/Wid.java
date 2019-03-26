/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.workitem.core.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jbpm.process.workitem.core.util.service.WidService;

/**
 * Defines an annotated class that is of type Wid.
 * Used for WorkItemHandler implmenentations to define
 * values for the handlers workitem configuration.
 * Can be used to auto-generate the handlers workitem configuration (.wid) file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Wid {

    String widfile() default "";

    String name() default "";

    String displayName() default "";

    String category() default "";

    String icon() default "";

    String description() default "";

    String defaultHandler() default "";

    String documentation() default "";

    WidParameter[] parameters() default {};

    WidParameterValues[] parameterValues() default {};

    WidResult[] results() default {};

    WidMavenDepends[] mavenDepends() default {};

    WidService serviceInfo() default @WidService;
}
