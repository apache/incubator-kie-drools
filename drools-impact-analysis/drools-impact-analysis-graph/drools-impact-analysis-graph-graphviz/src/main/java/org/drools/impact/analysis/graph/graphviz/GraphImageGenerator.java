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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.ForNodeLink;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

public class GraphImageGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GraphImageGenerator.class);

    private static final String DEFAULT_OUTPUT_DIR = "target" + File.separator + "graph-output";

    private String graphName;
    private int width = 0; // when 0, auto-sized
    private int height = 0; // when 0, auto-sized
    private int totalMemory = 1000000000; // 1GB by default
    private int cmdLineEngineTimeout = 600; // 10 minutes by default
    private String outputDir = DEFAULT_OUTPUT_DIR;

    private Rank.RankDir rankDir = Rank.RankDir.LEFT_TO_RIGHT; // LEFT_TO_RIGHT gives a better view when you have a large number of nodes
    private double sep = 1; // interval between levels

    public GraphImageGenerator(String graphName) {
        this.graphName = graphName;
        initEngines();
    }

    public GraphImageGenerator(String graphName, int width, int height, int cmdLineEngineTimeout) {
        this.graphName = graphName;
        this.width = width;
        this.height = height;
        this.cmdLineEngineTimeout = cmdLineEngineTimeout;
        initEngines();
    }

    /**
     * This initEngines should work generally but if needed, you can override
     */
    protected void initEngines() {
        // GraphvizCmdLineEngine is faster if available (e.g. /usr/bin/dot). If unavailable, falls back to the next engine
        GraphvizCmdLineEngine cmdLineEngine = new GraphvizCmdLineEngine();
        cmdLineEngine.timeout(cmdLineEngineTimeout, TimeUnit.SECONDS);
        Graphviz.useEngine(cmdLineEngine, new GraphvizV8Engine(), new GraphvizJdkEngine());
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rank.RankDir getRankDir() {
        return rankDir;
    }

    public void setRankDir(Rank.RankDir rankDir) {
        this.rankDir = rankDir;
    }

    public double getSep() {
        return sep;
    }

    public void setSep(double sep) {
        this.sep = sep;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    private guru.nidi.graphviz.model.Graph convertGraph(Graph g) {
        guru.nidi.graphviz.model.Graph graph = graph(graphName).directed()
                                                               .graphAttr().with(Rank.dir(rankDir).sep(sep));

        List<Node> nodeList = g.getNodeMap().values().stream().collect(Collectors.toList());
        for (Node n : nodeList) {
            guru.nidi.graphviz.model.Node node = node(n.getRuleName());
            if (n.getStatus() == Node.Status.CHANGED) {
                node = node.with(Color.RED, Style.FILLED);
            } else if (n.getStatus() == Node.Status.IMPACTED) {
                node = node.with(Color.YELLOW, Style.FILLED);
            }
            for (Link l : n.getOutgoingLinks()) {
                Style<ForNodeLink> style;
                if (l.getReactivityType() == ReactivityType.POSITIVE) {
                    style = Style.SOLID;
                } else if (l.getReactivityType() == ReactivityType.NEGATIVE) {
                    style = Style.DASHED;
                } else {
                    // UNKNOWN
                    style = Style.DOTTED;
                }
                node = node.link(to(node(l.getTarget().getRuleName())).with(style));
            }
            graph = graph.with(node);
        }
        return graph;
    }

    public void generateDot(Graph g) {
        guru.nidi.graphviz.model.Graph graph = convertGraph(g);

        try {
            String filePath = outputDir + File.separator + graphName + ".dot";
            Graphviz.fromGraph(graph).totalMemory(totalMemory).width(width).height(height).render(Format.DOT).toFile(new File(filePath));
            logger.info("--- Graph dot format is generated to " + filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void generatePng(Graph g) {
        guru.nidi.graphviz.model.Graph graph = convertGraph(g);

        try {
            String filePath = outputDir + File.separator + graphName + ".png";
            Graphviz.fromGraph(graph).totalMemory(totalMemory).width(width).height(height).render(Format.PNG).toFile(new File(filePath));
            logger.info("--- Graph png image is generated to " + filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void generateSvg(Graph g) {
        guru.nidi.graphviz.model.Graph graph = convertGraph(g);

        try {
            String filePath = outputDir + File.separator + graphName + ".svg";
            Graphviz.fromGraph(graph).totalMemory(totalMemory).width(width).height(height).render(Format.SVG).toFile(new File(filePath));
            logger.info("--- Graph svg image is generated to " + filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
