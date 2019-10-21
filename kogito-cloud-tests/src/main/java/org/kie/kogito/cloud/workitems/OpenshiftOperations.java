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
package org.kie.kogito.cloud.workitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShiftBinary;
import cz.xtf.core.openshift.OpenShifts;
import cz.xtf.core.waiting.SimpleWaiter;
import io.fabric8.openshift.api.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenshiftOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenshiftOperations.class);

    private OpenShift openShift;
    private OpenShiftBinary masterBinary;

    public void init(String projectName) {
        openShift = OpenShifts.master();
        if (openShift.getProject(projectName) == null) {
            LOGGER.info("Create project {}", projectName);
            openShift.createProjectRequest(projectName);
            waitFor(() -> openShift.getProject(projectName) != null, "Waiting for project to be created.", 5);
        }
        openShift = OpenShifts.master(projectName);
        openShift.addRoleToServiceAccount("view", "default");
        masterBinary = OpenShifts.masterBinary(openShift.getNamespace());
    }

    public void delete() {
        openShift.deleteProject();
    }

    public OpenShift getOpenshift() {
        return openShift;
    }

    public String startS2IBinaryBuild(String builderImageTag, String buildName, String projectDir) {
        List<String> command = new ArrayList<>(Arrays.asList("new-build", builderImageTag, "--binary=true", "--name=" + buildName));
        TestConfig.getMavenMirrorUrl().ifPresent(url -> {
            command.add("-e");
            command.add("MAVEN_MIRROR_URL=" + url);
        });

        launchBinaryCommand(command);
        launchBinaryCommand(Arrays.asList("start-build", buildName, "--from-dir", projectDir));
        return buildName;
    }

    public void startRuntimeBuild(String builderImageTag, String buildName, String sourceImageName) {
        launchBinaryCommand(Arrays.asList("new-build", "--name=" + buildName,
                                          "--source-image=" + sourceImageName,
                                          "--source-image-path=/home/kogito/bin:.",
                                          "--docker-image=" + builderImageTag));
    }

    public void startApp(String imageName, Map<String, String> envVariables, Map<String, String> serviceLabels) {
        List<String> command = new ArrayList<>(Arrays.asList("new-app", imageName + ":latest"));
        if (!envVariables.isEmpty()) {
            command.add("-e");
            command.add(getParameterKeyValueString(envVariables));
        }
        if (!serviceLabels.isEmpty()) {
            command.add("-l");
            command.add(getParameterKeyValueString(serviceLabels));
        }
        launchBinaryCommand(command);
    }

    public void waitForBuildCompleted(String buildName, long timeoutInMinutes) {
        // Small bug with openshift.waiters().hasBuildCompleted(buildName) in case build has not started yet ...
        openShift.waiters().isLatestBuildPresent(buildName).timeout(TimeUnit.MINUTES, 5L).waitFor();
        openShift.waiters().hasBuildCompleted(buildName).timeout(TimeUnit.MINUTES, timeoutInMinutes).waitFor();
    }

    public void waitFor(BooleanSupplier supplier, String reason, long timeoutInMinutes) {
        new SimpleWaiter(supplier).reason(reason).timeout(TimeUnit.MINUTES, timeoutInMinutes).waitFor();
    }

    public void launchBinaryCommand(List<String> command) {
        LOGGER.debug("{}", command);
        masterBinary.execute(command.toArray(new String[0]));
    }

    public void waitForPod(String deploymentName, int nbInstances, long timeoutInMinutes) {
        openShift.waiters().areExactlyNPodsRunning(nbInstances, deploymentName).timeout(TimeUnit.MINUTES, timeoutInMinutes).waitFor();
    }

    public void waitForService(String serviceName, long timeoutInMinutes) {
        waitFor(() -> openShift.getService(serviceName) != null, "Waiting for service " + serviceName, timeoutInMinutes);
    }

    public void exposeService(String serviceName) {
        masterBinary.execute("expose", "svc/" + serviceName);
    }

    public String getHttpRoute(String serviceName) {
        List<Route> routes = openShift.getRoutes();
        Optional<Route> route = routes.stream()
                .filter(n -> n.getSpec().getTls() == null)
                .filter(n -> n.getSpec().getTo().getName().equals(serviceName))
                .findAny();
        String routeHost = null;
        if (route.isPresent()) {
            routeHost = route.get().getSpec().getHost();
        } else {
            String routeNames = routes.stream()
                    .map(n -> n.getMetadata().getName())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("HTTP route leading to service " + serviceName + " not found. Available routes " + routeNames);
        }
        return "http://" + routeHost + ":80";
    }

    private static String getParameterKeyValueString(Map<String, String> parameters) {
        return parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
    }
}
