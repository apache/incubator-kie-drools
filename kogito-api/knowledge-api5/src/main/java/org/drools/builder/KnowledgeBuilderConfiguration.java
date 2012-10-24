/*
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

package org.drools.builder;

import org.drools.PropertiesConfiguration;
import org.drools.builder.conf.KnowledgeBuilderOptionsConfiguration;

/**
 * <p>
 * This class configures the knowledge package builder and compiler.
 * Dialects and their DialectConfigurations  are handled by the DialectRegistry
 * Normally you will not need to look at this class, unless you want to override the defaults.
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
 * So if you want to set a default configuration value for all your new KnowledgeBuilder, you can simply set the property as
 * a System property.
 * </p>
 * 
 * <p>
 * This class is not thread safe and it also contains state. After the KnowledgeBuilder is created, it makes the configuration 
 * immutable and there is no way to make it mutable again. This is to avoid inconsistent behaviour inside KnowledgeBuilder.
 * </p>
 * 
 * <p>
 * <ul>
 * <li>drools.dialect.default = &lt;String&gt;</li>
 * <li>drools.accumulate.function.&lt;function name&gt; = &lt;qualified class&gt;</li>
 * <li>drools.evaluator.<ident> = &lt;qualified class&gt;</li>
 * <li>drools.dump.dir = &lt;String&gt;</li>
 * <li>drools.parser.processStringEscapes = &lt;true|false&gt;</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Two dialects are supported, Java and MVEL. Java is the default dialect.<br/>
 * The Java dialect supports the following configurations:
 * <ul>
 * <li>drools.dialect.java.compiler = &lt;ECLIPSE|JANINO&gt;</li>
 * <li>drools.dialect.java.lngLevel = &lt;1.5|1.6&gt;</li>
 * </ul>
 * 
 * And MVEL supports the following configurations:
 * <ul>
 * <li>drools.dialect.mvel.strict = &lt;true|false&gt;</li>
 * </ul>
 * </p>
 * 
 * <p>
 * So for example if we wanted to create a new KnowledgeBuilder that used Janino as the default compiler we would do the following:<br/>
 * <pre>
 * KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
 * config.setProperty("drools.dialect.java.compiler", "JANINO");
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( config );
 * </pre>
 * </p>
 * 
 * <p>
 * Remember the KnowledgeBuilderConfiguration could have taken a Properties instance with that setting in it at constructor time,
 * or it could also discover from a disk based properties file too.
 * </p>
 * 
 * <p>
 * Available pre-configured Accumulate functions are:
 * <ul>
 * <li>drools.accumulate.function.average = org.drools.base.accumulators.AverageAccumulateFunction</li>
 * <li>drools.accumulate.function.max = org.drools.base.accumulators.MaxAccumulateFunction</li>
 * <li>drools.accumulate.function.min = org.drools.base.accumulators.MinAccumulateFunction</li>
 * <li>drools.accumulate.function.count = org.drools.base.accumulators.CountAccumulateFunction</li>
 * <li>drools.accumulate.function.sum = org.drools.base.accumulators.SumAccumulateFunction</li>
 * <li>drools.accumulate.function.collectSet = org.drools.base.accumulators.CollectSetAccumulateFunction</li>
 * <li>drools.accumulate.function.collectList = org.drools.base.accumulators.CollectListAccumulateFunction</li>
 * </ul>
 * </p>
 */
public interface KnowledgeBuilderConfiguration
    extends
    PropertiesConfiguration,
    KnowledgeBuilderOptionsConfiguration {

}
