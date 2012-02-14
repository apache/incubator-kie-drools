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
package org.jbpm.formapi.client.form;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Used to represent future position of a droppable item.
 */
public class PhantomPanel extends SimplePanel {

    /**
     * Label for IE quirks mode workaround.
     */
    private static final Label DUMMY_LABEL = new HTML("&nbsp;");
    
    public PhantomPanel(FBCompositeItem container, int x, int y) {
        setStyleName("phantomPanel");
        setWidget(DUMMY_LABEL);
        setSize("100%", "5px");
        container.add(this, x, y);
    }
}
