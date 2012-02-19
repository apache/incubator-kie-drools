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
package org.jbpm.formapi.client;

import org.jbpm.formapi.client.form.FBFormItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public abstract class AbstractFormItemCommand implements Command {

    private FBFormItem selectedItem;
    private MenuItem menuItem;
    
    
    public AbstractFormItemCommand append(FBFormItem item) {
        setSelectedItem(item);
        return this;
    }
    
    public FBFormItem getSelectedItem() {
        return selectedItem;
    }
    
    protected void setSelectedItem(FBFormItem item) {
        this.selectedItem = item;
        enable(menuItem);
    }
    
    public void enable() {
        enable(this.menuItem);
    }
    
    protected abstract void enable(MenuItem menuItem);

    public void setItem(MenuItem item) {
        this.menuItem = item;
    }
}
