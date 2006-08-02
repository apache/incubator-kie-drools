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
package org.drools.brms.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Grid;

/**
 * Demonstrates {@link com.google.gwt.user.client.ui.Table}.
 */
public class Search extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo(
      "Search",
      "Find the rules you want to edit and manage.") {
      public JBRMSFeature createInstance() {
        return new Search();
      }

	public Image getImage() {
 
		return new Image("images/drools.gif");
	}
    };
  }

  private Grid inner = new Grid(10, 5);
  private FlexTable outer = new FlexTable();

  public Search() {
    outer.setWidget(0, 0, new Image("rembrandt/LaMarcheNocturne.jpg"));
    outer.getFlexCellFormatter().setColSpan(0, 0, 2);
    outer.getFlexCellFormatter().setHorizontalAlignment(0, 0,
      HasHorizontalAlignment.ALIGN_CENTER);

    outer.setHTML(1, 0, "Look to the right...<br>"
      + "That's a nested table component ->");
    outer.setWidget(1, 1, inner);
    ((FlexTable.FlexCellFormatter) outer.getCellFormatter())
      .setColSpan(1, 1, 2);

    for (int i = 0; i < 10; ++i) {
      for (int j = 0; j < 5; ++j)
        inner.setText(i, j, "" + i + "," + j);
    }

    inner.setWidth("100%");
    outer.setWidth("100%");

    inner.setBorderWidth(1);
    outer.setBorderWidth(1);

    setWidget(outer);
  }

  public void onShow() {
  }
}
