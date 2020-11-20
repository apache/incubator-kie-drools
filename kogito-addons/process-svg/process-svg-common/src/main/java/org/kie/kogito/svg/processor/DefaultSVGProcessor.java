/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.svg.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.svg.model.NodeSummary;
import org.kie.kogito.svg.model.RenderType;
import org.kie.kogito.svg.model.SetSubProcessLinkTransformation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultSVGProcessor extends AbstractSVGProcessor {

    public DefaultSVGProcessor(Document svgDocument) {
        super(svgDocument, true);
    }

    @Override
    public void defaultCompletedTransformation(String nodeId, String completedNodeColor, String completeBorderColor) {
        transform(summary ->
            Optional.ofNullable(summary.getNode(nodeId)).ifPresent(node ->
                Optional.ofNullable(node.getBackground()).ifPresent(background -> {
                    background.setAttribute("fill", completedNodeColor);
                    setNodeBorderColor(node.getRenderType(), node.getBorder(), completeBorderColor);
                })
            )
        );
    }

    @Override
    public void defaultActiveTransformation(String nodeId, String activeNodeBorderColor) {
        transform(summary ->
            Optional.ofNullable(summary.getNode(nodeId)).ifPresent(node ->
                Optional.ofNullable(node.getBorder()).ifPresent(border ->
                    setNodeBorderColor(node.getRenderType(), border, activeNodeBorderColor)
                )
            )
        );
    }

    private void setNodeBorderColor(Optional<RenderType> renderType, Element border, String color) {
        final RenderType render = renderType.orElse(RenderType.STROKE);
        if (render == RenderType.STROKE) {
            border.setAttribute("stroke-width", "2");
            border.setAttribute("stroke", color);
        } else if (render == RenderType.FILL) {
            border.setAttribute("fill", color);
        }
    }

    @Override
    public void defaultSubProcessLinkTransformation(String nodeId, String link) {
        transform(new SetSubProcessLinkTransformation(nodeId, link));
    }

    private void processNode(final Node parent, final String nodeId) {
        final NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                final Node svgIdNode = attributes.getNamedItem("id");
                if (svgIdNode != null) {
                    getNodeSummary(nodeId, (Element) node, svgIdNode);
                    break;
                }
                processNode(node, nodeId);
            }
        }
    }

    private void getNodeSummary(String nodeId, Element node, Node svgIdNode) {
        final String value = svgIdNode.getNodeValue();
        if (Objects.isNull(value)) {
            return;
        }
        Map<String, String> parameters =
                Stream.of(value.substring(value.indexOf("?") + 1))
                        .filter(Objects::nonNull)
                        .map(str -> Collections.list(new StringTokenizer(str, "&")))
                        .flatMap(Collection::stream)
                        .map(String::valueOf)
                        .filter(str -> str.split("=").length == 2)
                        .collect(Collectors.toMap(v -> v.split("=")[0], v -> v.split("=")[1]));

        NodeSummary nodeSummary = summary.getNodesMap().getOrDefault(nodeId, new NodeSummary(nodeId, null, null, null, null, null));
        Element border = Objects.equals(parameters.get("shapeType"), "BORDER") ? node : nodeSummary.getBorder();
        Element background = Objects.equals(parameters.get("shapeType"), "BACKGROUND") ? node : nodeSummary.getBackground();
        RenderType renderType = RenderType.valueOf(Optional.ofNullable(parameters.get("renderType")).orElse(nodeSummary.getRenderType().orElse(RenderType.STROKE).name()));

        summary.addNode(new NodeSummary(nodeId, border, background, null, null, renderType));
    }

    @Override
    public void processNodes(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                Node svgIdNode = attributes.getNamedItem("id");
                if (svgIdNode != null) {
                    Node nodeIdNode = attributes.getNamedItem("bpmn2nodeid");
                    if (nodeIdNode != null) {
                        String nodeId = nodeIdNode.getNodeValue();
                        if (nodeId != null) {
                            //process bpmn2 node to parse the attributes
                            processNode(node, nodeId);
                        }
                    }
                }
            }
            processNodes(node.getChildNodes());
        }
    }
}
