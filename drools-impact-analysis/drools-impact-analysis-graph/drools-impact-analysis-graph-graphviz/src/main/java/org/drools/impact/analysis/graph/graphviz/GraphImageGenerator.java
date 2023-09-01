/**
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
package org.drools.impact.analysis.graph.graphviz;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.ForNodeLink;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.EngineResult;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.engine.GraphvizEngine;
import guru.nidi.graphviz.engine.GraphvizException;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.engine.Options;
import guru.nidi.graphviz.engine.Rasterizer;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.util.PortablePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

public class GraphImageGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GraphImageGenerator.class);

    private static final PortablePath DEFAULT_OUTPUT_DIR = PortablePath.of("target/graph-output");

    private static final String RENDER_FAILURE_WARN = "graphviz-java failed to render an image. Solutions would be:\n" +
                                                      "1. Install graphviz tools in your local machine. graphviz-java will use graphviz command line binary (e.g. /usr/bin/dot) if available.\n" +
                                                      "2. Consider generating a graph in DOT format and then visualize it with an external tool.";

    private boolean renderEngineFailed = false;
    private String graphName;
    private int width = 0; // when 0, auto-sized
    private int height = 0; // when 0, auto-sized
    private int totalMemory = 1000000000; // 1GB by default
    private int cmdLineEngineTimeout = 600; // 10 minutes by default
    private PortablePath outputDir = DEFAULT_OUTPUT_DIR;

    private Rank.RankDir rankDir = Rank.RankDir.LEFT_TO_RIGHT; // LEFT_TO_RIGHT gives a better view when you have a large number of nodes
    private double sep = 1; // interval between levels

    public GraphImageGenerator(String graphName) {
        this.graphName = graphName;
        initEngines();
    }

    // test purpose method to simulate an environment where rendering engine is not available
    public static GraphImageGenerator getGraphImageGeneratorWithErrorGraphvizEngine(String graphName) {
        GraphImageGenerator generator = new GraphImageGenerator();
        generator.graphName = graphName;
        Graphviz.useEngine(new GraphvizEngine() {

            @Override
            public void close() throws Exception {
                // not used
            }

            @Override
            public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
                onError.accept(this); // results in putting ErrorGraphvizEngine into Graphviz.engineQueue
            }

            @Override
            public EngineResult execute(String src, Options options, Rasterizer rasterizer) {
                return null;
            }

        });
        return generator;
    }

    private GraphImageGenerator() {
        // test purpose
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
        return outputDir.asString();
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = PortablePath.of(outputDir);
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
            } else if (n.getStatus() == Node.Status.TARGET) {
                node = node.with(Color.ORANGE, Style.FILLED);
            } else if (n.getStatus() == Node.Status.IMPACTING) {
                node = node.with(Color.LIGHTBLUE, Style.FILLED);
            }
            for (Link l : n.getOutgoingLinks()) {
                if (!nodeList.contains(l.getTarget())) {
                    continue; // a sub map may have a link to a node which doesn't exist in the sub map
                }
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

    public String generateDot(Graph g) {
        guru.nidi.graphviz.model.Graph graph = convertGraph(g);

        try {
            String filePath = outputDir.asString() + File.separator + graphName + ".dot";
            Graphviz.fromGraph(graph).totalMemory(totalMemory).width(width).height(height).render(Format.DOT).toFile(new File(filePath));
            logger.info("--- Graph dot format is generated to {}", filePath);
            return filePath;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String generatePng(Graph g) {
        return generateImage(g, Format.PNG);
    }

    public String generateSvg(Graph g) {
        return generateImage(g, Format.SVG);
    }

    private String generateImage(Graph g, Format format) {
        if (renderEngineFailed) {
            logger.warn(RENDER_FAILURE_WARN);
            return null;
        }

        guru.nidi.graphviz.model.Graph graph = convertGraph(g);

        try {
            String filePath = outputDir.asString() + File.separator + graphName + "." + format.fileExtension;
            Graphviz.fromGraph(graph).totalMemory(totalMemory).width(width).height(height).render(format).toFile(new File(filePath));
            logger.info("--- Graph {} image is generated to {}", format.fileExtension, filePath);
            return filePath;
        } catch (GraphvizException e) {
            logger.warn(RENDER_FAILURE_WARN, e);
            renderEngineFailed = true;
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
