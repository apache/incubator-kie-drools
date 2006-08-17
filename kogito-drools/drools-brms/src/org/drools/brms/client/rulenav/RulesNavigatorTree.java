/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.brms.client.rulenav;

import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

/**
 * This is a rule/resource navigator that uses the server side categories to 
 * navigate the repository.
 * Uses the the {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class RulesNavigatorTree implements TreeListener {


  private Tree navTreeWidget = new Tree();
  private RepositoryServiceAsync service = RepositoryServiceFactory.getService();
  private TreeItem lastItemChanged = null;
  
  public void setTreeSize(String width) {
	  navTreeWidget.setWidth(width);
  }   
  
  public Tree getTree() {
	  return navTreeWidget;
  }
  
  public RulesNavigatorTree() {

    service.loadChildCategories( "", new AsyncCallback() {

        public void onFailure(Throwable caught) {
            //TODO: work out how to handle it.
        }

        public void onSuccess(Object result) {
            String[] categories = (String[]) result;
            for ( int i = 0; i < categories.length; i++ ) {
                navTreeWidget.addItem( categories[i] ).addItem( new PendingItem() );
            }            
            
        }
        
    });  
      
    navTreeWidget.addTreeListener(this);
    
  }

  public void onShow() {
  }

  public void onTreeItemSelected(TreeItem item) {
      System.out.println("TODO: call rule list view");
  }
  

  public void onTreeItemStateChanged(TreeItem item) {
    if (notShowing( item )) return;
    if (item == lastItemChanged) return;
    lastItemChanged = item;
    final TreeItem root = item;
    
    String categoryPath = item.getText();
    //walk back up to build a tree
    TreeItem parent = item.getParentItem();
    while (parent != null) {
        categoryPath = parent.getText() + "/" + categoryPath;
        parent = parent.getParentItem();
    }
    
    service.loadChildCategories( categoryPath, new AsyncCallback() {

        public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub            
        }

        public void onSuccess(Object result) {
            TreeItem child = root.getChild( 0 );
            if (child instanceof PendingItem) {
                root.removeItem(child);
            }
            String[] list = (String[]) result;
            for ( int i = 0; i < list.length; i++ ) {
                root.addItem( list[i] ).addItem( new PendingItem() );
            }
        }
        
    });
    
    
  }

private boolean notShowing(TreeItem item) {
    return !item.getState();
}

//  private void createItem(Proto proto) {
//    proto.item = new TreeItem(proto.text);
//    proto.item.setUserObject(proto);
//    //if (proto.children != null)
//    proto.item.addItem(new PendingItem());
//  }
  
  private static class PendingItem extends TreeItem {
      public PendingItem() {
        super("Please wait...");
      }
   }
  
}
