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
package org.jbpm.formbuilder.client.command;

import org.jbpm.formbuilder.client.bus.UndoableEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Extends a Command but adds functionality to be added to a menu item.
 * It allows the command to disable or enable the gwt menu item given
 * its actions. All subclasses should use {@link UndoableEvent} in their
 * main actions.
 */
public interface BaseCommand extends Command {

    void setItem(MenuItem item);
    
    void setEmbeded(String profile);
}
