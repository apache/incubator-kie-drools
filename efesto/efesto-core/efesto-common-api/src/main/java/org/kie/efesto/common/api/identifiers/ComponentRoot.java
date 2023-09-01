/**
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
package org.kie.efesto.common.api.identifiers;

/**
 * Root path of a component.
 *
 * <p>
 * Each component should implement their root and provide a method of the form:
 *
 * <code><pre>
 *   IdSubclass get(Params...)
 * </pre></code>
 * <p>
 * The reason why there is no method in the interface is to allow implementors
 * to define a <code>get()</code> method with as many parameters as desired,
 * with the most appropriate types
 * <p>
 * e.g. these are all allowed
 *
 * <code><pre>
 *   public RuleUnitId get(Class<?> ruleUnitDefinition)
 *   public RuleUnitId get(String canonicalName)
 *   public DecisionId get(String namespace, String name)
 *   etc...
 * </pre></code>
 * <p>
 * A <code>get()</code> method should however <strong>always</strong>
 *
 * <ul>
 * <li>return a {@link LocalId} (or, sometimes an {@link Id} or
 * a {@link RemoteId} may be appropriate).</li>
 * <li>expect one or more parameters; these parameters must be used to construct the identifier
 * and, ideally, should appear somehow in the identifier (or at least, it should be possible
 * to map the identifier back on the originating parameters)</li>
 * </ul>
 * <p>
 * While component writers are expected to adhere to such rules,
 * they may otherwise choose to design these getters as they prefer.
 * <p>
 * The returned identifier should always be prefixed by the ComponentRoot prefix.
 * <p>
 * e.g. ProcessIds would start with <code>/processes</code>,
 * DecisionIds would start with <code>/decisions</code>, etc.
 * <p>
 * ComponentRoots may be addressable directly;
 * however, it is preferred to use them through some other interface.
 * <p>
 * e.g.:
 *
 * <code><pre>
 *   &#64;Inject AppRoot appRoot;
 *   // ...
 *   var id = appRoot.get(ProcessIds.class).get("my-process-id)
 * </pre></code>
 *
 *
 *
 * <code><pre>
 *   &#64;Inject ProcessIds processIds;
 *   // ...
 *   var id = processIds.get("my-process-id)
 * </pre></code>
 */
public interface ComponentRoot {
}
