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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessing;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.impl.domain.StockTick;

@EventProcessing(EventProcessingType.STREAM)
@Clock(ClockType.PSEUDO)
public class StockTickUnit implements RuleUnitData {
    private final DataStream<StockTick> stockTicks;
    private final List<StockTick> results = new ArrayList<>();

    public StockTickUnit() {
        this(DataSource.createStream());
    }

    public StockTickUnit(DataStream<StockTick> stockTicks) {
        this.stockTicks = stockTicks;
    }

    public DataStream<StockTick> getStockTicks() {
        return stockTicks;
    }

    public List<StockTick> getResults() {
        return results;
    }
}
