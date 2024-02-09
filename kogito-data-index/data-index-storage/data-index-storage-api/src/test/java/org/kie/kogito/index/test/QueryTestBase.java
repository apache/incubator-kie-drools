/*
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
package org.kie.kogito.index.test;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiConsumer;

import org.kie.kogito.persistence.api.StorageFetcher;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;

public abstract class QueryTestBase<K, V> {

    protected Boolean isDateTimeAsLong() {
        return true;
    }

    public void queryAndAssert(BiConsumer<List<V>, K[]> assertConsumer, StorageFetcher<K, V> storage, List<AttributeFilter<?>> filters, List<AttributeSort> sort, Integer offset, Integer limit,
            K... ids) {
        assertConsumer.accept(storage.query().filter(filters).sort(sort).offset(offset).limit(limit).execute(), ids);
    }

    protected Object getDateTime(ZonedDateTime dateTime) {
        return isDateTimeAsLong() ? dateTime.toInstant().toEpochMilli() : dateTime;
    }

    protected Object getDateTime() {
        return isDateTimeAsLong() ? Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli() : ZonedDateTime.now().plus(1, ChronoUnit.DAYS);
    }
}
