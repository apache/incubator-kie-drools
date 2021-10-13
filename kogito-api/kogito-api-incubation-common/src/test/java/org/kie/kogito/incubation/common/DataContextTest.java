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
import java.util.Objects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataContextTest {
    public static class Address {
        String street;

        @Override
        public boolean equals(Object o) {
            return (o instanceof Address)
                    && Objects.equals(((Address) o).street, street);
        }

    }

    public static class User implements DataContext, DefaultCastable {
        String firstName;
        String lastName;
        Address addr;

        @Override
        public boolean equals(Object o) {
            if (o instanceof User) {
                User user = (User) o;
                return Objects.equals(firstName, user.firstName)
                        && Objects.equals(lastName, user.lastName)
                        && Objects.equals(addr, user.addr);
            } else
                return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName, addr);
        }
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
        assertNotNull(user);
        assertEquals(paul, user);
    }

    @Test
    public void testFastAsUsingCast() {
        DataContext ctx = new MapDataContext(Map.of("full name", "John Doe", "age", 47));

        MapDataContext converted = ctx.as(MapDataContext.class);
        assertThat(converted).isSameAs(ctx);
    }
}
