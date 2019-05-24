/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.remote;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation should be used by users to specify (user-defined) classes that
 * should be available to remote services (REST, JMS, WS) when using a workbench.
 * <br>
 * A user will use this annotation on a class that's either part of a kjar or deployment
 * or on a class that is a dependency of the aforementioned kjar or deployment. When this deployment
 * is deployed, the workbench will scan the deployment and the dependency tree of the deployment
 * for classes marked with this annotation.
 * <br>
 * Classes at the top level in the deployment itself marked with the appropriate JAXB annotations
 * are always available for use with the remote services (regardless of whether the classes
 * are annotated with the {@code @Remotable} annotation or not).
 * However, in the dependency tree of the deployment, only classes marked with this annotation
 * (and with the appropriate JAXB annotations) will be usable with the workbench's remote services.
 * <br>
 * The @Remotable annotation is <em>not</em> inherited.
 */

@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface Remotable {

}
