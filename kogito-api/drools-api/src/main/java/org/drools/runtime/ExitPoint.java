/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime;

/**
 * <p>
 * An exit point provides a mechanism to send objects from the working memory to some external process 
 * or function.  For instance, an exit point can be used to inform some piece of code that an object 
 * matches a rule.  
 * </p>
 *
 * <p>
 * To create an exit point, implement the interface and register it with the KnowledgeRuntime:
 * </p>
 * <pre>
 * ...
 * ksession.registerExitPoint("my-exit-point", new MyExitPointImpl());
 * </pre>
 * 
 * <p>
 * Exit points are invoked from the consequence side of a rule:
 * </p>
 * <pre>
 * when
 *   ...
 * then
 *   exitPoints["my-exit-point"].insert(...);
 * </pre>
 * 
 * @deprecated Use Channel instead
 */
@Deprecated
public interface ExitPoint {
    void insert(Object object);
}
