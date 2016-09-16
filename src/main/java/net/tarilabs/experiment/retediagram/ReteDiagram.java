package net.tarilabs.experiment.retediagram;

import static java.util.stream.Collectors.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.KnowledgeRuntime;

public class ReteDiagram {
    private ReteDiagram() { }

    public static void dumpRete(KnowledgeBase kbase) {
        dumpRete((InternalKnowledgeBase) kbase);
    }

    public static void dumpRete(KnowledgeRuntime session) {
        dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(KieSession session) {
        dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(InternalKnowledgeBase kBase) {
        dumpRete(kBase.getRete());
    }

    public static void dumpRete(Rete rete) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            try (PrintStream out = new PrintStream(new FileOutputStream("test.gv"));) {
                out.println("digraph g {\n" +
                        "graph [fontname = \"Overpass\" fontsize=11];\n" + 
                        " node [fontname = \"Overpass\" fontsize=11];\n" + 
                        " edge [fontname = \"Overpass\" fontsize=11];");
                HashMap<Class<? extends BaseNode>, Set<BaseNode>> levelMap = new HashMap<>();
                HashMap<Class<? extends BaseNode>, List<BaseNode>> nodeMap = new HashMap<>();
                List<Vertex<BaseNode,BaseNode>> vertexes = new ArrayList<>();
                dumpNode( entryPointNode, "", new HashSet<BaseNode>(), nodeMap, vertexes, levelMap, out);
                out.println("");
                printNodeMap(nodeMap, out);
                out.println("");
                printVertexes(vertexes, out);
                out.println("");
                printLevelMap(levelMap, out, vertexes);
                out.println("}");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ProcessBuilder pbuilder = new ProcessBuilder( "dot", "-Tsvg", "-o", "test.svg", "test.gv" );
                pbuilder.redirectErrorStream( true );
                pbuilder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                ProcessBuilder pbuilder = new ProcessBuilder( "google-chrome", "test.svg" );
                pbuilder.redirectErrorStream( true );
                pbuilder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                ProcessBuilder pbuilder = new ProcessBuilder( "dot", "-Tpng", "-o", "test.png", "test.gv" );
                pbuilder.redirectErrorStream( true );
                pbuilder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void printVertexes(List<Vertex<BaseNode, BaseNode>> vertexes, PrintStream out ) {
        for ( Vertex<BaseNode, BaseNode> v : vertexes ) {
            out.println(printNodeId(v.from) + " -> " + printNodeId(v.to) + " ;");
        }
    }

    private static void printNodeMap(HashMap<Class<? extends BaseNode>, List<BaseNode>> nodeMap, PrintStream out) {
        printNodeMapNodes(nodeMap.get(EntryPointNode.class), out);
        printNodeMapNodes(nodeMap.get(ObjectTypeNode.class), out);
        printNodeMapNodes(nodeMap.get(AlphaNode.class), out);
        printNodeMapNodes(nodeMap.get(LeftInputAdapterNode.class), out);
        printNodeMapNodes(nodeMap.get(RightInputAdapterNode.class), out);
        // Level 4: BN
        List<BaseNode> l4 = nodeMap.entrySet().stream()
                                .filter(kv->BetaNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toList());
        printNodeMapNodes(l4, out);
        printNodeMapNodes(nodeMap.get(RuleTerminalNode.class), out);
    }

    public static void printNodeMapNodes(List<BaseNode> nodes, PrintStream out) {
        for (BaseNode node : nodes) {
            out.println(printNodeId(node) + " " + printNodeAttributes(node) + " ;");
        }
    }
    
    public static class Vertex<F,T> {
        public final F from;
        public final T to;
        public Vertex(F from, T to) {
            this.from = from;
            this.to = to;
        }
        public static <F, T> Vertex<F, T> of(F from, T to) {
            return new Vertex<F, T>(from, to);
        }
    }

    private static void printLevelMap(HashMap<Class<? extends BaseNode>, Set<BaseNode>> levelMap, PrintStream out, List<Vertex<BaseNode, BaseNode>> vertexes) {
        levelMap.keySet().forEach(System.out::println);
        // FIXME:
        levelMap.computeIfAbsent(BetaNode.class, k -> new HashSet<>());
        
        // Level 1: OTN
        Set<BaseNode> l1 = levelMap.entrySet().stream()
                                .filter(kv->ObjectTypeNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toSet());
        printLevelMapLevel("l1", l1, out);
        
        // Level 2: AN
        Set<BaseNode> l2 = levelMap.entrySet().stream()
                                .filter(kv->AlphaNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toSet());
        printLevelMapLevel("l2", l2, out);
        
        // Level 3: LIA
        Set<BaseNode> l3 = levelMap.entrySet().stream()
                                .filter(kv->LeftInputAdapterNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toSet());
        printLevelMapLevel("l3", l3, out);
        
        // RIA
        Set<BaseNode> lria = levelMap.entrySet().stream()
                                .filter(kv->RightInputAdapterNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toSet());
        printLevelMapLevel("lria", lria, out);
        
        // RIA beta sources
        Set<BaseNode> lriaSources = new HashSet<>();
        Set<Vertex<BaseNode, BaseNode>> onlyBetas = vertexes.stream().filter(v->v.from instanceof BetaNode).collect(toSet());
        for (BaseNode ria : lria) {
            Set<BaseNode> t = onlyBetas.stream()
                    .filter(v->v.to.equals(ria))
                    .map(v->v.from)
                    .collect(toSet());
            lriaSources.addAll(t);
        }
        for (BaseNode lriaSource : lriaSources) {
            lriaSources.addAll( recurseIncomingVertex(lriaSource, onlyBetas) );
        }
        printLevelMapLevel("lriaSources", lriaSources, out);
        
        // subnetwork Betas
        Set<BaseNode> lsubbeta = levelMap.entrySet().stream()
                                .filter(kv->BetaNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream())
                                .filter(b-> ((BetaNode) b).getObjectType() == null )
                                .collect(toSet());
        printLevelMapLevel("lsubbeta", lsubbeta, out);

        // Level 4: BN
        Set<BaseNode> l4 = levelMap.entrySet().stream()
                                .filter(kv->BetaNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream())
                                .filter(b-> !lriaSources.contains(b) )
                                .filter(b-> !lsubbeta.contains(b) )
                                .collect(toSet());
        printLevelMapLevel("l4", l4, out);

        // Level 5: RTN
        Set<BaseNode> l5 = levelMap.entrySet().stream()
                                .filter(kv->RuleTerminalNode.class.isAssignableFrom( kv.getKey() ))
                                .flatMap(kv->kv.getValue().stream()).collect(toSet());
        printLevelMapLevel("l5", l5, out);
        
        out.println(
//                " edge[style=invis];\n" + 
                " l1->l2->l3->lriaSources->lria->lsubbeta->l4->l5;");
    }

    private static Set<BaseNode> recurseIncomingVertex(BaseNode to, Set<Vertex<BaseNode, BaseNode>> vertexes) {
        Set<BaseNode> acc = new HashSet<>();
        for (Vertex<BaseNode, BaseNode> v : vertexes) {
            if (v.to.equals(to)) {
                acc.add( v.from );
                acc.addAll( recurseIncomingVertex(v.from, vertexes) );
            }
        }
        return acc;
    }

    private static void printLevelMapLevel(String levelId, Set<BaseNode> value, PrintStream out) {
        StringBuilder nodeIds = new StringBuilder();
        for (BaseNode n : value) {
            nodeIds.append(printNodeId(n)+"; ");
        }
        String level = String.format(" {rank=same; %1$s[style=invis, shape=point]; %2$s}",
                levelId,
                nodeIds.toString());
        out.println(level);
    }

    private static void dumpNode(BaseNode node, String ident, Set<BaseNode> visitedNodes, HashMap<Class<? extends BaseNode>, List<BaseNode>> nodeMap, List<Vertex<BaseNode, BaseNode>> vertexes, Map<Class<? extends BaseNode>, Set<BaseNode>> levelMap, PrintStream out) {
        if (!visitedNodes.add( node )) {
            return;
        }
        addToNodeMap(node, nodeMap);
        addToLevel(node, levelMap);
        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                vertexes.add(Vertex.of(node, (BaseNode)sink));
                if (sink instanceof BaseNode) {
                    dumpNode((BaseNode)sink, ident + " ", visitedNodes, nodeMap, vertexes, levelMap, out);
                }
            }
        }
    }

    private static void addToNodeMap(BaseNode node, HashMap<Class<? extends BaseNode>, List<BaseNode>> nodeMap) {
        nodeMap.computeIfAbsent(node.getClass(), k -> new ArrayList<>()).add(node);
    }

    private static void addToLevel(BaseNode node, Map<Class<? extends BaseNode>, Set<BaseNode>> levelMap) {
        levelMap.computeIfAbsent(node.getClass(), k -> new HashSet<>()).add(node);
    }

    private static String printNodeId(BaseNode node) {
        if (node instanceof EntryPointNode ) {
            return "EP"+node.getId();
        } else if (node instanceof ObjectTypeNode ) {
            return "OTN"+node.getId();
        } else if (node instanceof AlphaNode ) {
            return "AN"+node.getId();
        } else if (node instanceof LeftInputAdapterNode ) {
            return "LIA"+node.getId();
        } else if (node instanceof RightInputAdapterNode ) {
            return "RIA"+node.getId();
        } else if (node instanceof BetaNode ) {
            return "BN"+node.getId();
        } else if (node instanceof RuleTerminalNode ) {
            return "RTN"+node.getId();
        }
        return "UNK"+node.getId();
    }

    private static String printNodeAttributes(BaseNode node) {
        if (node instanceof EntryPointNode ) {
            EntryPointNode n = (EntryPointNode) node;
            return String.format("[shape=circle width=0.15 fillcolor=black style=filled label=\"\" xlabel=\"%1$s\"]",
                    n.getEntryPoint().getEntryPointId());
        } else if (node instanceof ObjectTypeNode ) {
            ObjectTypeNode n = (ObjectTypeNode) node;
            return String.format("[shape=rect style=rounded label=\"%1$s\"]",
                    strObjectType(n.getObjectType()) );
        } else if (node instanceof AlphaNode ) {
            AlphaNode n = (AlphaNode) node;
            return String.format("[label=\"%1$s\"]",
                    escapeDot(n.getConstraint().toString()));
        } else if (node instanceof LeftInputAdapterNode ) {
            return "[shape=house orientation=-90]";
        } else if (node instanceof RightInputAdapterNode ) {
            return "[shape=house orientation=90]";
        } else if (node instanceof JoinNode ) {
            BetaNode n = (BetaNode) node;
            BetaNodeFieldConstraint[] constraints = n.getConstraints();
            String label = "\u22C8";
            if (constraints.length > 0) {
                label = Arrays.stream(constraints).map(Object::toString).collect(Collectors.joining(", "));
            }
            return String.format("[shape=box label=\"%1$s\" href=\"http://drools.org\"]",
                    escapeDot(label));
        } else if (node instanceof NotNode ) {
            NotNode n = (NotNode) node;
            String label = "\u22C8";
            if (n.getObjectType() != null) {
                label = strObjectType(n.getObjectType());
            }
            return String.format("[shape=box label=\"not( %1$s )\"]", label );
        } else if (node instanceof AccumulateNode ) {
            AccumulateNode n = (AccumulateNode) node;
            return String.format("[shape=box label=<%1$s<BR/>%2$s<BR/>%3$s>]", 
                    n, Arrays.asList(n.getAccumulate().getAccumulators()), Arrays.asList(n.getConstraints()) );
        } else if (node instanceof RuleTerminalNode ) {
            RuleTerminalNode n = (RuleTerminalNode) node;
            return String.format("[shape=doublecircle width=0.2 fillcolor=black style=filled label=\"\" xlabel=\"%1$s\" href=\"http://drools.org\"]",
                    n.getRule().getName());
        }
        return String.format("[shape=box style=dotted label=\"%1$s\"]", node.toString());
    }
    
    private static String strObjectType(ObjectType ot) {
        if (ot instanceof ClassObjectType) {
            return abbrvClassForObjectType((ClassObjectType) ot);
        }
        return "??"+ ((ot==null)?"null":ot.toString());
    }

    private static String abbrvClassForObjectType(ClassObjectType cot) {
        Class<?> classType = cot.getClassType();
        String[] packageToken = classType.getPackage().getName().split("\\.");
        StringBuilder result = new StringBuilder();
        for (String pt : packageToken) {
            result.append(pt.charAt(0) + ".");
        }
        result.append(classType.getSimpleName());
        return result.toString();
    }

    private static String escapeDot(String string) {
        String escapeQuote = string.replace("\"", "\\\"");
        return escapeQuote;
    }

    public static Sink[] getSinks( BaseNode node ) {
        Sink[] sinks = null;
        if (node instanceof EntryPointNode ) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (node instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }
}
