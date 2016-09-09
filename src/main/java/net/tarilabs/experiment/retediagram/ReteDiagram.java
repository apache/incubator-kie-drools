package net.tarilabs.experiment.retediagram;

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

import org.apache.commons.lang3.StringEscapeUtils;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.spi.BetaNodeFieldConstraint;
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
                dumpNode( entryPointNode, "", new HashSet<BaseNode>(), levelMap, out);
                out.println("");
                printLevelMap(levelMap, out);
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
        }
    }

    private static void printLevelMap(HashMap<Class<? extends BaseNode>, Set<BaseNode>> levelMap, PrintStream out) {
        // FIXME:
        levelMap.computeIfAbsent(BetaNode.class, k -> new HashSet<>());
        for (Entry<Class<? extends BaseNode>, Set<BaseNode>> kv : levelMap.entrySet()) {
            if (kv.getKey().isAssignableFrom(  ObjectTypeNode.class) ) {
               printLevelMapLevel(1, kv.getValue(), out);
            } else if (kv.getKey().isAssignableFrom(  AlphaNode.class) ) {
                printLevelMapLevel(2, kv.getValue(), out);
            } else if (kv.getKey().isAssignableFrom(  LeftInputAdapterNode.class) ) {
                printLevelMapLevel(3, kv.getValue(), out);
            } else if (kv.getKey().isAssignableFrom(  BetaNode.class) ) {
                printLevelMapLevel(4, kv.getValue(), out);
            } else if (kv.getKey().isAssignableFrom(  RuleTerminalNode.class) ) {
                printLevelMapLevel(5, kv.getValue(), out);
            }
        }
        out.println(" edge[style=invis];\n" + 
                " l1->l2->l3->l4->l5;");
    }

    private static void printLevelMapLevel(int i, Set<BaseNode> value, PrintStream out) {
        StringBuilder nodeIds = new StringBuilder();
        for (BaseNode n : value) {
            nodeIds.append(printNodeId(n)+"; ");
        }
        String level = String.format(" {rank=same; l%1$d[style=invis, shape=point]; %2$s}",
                i,
                nodeIds.toString());
        out.println(level);
    }

    private static void dumpNode(BaseNode node, String ident, Set<BaseNode> visitedNodes, Map<Class<? extends BaseNode>, Set<BaseNode>> levelMap, PrintStream out) {
        if (!visitedNodes.add( node )) {
            return;
        }
        out.println(ident + printNodeId(node) + " " + printNodeAttributes(node) + " ;");
        addToLevel(node, levelMap);
        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                out.println(ident + printNodeId(node) + " -> " + printNodeId((BaseNode)sink) + " ;");
                if (sink instanceof BaseNode) {
                    dumpNode((BaseNode)sink, ident + " ", visitedNodes, levelMap, out);
                }
            }
        }
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
        } else if (node instanceof BetaNode ) {
            return "BN"+node.getId();
        } else if (node instanceof RuleTerminalNode ) {
            return "RTN"+node.getId();
        }
        return "???"+node;
    }

    private static String printNodeAttributes(BaseNode node) {
        if (node instanceof EntryPointNode ) {
            EntryPointNode n = (EntryPointNode) node;
            return String.format("[shape=point xlabel=\"%1$s\"]",
                    n.getEntryPoint().getEntryPointId());
        } else if (node instanceof ObjectTypeNode ) {
            ObjectTypeNode n = (ObjectTypeNode) node;
            return String.format("[shape=rect style=rounded label=\"%1$s\"]",
                    printClassObjectType(n));
        } else if (node instanceof AlphaNode ) {
            AlphaNode n = (AlphaNode) node;
            return String.format("[label=\"%1$s\"]",
                    escapeDot(n.getConstraint().toString()));
        } else if (node instanceof LeftInputAdapterNode ) {
            return "[shape=house orientation=-90]";
        } else if (node instanceof BetaNode ) {
            BetaNode n = (BetaNode) node;
            BetaNodeFieldConstraint[] constraints = n.getConstraints();
            String label = Arrays.stream(constraints).map(Object::toString).collect(Collectors.joining(", "));
            return String.format("[shape=box label=\"%1$s\"]",
                    escapeDot(label));
        } else if (node instanceof RuleTerminalNode ) {
            RuleTerminalNode n = (RuleTerminalNode) node;
            return String.format("[shape=doublecircle width=0.2 fillcolor=black style=filled label=\"\" xlabel=\"%1$s\"]",
                    n.getRule().getName());
        }
        return "???"+node;
    }

    private static String printClassObjectType(ObjectTypeNode n) {
        Class<?> classType = ((ClassObjectType)n.getObjectType()).getClassType();
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
