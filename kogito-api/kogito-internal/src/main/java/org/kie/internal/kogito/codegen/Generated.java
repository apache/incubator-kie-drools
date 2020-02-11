/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.kogito.codegen;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface Generated {
   /**
    * The value element MUST have the name of the code generator.
    */
   String[] value();
   /**
    * A reference identifier that the generated class refers to
    * @return reference identifier
    */
   String reference();
   
   /**
    * A optional name to be used
    * @return alternative name
    */
   String name() default "";
   
   /**
    * Optional flag indicating that the generated class shall be hidden from 
    * other generators.
    * @return true if the class should be hidden otherwise false
    */
   boolean hidden() default false;
}