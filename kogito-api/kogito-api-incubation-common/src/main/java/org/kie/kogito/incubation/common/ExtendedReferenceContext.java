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

/**
 * An extended ReferenceContext includes a <code>meta</code> section
 * and a <code>data</code> section.
 * <p>
 * Meta contains meta-data about the data context.
 * <p>
 * A ReferenceContext cannot be converted into another type of context.
 */
public final class ExtendedReferenceContext implements ReferenceContext {
    public static ExtendedReferenceContext of(MetaDataContext meta, ReferenceContext data) {
        return new ExtendedReferenceContext(meta, data);
    }

    public static ExtendedReferenceContext ofData(ReferenceContext data) {
        return new ExtendedReferenceContext(EmptyMetaDataContext.Instance, data);
    }

    private MetaDataContext meta = EmptyMetaDataContext.Instance;
    private ReferenceContext data = EmptyDataContext.Instance;

    ExtendedReferenceContext() {
    }

    ExtendedReferenceContext(MetaDataContext meta, ReferenceContext data) {
        this.meta = meta;
        this.data = data;
    }

    void setMeta(MetaDataContext meta) {
        this.meta = meta;
    }

    void setData(DataContext data) {
        this.data = data;
    }

    public MetaDataContext meta() {
        return meta;
    }

    public ReferenceContext data() {
        return data;
    }

}
