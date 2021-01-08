/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.graph.graphviz;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import guru.nidi.graphviz.attribute.ForNodeLink;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

public class GraphImageGenerator {

    private static final String DEFAULT_OUTPUT_DIR = "target" + File.separator + "graph-output";

    private String graphName;
    private int width = 0; // when 0, auto-sized
    private int height = 0; // when 0, auto-sized
    private String outputDir = DEFAULT_OUTPUT_DIR;

    public GraphImageGenerator(String graphName) {
        this.graphName = graphName;
    }

    public GraphImageGenerator(String graphName, int width, int height) {
        this.graphName = graphName;
        this.width = width;
        this.height = height;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void generatePng(Graph g) {
        guru.nidi.graphviz.model.Graph graph = graph(graphName).directed()
                                      .graphAttr().with(Rank.dir(LEFT_TO_RIGHT));

        List<Node> nodeList = g.getNodeMap().values().stream().collect(Collectors.toList());
        for (Node n : nodeList) {
            guru.nidi.graphviz.model.Node node = node(n.getRuleName());
            for (Link l : n.getOutgoingLinks()) {
                Style<ForNodeLink> style;
                if (l.getType() == Link.Type.POSITIVE) {
                    style = Style.SOLID;
                } else if (l.getType() == Link.Type.NEGATIVE) {
                    style = Style.DASHED;
                } else {
                    // UNKNOWN
                    style = Style.DOTTED;
                }
                node = node.link(to(node(l.getTarget().getRuleName())).with(style));
            }
            graph = graph.with(node);
        }

        try {
            String filePath = outputDir + File.separator + graphName + ".png";
            Graphviz.fromGraph(graph).width(width).height(height).render(Format.PNG).toFile(new File(filePath));
            System.out.println("--- Graph image is generated to " + filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
