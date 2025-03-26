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
package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.kie.efesto.common.core.serialization.SerializerService;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

@SuppressWarnings("rawtypes")
public class EfestoOutputSerializerService implements SerializerService<EfestoOutput> {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<EfestoOutput> type() {
        return EfestoOutput.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public JsonSerializer<? extends EfestoOutput> ser() {
        return new EfestoOutputSerializer();
    }
}
