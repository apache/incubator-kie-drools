/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.compiler;

import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
public class StockTickEvent extends StockTick {

    public StockTickEvent() {
        super();
    }

    public StockTickEvent(final long seq,
                          final String company,
                          final double price,
                          final long time) {
        super(seq, company, price, time);
    }

    public StockTickEvent(final long seq,
                          final String company,
                          final double price,
                          final long time,
                          final long duration) {
        super(seq, company, price, time, duration);
    }
}
