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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.java.nio.file.Path;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 *
 * @author esteban
 */
public class TriggerTestsWorkItemHandler implements WorkItemHandler {

    public final static String WIP_INPUT_RELEASE = "in_release_path";
    public final static String WIP_INPUT_TEST = "in_test_dir";
    public final static String WIP_INPUT_MIN_CONTENT_LENGTH = "in_min_lenght";
    public final static String WIP_INPUT_MAX_CONTENT_LENGTH = "in_max_lenght";
    public final static String WIP_OUTPUT_SUCCESSFUL = "out_test_successful";
    public final static String WIP_OUTPUT_REPORT = "out_test_report";
    private static final int DEFAULT_MIN_CONTENT_LENGTH = 10;
    private static final int DEFAULT_MAX_CONTENT_LENGTH = 50;
    @Inject
    private FileService fs;

    @Override
    public void executeWorkItem(final WorkItem workItem, final WorkItemManager manager) {

        Boolean success = true;
        Boolean sleep = false;
        StringBuilder report = null;


        //Read release path
        String releasePath = (String) workItem.getParameter(WIP_INPUT_RELEASE);

        //Read test dir
        String testDir = (String) workItem.getParameter(WIP_INPUT_TEST);


        int minLength = workItem.getParameter(WIP_INPUT_MIN_CONTENT_LENGTH) != null ? Integer.parseInt(workItem.getParameter(WIP_INPUT_MIN_CONTENT_LENGTH).toString()) : DEFAULT_MIN_CONTENT_LENGTH;

        int maxLength = workItem.getParameter(WIP_INPUT_MAX_CONTENT_LENGTH) != null ? Integer.parseInt(workItem.getParameter(WIP_INPUT_MAX_CONTENT_LENGTH).toString()) : DEFAULT_MAX_CONTENT_LENGTH;

        //check mandatory parameters
        if (releasePath == null || releasePath.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_RELEASE + "' parameter is mandatory!");
        }

        if (testDir == null || testDir.isEmpty()) {
            throw new IllegalArgumentException("'" + WIP_INPUT_TEST + "' parameter is mandatory!");
        }

        String testPath = releasePath + "/" + testDir;

        if (!fs.exists(fs.getPath(testPath))) {
            throw new IllegalArgumentException(testPath + " doesn't exist!");
        }


        //'test' files inside test dir
        report = new StringBuilder("");

        try {
            Iterable<Path> txtFiles = fs.loadFilesByType(fs.getPath(testPath), "txt");
            if (txtFiles == null || !txtFiles.iterator().hasNext()) {
                report.append("EE ").append(testPath).append(" doesn't contain any .txt file!\n");
                success = false;
            } else {
                Iterator<Path> iterator = txtFiles.iterator();
                while (iterator.hasNext()) {
                    Path path = iterator.next();

                    String content = new String(fs.loadFile(path));

                    if (content == null || content.isEmpty()) {
                        report.append("EE ").append(path).append(" is empty!\n");
                        success = false;
                        continue;
                    }

                    if (content.length() < minLength) {
                        report.append("EE ").append(path).append(" -> Content is shorter than ").append(minLength).append(" -> '").append(content).append("'");
                        success = false;
                    }
                    if (content.length() > maxLength) {
                        report.append("WW ").append(path).append(" -> Took too much time! \n");
                        sleep = true;
                    } else {
                        report.append("II ").append(path).append(" -> OK\n");
                    }

                }
            }
        } catch (FileException ex) {
            throw new RuntimeException(ex);
        }
        final Map<String, Object> results = new HashMap<String, Object>();
        results.put(WIP_OUTPUT_SUCCESSFUL, (success + "").toLowerCase());
        results.put(WIP_OUTPUT_REPORT, report.toString());

        if (sleep) {
            new Thread(new Runnable() {

              @Override
              public void run() {
                try {
                    for (int i = 0; i < 11; i++) {
                        System.out.println(">>> Running Tests ... ");
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(TriggerTestsWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                manager.completeWorkItem(workItem.getId(), results);
                }
            }).start();

        } else {
            System.out.println(">>> Running Tests ... ");


            manager.completeWorkItem(workItem.getId(), results);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
}
