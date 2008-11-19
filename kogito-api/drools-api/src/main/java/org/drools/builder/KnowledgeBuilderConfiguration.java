package org.drools.builder;

import org.drools.PropertiesConfiguration;

/**
 * This class configures the knowledge package builder and compiler.
 * Dialects and their DialectConfigurations  are handled by the DialectRegistry
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 * 
 * So if you want to set a default configuration value for all your new KnowledgeBuilder, you can simply set the property as
 * a System property.
 *
 * This class is not thread safe and it also contains state. After the KnowledgeBuilder is created, it makes the configuration 
 * immutable and there is no way to make it mutable again. This is to avoid inconsistent behaviour inside KnowledgeBuilder.
 *
 * drools.dialect.default = <String>
 * drools.accumulate.function.<function name> = <qualified class>
 * drools.evaluator.<ident> = <qualified class>
 * drools.dump.dir = <String>
 *
 * default dialect is java.
 * Available preconfigured Accumulate functions are:
 * drools.accumulate.function.average = org.drools.base.accumulators.AverageAccumulateFunction
 * drools.accumulate.function.max = org.drools.base.accumulators.MaxAccumulateFunction
 * drools.accumulate.function.min = org.drools.base.accumulators.MinAccumulateFunction
 * drools.accumulate.function.count = org.drools.base.accumulators.CountAccumulateFunction
 * drools.accumulate.function.sum = org.drools.base.accumulators.SumAccumulateFunction
 */
public interface KnowledgeBuilderConfiguration
    extends
    PropertiesConfiguration {

}
