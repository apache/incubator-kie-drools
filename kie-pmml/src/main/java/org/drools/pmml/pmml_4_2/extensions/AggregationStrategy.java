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
