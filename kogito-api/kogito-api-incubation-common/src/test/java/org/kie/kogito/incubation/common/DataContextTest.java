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

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.common.objectmapper.InternalObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class DataContextTest {
    @Test
    public void fromMap() {
        MapDataContext ctx = MapDataContext.create();
        ctx.set("firstName", "Paul");
        ctx.set("lastName", "McCartney");

        User u = ctx.as(User.class);
        assertThat(u.firstName).isEqualTo("Paul");
        assertThat(u.lastName).isEqualTo("McCartney");
    }

    @Test
    public void toMap() {
        User u = new User();
        u.firstName = "Paul";
        u.lastName = "McCartney";

        MapLikeDataContext ctx = u.as(MapLikeDataContext.class);
        assertThat(ctx.get("firstName")).isEqualTo("Paul");
        assertThat(ctx.get("lastName")).isEqualTo("McCartney");

        MapLikeDataContext ctx2 = u.as(MapDataContext.class);
        assertThat(ctx2.get("firstName")).isEqualTo("Paul");
        assertThat(ctx2.get("lastName")).isEqualTo("McCartney");
    }

    @Test
    public void nestedValue() {
        User u = new User();
        u.firstName = "Paul";
        u.lastName = "McCartney";
        u.addr = new Address();
        u.addr.street = "Abbey Rd.";

        MapDataContext ctx = u.as(MapDataContext.class);
        assertThat(ctx.get("addr").getClass()).isNotEqualTo(Address.class);
        Address addr = InternalObjectMapper.objectMapper().convertValue(ctx.get("addr"), Address.class);
        assertThat(addr.street).isEqualTo("Abbey Rd.");
    }

    @Test
    public void getTypedValueFromMap() {
        User u = new User();
        u.firstName = "Paul";
        u.lastName = "McCartney";
        u.addr = new Address();
        u.addr.street = "Abbey Rd.";

        MapDataContext mdc = MapDataContext.of(Map.of("Paul", u));
        User paul = (User) mdc.get("Paul");
        User user = mdc.get("Paul", User.class);
        assertThat(user).isNotNull()
                .isEqualTo(paul);
    }

    @Test
    public void testFastAsUsingCast() {
        DataContext ctx = new MapDataContext(Map.of("full name", "John Doe", "age", 47));

        MapDataContext converted = ctx.as(MapDataContext.class);
        assertThat(converted).isSameAs(ctx);
    }

    @Test
    public void shouldAllowEmptyMetaDataContext() throws JsonProcessingException {
        MetaDataContext mdc = EmptyMetaDataContext.Instance;
        assertThat(new ObjectMapper().writeValueAsString(mdc)).isEqualTo("{}");
    }
}
