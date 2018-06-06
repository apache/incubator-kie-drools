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
package org.jbpm.process.workitem.core.util.service;

import java.lang.annotation.Documented;

/**
 * Defines an annotated class that is of type Service.
 * Used for WorkItemHandler implmenentations to define
 * values for the Service configuration.
 * Can be used to auto-generate stand-alone Service applets.
 */

@Documented
public @interface WidService {

    String category() default "";

    String description() default "";

    String keywords() default "";

    WidTrigger trigger() default @WidTrigger;

    WidAction action() default @WidAction;
}
