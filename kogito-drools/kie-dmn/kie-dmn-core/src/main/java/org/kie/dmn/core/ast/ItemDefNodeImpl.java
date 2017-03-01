/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.model.v1_1.ItemDefinition;

public class ItemDefNodeImpl
        extends DMNBaseNode
        implements ItemDefNode {

    private ItemDefinition itemDef;
    private DMNType        type;

    public ItemDefNodeImpl(ItemDefinition itemDef) {
        this( itemDef, null );
    }

    public ItemDefNodeImpl(ItemDefinition itemDef, DMNType type) {
        super( itemDef );
        this.itemDef = itemDef;
        this.type = type;
    }

    public ItemDefinition getItemDef() {
        return itemDef;
    }

    public void setItemDef(ItemDefinition itemDef) {
        this.itemDef = itemDef;
    }

    public String getId() {
        return itemDef.getId();
    }

    public String getName() {
        return itemDef.getName();
    }

    public boolean isCollection() {
        return itemDef.isIsCollection();
    }

    @Override
    public DMNType getType() {
        return type;
    }

    public void setType(DMNType type) {
        this.type = type;
    }
}
