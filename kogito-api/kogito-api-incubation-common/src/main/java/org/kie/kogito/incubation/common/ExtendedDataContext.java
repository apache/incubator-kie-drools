/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.incubation.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An extended DataContext includes a <code>meta</code> section
 * and a <code>data</code> section.
 * <p>
 * Meta contains meta-data about the data context.
 * <p>
 * Converting an extended context is equivalent to converting
 * the Data section, unless the type is ExtendedDataContext; e.g.:
 *
 * <code><pre>
 *     ExtendedDataContext edc = ...;
 *     var res = edc.as(MapDataContext.class);
 *     // is equivalent to
 *     var res = edc.data().as(MapDataContext.class);
 *     // however:
 *     var res = edc.as(ExtendedDataContext.class);
 *     // res .equals( mdc )
 * </pre></code>
 *
 *
 */
public final class ExtendedDataContext implements DataContext {
    public static ExtendedDataContext of(MetaDataContext meta, DataContext data) {
        return new ExtendedDataContext(meta, data);
    }

    public static ExtendedDataContext ofData(DataContext data) {
        return new ExtendedDataContext(EmptyMetaDataContext.Instance, data);
    }

    private MetaDataContext meta = EmptyMetaDataContext.Instance;
    private DataContext data = EmptyDataContext.Instance;

    ExtendedDataContext() {
    }

    ExtendedDataContext(MetaDataContext meta, DataContext data) {
        this.meta = meta;
        this.data = data;
    }

    void setMeta(MetaDataContext meta) {
        this.meta = meta;
    }

    void setData(DataContext data) {
        this.data = data;
    }

    @JsonProperty("meta")
    public MetaDataContext meta() {
        return meta;
    }

    @JsonProperty("data")
    public DataContext data() {
        return data;
    }

    public <T extends DataContext> T as(Class<T> type) {
        if (type == ExtendedDataContext.class || type == DataContext.class) {
            return (T) this;
        } else {
            return this.data().as(type);
        }
    }
}
