package org.drools.pmml.pmml_4_2.extensions;


public enum AggregationStrategy {
    AGGREGATE_SCORE( "sum", false ),
    AVERAGE_SCORE( "average", false ),
    MAXIMUM_SCORE( "max", false ),
    MINIMUM_SCORE( "min", false ),
    WEIGHTED_AGGREGATE_SCORE( "sum", true ),
    WEIGHTED_AVERAGE_SCORE( "average", true ),
    WEIGHTED_MAXIMUM_SCORE( "max", true ),
    WEIGHTED_MINIMUM_SCORE( "min", true );

    private String aggregator;

    private boolean weighted;

    private AggregationStrategy( String aggregator, boolean weighted ) {
        this.aggregator = aggregator;
        this.weighted = weighted;
    }

    public String getAggregator() {
        return aggregator;
    }

    public boolean isWeighted() {
        return weighted;
    }

}
