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

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

/**
 * Demonstrates the {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class RulesNavigatorTree implements TreeListener {

  private static class PendingItem extends TreeItem {
    public PendingItem() {
      super("Please wait...");
    }
  }

  private static class Proto {
    public Proto[] children;
    public TreeItem item;
    public String text;

    public Proto(String text) {
      this.text = text;
    }

    public Proto(String text, Proto[] children) {
      this(text);
      this.children = children;
    }
  }

  private static Proto[] fProto = new Proto[]{
    new Proto("Pricing", new Proto[]{
      new Proto("Correspondent", new Proto[]{
        new Proto("Closed End seconds"), new Proto("No. 2 - B-Flat Major"),
        new Proto("Government"), new Proto("No. 4 - G Major"),
        new Proto("NGOs"),}),
      new Proto("Wholesale", new Proto[]{}),
      new Proto("Retail", new Proto[]{}),
      new Proto("Symphonies", new Proto[]{
        new Proto("No. 1 - C Major"), new Proto("No. 2 - D Major"),
        new Proto("No. 3 - E-Flat Major"), new Proto("No. 4 - B-Flat Major"),
        new Proto("No. 5 - C Minor"), new Proto("No. 6 - F Major"),
        new Proto("No. 7 - A Major"), new Proto("No. 8 - F Major"),
        new Proto("No. 9 - D Minor"),}),}),
    new Proto("Eligibility", new Proto[]{
      new Proto("Concertos", new Proto[]{
        new Proto("Violin Concerto"), new Proto("Double Concerto - A Minor"),
        new Proto("Piano Concerto No. 1 - D Minor"),
        new Proto("Piano Concerto No. 2 - B-Flat Major"),}),
      new Proto("Quartets", new Proto[]{
        new Proto("Piano Quartet No. 1 - G Minor"),
        new Proto("Piano Quartet No. 2 - A Major"),
        new Proto("Piano Quartet No. 3 - C Minor"),
        new Proto("String Quartet No. 3 - B-Flat Minor"),}),
      new Proto("Sonatas", new Proto[]{
        new Proto("Two Sonatas for Clarinet - F Minor"),
        new Proto("Two Sonatas for Clarinet - E-Flat Major"),}),
      new Proto("Symphonies", new Proto[]{
        new Proto("No. 1 - C Minor"), new Proto("No. 2 - D Minor"),
        new Proto("No. 3 - F Major"), new Proto("No. 4 - E Minor"),}),}),
    new Proto("Rate Adjustments", new Proto[]{new Proto("Concertos", new Proto[]{
      new Proto("Piano Concerto No. 12"), new Proto("Piano Concerto No. 17"),
      new Proto("Clarinet Concerto"), new Proto("Violin Concerto No. 5"),
      new Proto("Violin Concerto No. 4"),}),}),};


  private Tree fTree = new Tree();

  public Tree getTree() {
	  return fTree;
  }
  
  public RulesNavigatorTree() {
    for (int i = 0; i < fProto.length; ++i) {
      createItem(fProto[i]);
      fTree.addItem(fProto[i].item);
    }

    fTree.addTreeListener(this);
    
  }

  public void onShow() {
  }

  public void onTreeItemSelected(TreeItem item) {
  }

  public void onTreeItemStateChanged(TreeItem item) {
    TreeItem child = item.getChild(0);
    if (child instanceof PendingItem) {
      item.removeItem(child);

      Proto proto = (Proto) item.getUserObject();
      for (int i = 0; i < proto.children.length; ++i) {
        createItem(proto.children[i]);
        item.addItem(proto.children[i].item);
      }
    }
  }

  private void createItem(Proto proto) {
    proto.item = new TreeItem(proto.text);
    proto.item.setUserObject(proto);
    if (proto.children != null)
      proto.item.addItem(new PendingItem());
  }
}
