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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formbuilder.client.bus.ui.FormItemAddedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormItemRemovedEvent;
import org.jbpm.formbuilder.client.form.FBForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class TreePresenterTest extends TestCase {

    private CommonGlobals cg = CommonGlobals.getInstance();
    private EventBus bus;
    private TreeView view;
    
    @Before
    @Override
    protected void setUp() throws Exception {
        bus = new SimpleEventBus();
        cg.registerEventBus(bus);
        view = EasyMock.createMock(TreeView.class);
    }
    
    @After
    @Override
    protected void tearDown() throws Exception {
        bus = null;
        cg.registerEventBus(null);
        view = null;
    }
    
    @Test
    public void testTreeStartUp() throws Exception {
        EasyMock.replay(view);
        new TreePresenter(view);
        EasyMock.verify(view);
    }

    @Test
    public void testFormItemRemoved() throws Exception {
        FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        view.removeFormItem(EasyMock.same(formItem));
        EasyMock.replay(view, formItem);
        new TreePresenter(view);
        bus.fireEvent(new FormItemRemovedEvent(formItem));
        EasyMock.verify(view, formItem);
    }
    
    @Test
    public void testFormItemAddedOnRoot() throws Exception {
        FBForm parentItem = EasyMock.createMock(FBForm.class);
        FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        view.addFormItem(EasyMock.same(formItem), EasyMock.same(parentItem));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view, parentItem, formItem);
        new TreePresenter(view);
        bus.fireEvent(new FormItemAddedEvent(formItem, parentItem));
        EasyMock.verify(view, parentItem, formItem);
    }
    
    @Test
    public void testFormItemAddedOnLeaf() throws Exception {
        FBForm rootItem = EasyMock.createMock(FBForm.class);
        LayoutFormItem parentItem = EasyMock.createMock(LayoutFormItem.class);
        FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        //EasyMock.expect(formItem.getParent()).andReturn(parentItem).once();
        List<FBFormItem> items = new ArrayList<FBFormItem>();
        items.add(formItem);
        EasyMock.expect(parentItem.getItems()).andReturn(items).atLeastOnce();
        view.addFormItem(EasyMock.same(parentItem), EasyMock.same(rootItem));
        EasyMock.expectLastCall().once();
        view.addFormItem(EasyMock.same(formItem), EasyMock.same(parentItem));
        EasyMock.expectLastCall().once();
        view.addFormItem(EasyMock.same(formItem), EasyMock.isNull(FBCompositeItem.class));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view, rootItem, parentItem, formItem);
        new TreePresenter(view);
        bus.fireEvent(new FormItemAddedEvent(parentItem, rootItem));
        bus.fireEvent(new FormItemAddedEvent(formItem, null));
        EasyMock.verify(view, rootItem, parentItem, formItem);
    }
    
}
