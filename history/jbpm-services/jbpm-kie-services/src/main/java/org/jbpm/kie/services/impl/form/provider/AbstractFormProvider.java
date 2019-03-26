/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.impl.form.provider;

import org.jbpm.kie.services.impl.FormManagerService;
import org.jbpm.kie.services.impl.form.FormProvider;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalTask;

public abstract class AbstractFormProvider implements FormProvider {

    protected FormManagerService formManagerService;

    public void setFormManagerService(FormManagerService formManagerService){
        this.formManagerService = formManagerService;
    }

    protected String getFormSuffix() {
        return "-taskform" + getFormExtension();
    }

    protected String getTaskFormName(Task task) {
        String formName = ((InternalTask ) task).getFormName();
        if (formName != null && !formName.equals("")) {
            // if the form name has extension it
            if ( formName.endsWith( getFormExtension() ) ) return formName;
            return formName + getFormSuffix();
        } else {
            if (task.getNames() != null && !task.getNames().isEmpty()) {
                formName = task.getNames().get(0).getText();
                if (formName != null) return formName.replace(" ", "") + getFormSuffix();
            }
        }
        return null;
    }

    protected String getFormExtension() {
        return "";
    }
}
