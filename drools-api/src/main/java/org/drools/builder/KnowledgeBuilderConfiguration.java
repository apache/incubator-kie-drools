package org.drools.builder;

import org.drools.PropertiesConfiguration;

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
// * <li>drools.dialect.default = &lt;String&gt;</li>
 * <li>drools.accumulate.function.&lt;function name&gt; = &lt;qualified class&gt;</li>
 * <li>drools.evaluator.<ident> = &lt;qualified class&gt;</li>
 * <li>drools.dump.dir = &lt;String&gt;</li>
 * </ul>
 * </p>
 * 
 * <p>
 * default dialect is java.
 * Available pre-configured Accumulate functions are:
 * <ul>
 * <li>drools.accumulate.function.average = org.drools.base.accumulators.AverageAccumulateFunction</li>
 * <li>drools.accumulate.function.max = org.drools.base.accumulators.MaxAccumulateFunction</li>
 * <li>drools.accumulate.function.min = org.drools.base.accumulators.MinAccumulateFunction</li>
 * <li>drools.accumulate.function.count = org.drools.base.accumulators.CountAccumulateFunction</li>
 * <li>drools.accumulate.function.sum = org.drools.base.accumulators.SumAccumulateFunction</li>
 * </ul>
 * </p>
 */
public interface KnowledgeBuilderConfiguration
    extends
    PropertiesConfiguration {

}
