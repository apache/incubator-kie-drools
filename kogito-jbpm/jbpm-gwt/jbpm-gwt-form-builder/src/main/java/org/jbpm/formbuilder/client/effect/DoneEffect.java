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
package org.jbpm.formbuilder.client.effect;

import org.jbpm.formapi.client.bus.FormItemSelectionEvent;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formbuilder.client.FormBuilderGlobals;

import com.gwtent.reflection.client.Reflectable;

/**
 * Simple {@link FBFormEffect} implementation, simply deselects 
 * the effect's item
 */
@Reflectable
public class DoneEffect extends FBFormEffect {

    public DoneEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().DoneEffectLabel(), false);
    }
    
    @Override
    protected void createStyles() {
        getItem().fireSelectionEvent(new FormItemSelectionEvent(getItem(), false));
    }
}
