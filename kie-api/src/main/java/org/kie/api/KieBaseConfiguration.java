/*
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
package org.kie.api;

import org.kie.api.conf.KieBaseOptionsConfiguration;

/**
 *<p>
 * A class to store KieBase related configuration. It must be used at KieBase instantiation time
 * or not used at all.
 * </p>
 *
 * <p>
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 * </p>
 *
 * <p>
 * So if you want to set a default configuration value for all your new KieBase, you can simply set the property as
 * a System property.
 * </p>
 *
 * <p>
 * After the KieBase is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour inside KieBase.
 * </p>
 *
 * <p>
 * The following properties are supported:
 * </p>
 * <ul>
 * <li>drools.maintainTms = &lt;true|false&gt;</li>
 * <li>drools.assertBehaviour = &lt;identity|equality&gt;</li>
 * <li>drools.logicalOverride = &lt;discard|preserve&gt;</li>
 * <li>drools.sequential = &lt;true|false&gt;</li>
 * <li>drools.sequential.agenda = &lt;sequential|dynamic&gt;</li>
 * <li>drools.removeIdentities = &lt;true|false&gt;</li>
 * <li>drools.shareAlphaNodes  = &lt;true|false&gt;</li>
 * <li>drools.shareBetaNodes = &lt;true|false&gt;</li>
 * <li>drools.alphaNodeHashingThreshold = &lt;1...n&gt;</li>
 * <li>drools.alphaNodeRangeIndexThreshold = &lt;1...n&gt;</li>
 * <li>drools.compositeKeyDepth  = &lt;1..3&gt;</li>
 * <li>drools.indexLeftBetaMemory = &lt;true/false&gt;</li>
 * <li>drools.indexRightBetaMemory = &lt;true/false&gt;</li>
 * <li>drools.consequenceExceptionHandler = &lt;qualified class name&gt;</li>
 * <li>drools.mbeans = &lt;enabled|disabled&gt;</li>
 * </ul>
 *
 * <p>
 * The follow properties have not yet been migrated from the Drools 4.0 api:
 * </p>
 * <ul>
 * <li>drools.executorService = &lt;qualified class name&gt;</li>
 * <li>drools.conflictResolver = &lt;qualified class name&gt;</li>
 * <li>drools.ruleBaseUpdateHandler = &lt;qualified class name&gt;</li>
 * </ul>
 */
public interface KieBaseConfiguration
        extends
        KieBaseOptionsConfiguration {

}
