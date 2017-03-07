/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.games.adventures.model;

public class Item extends Thing {

    public Item(String name) {
        this( name, true);
    }

    public Item(String name, boolean portable) {
        super( name, portable );
    }

    public Item(long id, String name, boolean portable) {
        super(name, portable );
    }

    @Override
    public String toString() {
        return "Item{id=" + getId() +", name=" + getName() + "} ";
    }
}
