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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedDataContextTest {

    @Test
    public void convertReturnsDataSection() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        MapDataContext meta = MapDataContext.create();
        meta.set("meta-value", "this is not data");

        ExtendedDataContext ctx = ExtendedDataContext.of(meta, data);

        assertThat(ctx.as(MapDataContext.class)).as("Converting an ExtendedContext should be equivalent to converting its data section").isEqualTo(ctx.data().as(MapDataContext.class));

    }

    @Test
    public void convertToExtendedIsNoOp() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        MapDataContext meta = MapDataContext.create();
        meta.set("meta-value", "this is not data");

        ExtendedDataContext ctx = ExtendedDataContext.of(meta, data);

        assertThat(ctx.as(DataContext.class)).isSameAs(ctx);
        assertThat(ctx.as(ExtendedDataContext.class)).isSameAs(ctx);

        assertThat(ctx.as(MapDataContext.class)).as("Converting an ExtendedContext should be equivalent to converting its data section").isEqualTo(ctx.data().as(MapDataContext.class));

    }

    @Test
    public void convertDataContextToExtendedWraps() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        ExtendedDataContext edc = data.as(ExtendedDataContext.class);
        assertThat(edc.data()).isEqualTo(data);
    }

    @Test
    public void convertMapDataContextToExtendedWraps() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        MapDataContext mapData = data.as(MapDataContext.class);
        ExtendedDataContext edc = mapData.as(ExtendedDataContext.class);
        assertThat(edc.data()).isEqualTo(mapData);
    }

    @Test
    public void convertMetaToMap() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        MapDataContext meta = MapDataContext.create();
        meta.set("meta-value", "this is not data");

        ExtendedDataContext ctx = ExtendedDataContext.of(meta, data);

        assertThat(ctx.meta()).isEqualTo(meta);

        MapDataContext fromMeta = MapDataContext.from(ctx.meta());
        assertThat(fromMeta).isEqualTo(meta);

    }

    @Test
    public void convertCustomMetaToMap() {
        User data = new User();
        data.firstName = "Paul";
        data.lastName = "McCartney";
        data.addr = new Address();
        data.addr.street = "Abbey Rd.";

        CustomMeta meta = new CustomMeta();
        meta.value = "this is not data";

        ExtendedDataContext ctx = ExtendedDataContext.of(meta, data);

        assertThat(ctx.meta()).isEqualTo(meta);

        MapDataContext fromMeta = MapDataContext.from(ctx.meta());
        assertThat(fromMeta.get("value")).isEqualTo(meta.value);
    }

}
