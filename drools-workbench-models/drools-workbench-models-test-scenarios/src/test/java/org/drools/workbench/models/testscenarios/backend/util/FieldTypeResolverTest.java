/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.testscenarios.backend.util;

import java.time.LocalDate;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldTypeResolverTest {

    @Test
    public void isDate() {
        final Person person = new Person();
        assertTrue(FieldTypeResolver.isDate("bday", person));
        assertFalse(FieldTypeResolver.isDate("bdayLocalDate", person));
        assertFalse(FieldTypeResolver.isDate("name", person));
    }

    @Test
    public void isLocalDate() {
        final Person person = new Person();
        assertFalse(FieldTypeResolver.isLocalDate("bday", person));
        assertTrue(FieldTypeResolver.isLocalDate("bdayLocalDate", person));
        assertFalse(FieldTypeResolver.isLocalDate("name", person));
    }

    static class Person {

        String name;
        Date bday;
        LocalDate bdayLocalDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBday() {
            return bday;
        }

        public void setBday(Date bday) {
            this.bday = bday;
        }

        public LocalDate getBdayLocalDate() {
            return bdayLocalDate;
        }

        public void setBdayLocalDate(LocalDate bdayLocalDate) {
            this.bdayLocalDate = bdayLocalDate;
        }
    }
}