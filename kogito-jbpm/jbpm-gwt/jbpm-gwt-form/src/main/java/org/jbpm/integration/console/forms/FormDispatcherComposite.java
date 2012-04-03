/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.integration.console.forms;

import java.net.URL;

import javax.activation.DataHandler;

import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;

public class FormDispatcherComposite implements FormDispatcherPlugin {

    private FormDispatcherPlugin taskDispatcher;
    private FormDispatcherPlugin processDispatcher;

    public FormDispatcherComposite() {
        this.taskDispatcher = new TaskFormDispatcher();
        this.processDispatcher = new ProcessFormDispatcher();
    }

    public URL getDispatchUrl(FormAuthorityRef ref) {
        switch (ref.getType()) {
        case TASK:
            return taskDispatcher.getDispatchUrl(ref);
        case PROCESS:
            return processDispatcher.getDispatchUrl(ref);
        default:
            throw new IllegalArgumentException("Unknown authority type:" + ref.getType());
        }
    }

    public DataHandler provideForm(FormAuthorityRef ref) {
        switch (ref.getType()) {
        case TASK:
            return taskDispatcher.provideForm(ref);
        case PROCESS:
            return processDispatcher.provideForm(ref);
        default:
            throw new IllegalArgumentException("Unknown authority type:" + ref.getType());
        }
    }

}
