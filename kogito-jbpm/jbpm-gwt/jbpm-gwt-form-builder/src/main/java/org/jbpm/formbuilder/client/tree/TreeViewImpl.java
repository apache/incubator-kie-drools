/*
 * Copyright 2011 JBoss Inc 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formbuilder.client.tree;

import java.util.List;

import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Tree view. Holds a tree structure representing layout objects
 */
public class TreeViewImpl extends ScrollPanel implements TreeView {

    private Tree tree = new Tree();
    
    public TreeViewImpl() {
        tree.setSize("100%", "100%");
        tree.addItem(new TreeElement());
        setWidget(tree);
        
        new TreePresenter(this);
    }
    
    public void clearItems() {
        tree.clear();
        tree.addItem(new TreeElement());
    }
    
    @Override
    public void addFormItem(FBFormItem item, FBCompositeItem parent) {
        TreeItem treeBranch = tree.getItem(0);
        if (parent != null && parent instanceof FBFormItem) {
            TreeItem treeBranch2 = getItem(treeBranch, (FBFormItem) parent);
            if (treeBranch2 != null) {
                treeBranch = treeBranch2;
            }
        } 
        if (parent != null && item != null) {
            treeBranch.addItem(new TreeElement(item));
            if (item instanceof FBCompositeItem) {
                FBCompositeItem compItem = (FBCompositeItem) item;
                List<FBFormItem> subItems = compItem.getItems();
                if (subItems != null) {
                    for (FBFormItem subItem : subItems) {
                        if (subItem != null) {
                            addFormItem(subItem, compItem);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void removeFormItem(FBFormItem item) {
        TreeItem treeBranch = tree.getItem(0);
        TreeItem treeBranch2 = getItem(treeBranch, item);
        if (treeBranch2 != null) {
            treeBranch = treeBranch2;
        }
        if (treeBranch.getParentItem() != null) {
            treeBranch.getParentItem().removeItem(treeBranch);
        }
    }

    private TreeItem getItem(TreeItem treeBranch, FBFormItem item) {
        TreeItem retval = null;
        for (int index = 0; retval == null && index < treeBranch.getChildCount(); index++) {
            TreeItem treeItem = treeBranch.getChild(index);
            TreeElement elem = (TreeElement) treeItem.getWidget();
            if (elem.represents(item)) {
                retval = treeItem;
                break;
            } else {
                retval = getItem(treeItem, item);
            }
        }
        return retval;
    }
}
