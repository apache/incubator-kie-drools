/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jbpm.shared.services.api.FileService;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

/**
 *
 */
public class MoveFileWorkItemHandler implements WorkItemHandler {

    public final static String WIP_INPUT_RELEASE = "in_release_path";
    public final static String WIP_INPUT_SOURCE = "in_source_dir";
    public final static String WIP_INPUT_TARGET = "in_target_dir";
    public final static String WIP_INPUT_FILES = "in_files";
    public final static String WIP_OUTPUT_ERRORS = "out_errors";
    @Inject
    private FileService fs;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Debug only
        System.out.println("############ Work Item Parameters");
        for (String k : workItem.getParameters().keySet()) {
            System.out.println("Key = " + k + " - value = " + workItem.getParameter(k));
        }
        System.out.println("#################################");
        
        Map<String, String> errors = new LinkedHashMap<String, String>();

        //Read release path
        String release = (String) workItem.getParameter(WIP_INPUT_RELEASE);

        //Read source dir
        String source = (String) workItem.getParameter(WIP_INPUT_SOURCE);

        //Read file list
        String filesNames = (String) workItem.getParameter(WIP_INPUT_FILES);

        //Read destination dir
        String destination = (String) workItem.getParameter(WIP_INPUT_TARGET);


        //check mandatory parameters
        if (filesNames == null || filesNames.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_FILES + "' parameter is mandatory!");
        }

        if (destination == null || destination.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_TARGET + "' parameter is mandatory!");
        }

        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_SOURCE + "' parameter is mandatory!");
        }

        if (release == null || release.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_RELEASE + "' parameter is mandatory!");
        }

        //separate file names
        List<String> files = Arrays.asList(filesNames.trim().split(","));


        //create full paths
        source = release + "/" + source;
        destination = release + "/" + destination;


        //Destination must exist
        if (fs.exists(destination)) {
            //Move each file to destination. If the file doesn't exist put it 
            //in the errors map
            for (String file : files) {
                String fqn = source + "/" + file.trim();

                try {
                    if (!fs.exists(fqn)) {
                        errors.put(fqn, "Doesn't exist");
                        continue;
                    }

                    fs.move(fqn, destination + "/" + file.trim());
                } catch (Exception e) {
                    errors.put(fqn, e.getMessage());
                }
            }
        } else {
            errors.put("destination", "Doesn't exist");
        }


        //convert errors to String
        StringBuilder errorsString = new StringBuilder("");
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            if (errorsString.length() != 0) {
                errorsString.append(",");
            }
            errorsString.append("[").append(entry.getKey()).append("] -> ").append(entry.getValue());
        }

        //Complete work item
        Map<String, Object> results = new HashMap<String, Object>();
        results.put(WIP_OUTPUT_ERRORS, errorsString.toString());
        System.out.println("############ Work Item Outputs");
        System.out.println(" ERRORS: "+errorsString.toString());
        System.out.println("#################################");
        manager.completeWorkItem(workItem.getId(), results);

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
}
