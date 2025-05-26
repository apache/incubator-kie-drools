/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.svg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.drools.util.PathUtils;
import org.kie.kogito.svg.dataindex.DataIndexClient;
import org.kie.kogito.svg.dataindex.NodeInstance;
import org.kie.kogito.svg.processor.SVGProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

public abstract class AbstractProcessSvgService implements ProcessSvgService {
    public static final String DEFAULT_COMPLETED_COLOR = "#C0C0C0";
    public static final String DEFAULT_COMPLETED_BORDER_COLOR = "#030303";
    public static final String DEFAULT_ACTIVE_BORDER_COLOR = "#FF0000";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessSvgService.class);
    public static final String SVG_RELATIVE_PATH = "META-INF/processSVG/";
    protected DataIndexClient dataIndexClient;
    protected Optional<String> svgResourcesPath;
    protected String completedColor;
    protected String completedBorderColor;
    protected String activeBorderColor;

    public AbstractProcessSvgService() {
    }

    public AbstractProcessSvgService(DataIndexClient dataIndexClient, Optional<String> svgResourcesPath, String completedColor, String completedBorderColor, String activeBorderColor) {
        this.dataIndexClient = dataIndexClient;
        this.svgResourcesPath = svgResourcesPath;
        this.completedColor = completedColor;
        this.completedBorderColor = completedBorderColor;
        this.activeBorderColor = activeBorderColor;
    }

    public void setSvgResourcesPath(Optional<String> svgResourcesPath) {
        this.svgResourcesPath = svgResourcesPath;
    }

    @Override
    public Optional<String> getProcessSvg(String processId) {
        if (svgResourcesPath.isPresent()) {
            Path baseDir = Paths.get(svgResourcesPath.get());
            try {
                Path path = PathUtils.getSecuredPath(baseDir, processId + ".svg");
                if (Files.notExists(path) || !Files.isRegularFile(path) || !path.toRealPath().startsWith(baseDir.toRealPath())) {
                    LOGGER.warn("Could not find {}.svg file in folder {}", processId, svgResourcesPath.get());
                    return Optional.empty();
                }
                return Optional.of(new String(Files.readAllBytes(path)));
            } catch (IOException e) {
                throw new ProcessSVGException("Exception trying to read SVG file", e);
            }
        } else {
            return readFileContentFromClassPath(processId + ".svg");
        }
    }

    protected Optional<String> readFileContentFromClassPath(String fileName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(SVG_RELATIVE_PATH + fileName)) {
            if (is == null) {
                return Optional.empty();
            }
            return Optional.of(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new ProcessSVGException("Exception trying to read file from classpath", ex);
        }
    }

    protected Optional<String> annotateExecutedPath(String svg, List<String> completedNodes, List<String> activeNodes) {
        if (svg == null || svg.isEmpty()) {
            return Optional.empty();
        }

        if (completedNodes.isEmpty() && activeNodes.isEmpty()) {
            return Optional.of(svg);
        }

        try (InputStream svgStream = new ByteArrayInputStream(svg.getBytes())) {
            SVGProcessor processor = new SVGImageProcessor(svgStream).getProcessor();
            completedNodes.forEach(nodeId -> processor.defaultCompletedTransformation(nodeId, completedColor, completedBorderColor));
            activeNodes.forEach(nodeId -> processor.defaultActiveTransformation(nodeId, activeBorderColor));
            return Optional.of(processor.getSVG());
        } catch (Exception e) {
            throw new ProcessSVGException("Failed to annotated SVG for process instance", e);
        }
    }

    @Override
    public Optional<String> getProcessInstanceSvg(String processId, String processInstanceId, String authHeader) {
        Optional<String> processSvg = getProcessSvg(processId);
        if (processSvg.isPresent()) {
            List<NodeInstance> nodes = dataIndexClient.getNodeInstancesFromProcessInstance(processInstanceId, authHeader);
            List<String> completedNodes = nodes.stream().filter(NodeInstance::isCompleted).map(NodeInstance::getDefinitionId).collect(toList());
            List<String> activeNodes = nodes.stream().filter(n -> !n.isCompleted()).map(NodeInstance::getDefinitionId).collect(toList());
            return annotateExecutedPath(processSvg.get(), completedNodes, activeNodes);
        } else {
            return Optional.empty();
        }
    }
}
