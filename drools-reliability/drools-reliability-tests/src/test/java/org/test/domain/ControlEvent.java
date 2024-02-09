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
package org.test.domain;

import java.io.Serializable;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("60s")
public class ControlEvent implements Serializable  {

    private StockTick stockTick;

    public ControlEvent(StockTick stockTick) {
        this.stockTick = stockTick;
    }

    public StockTick getStockTick() {
        return stockTick;
    }

    public void setStockTick(StockTick stockTick) {
        this.stockTick = stockTick;
    }

    @Override
    public String toString() {
        return "ControlEvent{" +
                "stockTick=" + stockTick +
                '}';
    }
}
