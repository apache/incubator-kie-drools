/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.xes.mapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

import org.jbpm.xes.model.ClassifierType;
import org.jbpm.xes.model.ExtensionType;
import org.jbpm.xes.model.GlobalsType;
import org.jbpm.xes.model.LogType;

public class LogTypeMapper implements BiFunction<String, String, LogType> {

    @Override
    public LogType apply(String processName,
                         String processId) {
        final LogType log = new LogType();
        log.setXesFeatures("");
        log.setXesVersion("2.0");
        log.getExtension().addAll(getExtensions());
        log.getClassifier().add(new ClassifierType("By Node Name",
                                                   "concept:name"));
        log.getClassifier().add(new ClassifierType("By Node Id",
                                                   "jbpm:nodeid"));
        log.getClassifier().add(new ClassifierType("By Name and Transition",
                                                   "concept:name lifecycle:transition"));
        log.getClassifier().add(new ClassifierType("By Resource",
                                                   "org:resource"));
        log.getGlobal().addAll(getGlobals());
        log.addStringType("source",
                          "jBPM");
        log.addStringType("lifecycle:model",
                          "standard");
        log.addStringType("concept:name",
                          processName);
        log.addStringType("jbpm:processid",
                          processId);
        return log;
    }

    protected List<ExtensionType> getExtensions() {
        return Arrays.asList(
                new ExtensionType("Lifecycle",
                                  "lifecycle",
                                  "http://www.xes-standard.org/lifecycle.xesext"),
                new ExtensionType("Organizational",
                                  "org",
                                  "http://www.xes-standard.org/org.xesext"),
                new ExtensionType("Time",
                                  "time",
                                  "http://www.xes-standard.org/time.xesext"),
                new ExtensionType("Concept",
                                  "concept",
                                  "http://www.xes-standard.org/concept.xesext")
        );
    }

    protected List<GlobalsType> getGlobals() {
//      Attributes that are always present
        final GlobalsType trace = new GlobalsType("trace");
        trace.addStringType("concept:name",
                            "");
        trace.addDateType("jbpm:start",
                          new Date());
        trace.addStringType("jbpm:status",
                            "");
        trace.addStringType("jbpm:version",
                            "");
        trace.addStringType("jbpm:description",
                            "");
        trace.addIntegerType("jbpm:instanceid",
                             0);
        trace.addStringType("jbpm:correlationkey",
                            "");
        trace.addIntegerType("jbpm:logid",
                             0);

        final GlobalsType event = new GlobalsType("event");
        event.addDateType("time:timestamp",
                          new Date());
        event.addStringType("concept:name",
                            "");
        event.addStringType("org:resource",
                            "");
        event.addStringType("lifecycle:transition",
                            "");
        event.addStringType("jbpm:nodeinstanceid",
                            "");
        event.addStringType("jbpm:nodeid",
                            "");
        event.addStringType("jbpm:nodetype",
                            "");

        event.addIntegerType("jbpm:logid",
                             0);

        return Arrays.asList(
                trace,
                event
        );
    }
}
