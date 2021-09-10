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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DataContextTest {
    public static class Address {
        String street;
    }

    public static class User implements DataContext, DefaultCastable {
        String firstName;
        String lastName;
        Address addr;
    }

    @Test
    public void fromMap() {
        MapDataContext ctx = MapDataContext.create();
        ctx.set("firstName", "Paul");
        ctx.set("lastName", "McCartney");

        User u = ctx.as(User.class);
        assertEquals("Paul", u.firstName);
        assertEquals("McCartney", u.lastName);
    }

    @Test
    public void toMap() {
        User u = new User();
        u.firstName = "Paul";
        u.lastName = "McCartney";

        MapLikeDataContext ctx = u.as(MapLikeDataContext.class);
        assertEquals("Paul", ctx.get("firstName"));
        assertEquals("McCartney", ctx.get("lastName"));

        MapLikeDataContext ctx2 = u.as(MapDataContext.class);
        assertEquals("Paul", ctx2.get("firstName"));
        assertEquals("McCartney", ctx2.get("lastName"));
    }

    @Test
    public void nestedValue() {
        User u = new User();
        u.firstName = "Paul";
        u.lastName = "McCartney";
        u.addr = new Address();
        u.addr.street = "Abbey Rd.";

        MapDataContext ctx = u.as(MapDataContext.class);
        assertNotEquals(Address.class, ctx.get("addr").getClass());
        Address addr = InternalObjectMapper.convertValue(ctx.get("addr"), Address.class);
        assertEquals("Abbey Rd.", addr.street);
    }
}
