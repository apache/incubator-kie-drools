/*
 * Copyright (C) 2009 The JSR-330 Expert Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.inject;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * Identifies scope annotations. A scope annotation applies to a class
 * containing an injectable constructor and governs how the injector reuses
 * instances of the type. By default, if no scope annotation is present, the
 * injector creates an instance (by injecting the type's constructor), uses
 * the instance for one injection, and then forgets it. If a scope annotation
 * is present, the injector may retain the instance for possible reuse in a
 * later injection. If multiple threads can access a scoped instance, its
 * implementation should be thread safe. The implementation of the scope
 * itself is left up to the injector.
 *
 * <p>In the following example, the scope annotation {@code @Singleton} ensures
 * that we only have one Log instance:
 *
 * <pre>
 *   &#064;Singleton
 *   class Log {
 *     void log(String message) { ... }
 *   }</pre>
 *
 * <p>The injector generates an error if it encounters more than one scope
 * annotation on the same class or a scope annotation it doesn't support.
 *
 * <p>A scope annotation:
 * <ul>
 *   <li>is annotated with {@code @Scope}, {@code @Retention(RUNTIME)},
 *      and typically {@code @Documented}.</li>
 *   <li>should not have attributes.</li>
 *   <li>is typically not {@code @Inherited}, so scoping is orthogonal to
 *      implementation inheritance.</li>
 *   <li>may have restricted usage if annotated with {@code @Target}. While
 *      this specification covers applying scopes to classes only, some
 *      injector configurations might use scope annotations
 *      in other places (on factory method results for example).</li>
 * </ul>
 *
 * <p>For example:
 *
 * <pre>
 *   &#064;java.lang.annotation.Documented
 *   &#064;java.lang.annotation.Retention(RUNTIME)
 *   &#064;javax.inject.Scope
 *   public @interface RequestScoped {}</pre>
 *
 * <p>Annotating scope annotations with {@code @Scope} helps the injector
 * detect the case where a programmer used the scope annotation on a class but
 * forgot to configure the scope in the injector. A conservative injector
 * would generate an error rather than not apply a scope.
 *
 * @see javax.inject.Singleton @Singleton
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
@Documented
public @interface Scope {}
