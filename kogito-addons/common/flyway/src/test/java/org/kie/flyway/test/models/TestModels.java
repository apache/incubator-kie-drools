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

package org.kie.flyway.test.models;

import java.util.Arrays;
import java.util.Collection;

public interface TestModels {

    static final Collection<KieFlywayMigration> EXPECTED_CUSTOMERS_MIGRATIONS = Arrays.asList(
            new KieFlywayMigration("1.0.0", "Create table %s"),
            new KieFlywayMigration("1.0.1", "Alter table %s"),
            new KieFlywayMigration("1.0.2", "Insert book characters %s"),
            new KieFlywayMigration("1.0.5", "Insert game characters %s"));

    static final Collection<Customer> EXPECTED_CUSTOMERS = Arrays.asList(new Customer(1, "Ned", "Stark", "n.stark@winterfell.book"),
            new Customer(2, "Ender", "Wiggin", "ender@endersgame.book"),
            new Customer(3, "Guybrush", "Threepwood", "guybrush@monkeyisland.game"),
            new Customer(4, "Herman", "Toothrot", "toothrot@monkeyisland.game"));

    static final Collection<KieFlywayMigration> EXPECTED_GUITARS_MIGRATIONS = Arrays.asList(
            new KieFlywayMigration("1.0.0", "Create guitars table %s"),
            new KieFlywayMigration("1.0.1", "Alter guitars table %s"),
            new KieFlywayMigration("1.0.2", "Insert fender guitars %s"),
            new KieFlywayMigration("1.0.5", "Insert gibson guitars %s"));

    static final Collection<Guitar> EXPECTED_GUITARS = Arrays.asList(new Guitar(1, "Fender", "Telecaster", 10),
            new Guitar(2, "Fender", "Stratocaster", 9),
            new Guitar(3, "Fender", "Jazzmaster", 7),
            new Guitar(4, "Gibson", "SG", 9),
            new Guitar(5, "Gibson", "Les Paul", 9),
            new Guitar(6, "Gibson", "ES-330", 10));
}
