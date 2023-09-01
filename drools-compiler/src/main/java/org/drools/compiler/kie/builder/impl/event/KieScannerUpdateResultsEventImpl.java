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
package org.drools.compiler.kie.builder.impl.event;

import org.kie.api.builder.Results;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;

public class KieScannerUpdateResultsEventImpl implements KieScannerUpdateResultsEvent {
    private final Results results;
    public KieScannerUpdateResultsEventImpl(Results results) {
        this.results = results;
    }
    public Results getResults() {
        return results;
    }
}
