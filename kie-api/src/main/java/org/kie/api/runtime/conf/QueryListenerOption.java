package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * An enum to configure the session query listener configuration.
 *
 * Query results are collected by a listener class. The "STANDARD"
 * query listener class copies and disconnects fact handles and objects
 * for query results, making them somewhat resilient to some working
 * memory actions. But this copying is costly. For the cases where
 * no concurrency exists between query execution and other working memory
 * actions, a lightweight listener implementation can be used, preventing
 * the copy and improving query performance significantly.
 */
public enum QueryListenerOption implements SingleValueRuleRuntimeOption {

    STANDARD("standard"),
    LIGHTWEIGHT("lightweight");

    /**
     * The property name for the clock type configuration
     */
    public static final String PROPERTY_NAME = "drools.queryListener";

    public static OptionKey<QueryListenerOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private String             option;

    QueryListenerOption(String option) {
        this.option = option;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String getAsString() {
        return option;
    }

    public String toString() {
        return "QueryListenerClassOption( " + option + " )";
    }

    public static QueryListenerOption determineQueryListenerClassOption(String option) {
        if ( STANDARD.getAsString().equalsIgnoreCase( option ) ) {
            return STANDARD;
        } else if ( LIGHTWEIGHT.getAsString().equalsIgnoreCase( option ) ) {
            return LIGHTWEIGHT;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + option + "' for QueryListenerOption" );
    }

}
