package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.model.api.ItemDefinition;

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
