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

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Item extends Thing {
    @Position(2)
    private boolean fixed;

    public Item(String name) {
        this( name, false);
    }

    public Item(String name, boolean fixed) {
        super( name );
        this.fixed = fixed;
    }

    public Item(long id, String name, boolean fixed) {
        super(name );
        this.fixed = fixed;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }



    @Override
    public String toString() {
        return "Item{id=" + getId() +", name=" + getName() + "} ";
    }
}
