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
package org.jbpm.formapi.common.panels;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarPanel extends HorizontalPanel {
    
    private final Widget icon;
    private final Widget text;
    
    private int iconDefaultWidth = 0;
    
    public CalendarPanel(Widget text, Widget icon) {
        super();
        this.icon = icon;
        this.text = text;
        add(text);
        add(icon);
    }

    @Override
    protected void onLoad() {
        iconDefaultWidth = icon.getOffsetWidth() + 5 /* a small margin */;
    }
    
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (iconDefaultWidth > 0) {
            this.text.setWidth(""+ (getOffsetWidth() - iconDefaultWidth) + "px");
        }
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        this.text.setHeight(height);
    }
}