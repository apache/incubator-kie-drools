package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;

import java.util.List;

public class JunctionTree {
    private Graph<BayesVariable>    graph;
    private JunctionTreeClique      root;
    private JunctionTreeClique[]    jtNodes;
    private JunctionTreeSeparator[] jtSeps;

    public JunctionTree(Graph<BayesVariable> graph, JunctionTreeClique root, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps) {
        this( graph, root, jtNodes, jtSeps, true );
    }

    public JunctionTree(Graph<BayesVariable> graph, JunctionTreeClique root, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps, boolean init) {
        this.graph = graph;
        this.root = root;
        this.jtNodes = jtNodes;
        this.jtSeps = jtSeps;
        if ( init ) {
            initialize();
        }
    }

    public Graph<BayesVariable> getGraph() {
        return graph;
    }

    public JunctionTreeClique getRoot() {
        return root;
    }

    private void initialize() {
        recurseJTNodesAndInitialisePotentials( graph, root );
    }


    public void recurseJTNodesAndInitialisePotentials(Graph graph, JunctionTreeClique jtNode) {
        BayesVariable[] vars = jtNode.getValues().toArray( new BayesVariable[jtNode.getValues().size()] );

        List<BayesVariable> family = jtNode.getFamily();
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] multipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);
        for ( BayesVariable var : family ) {
            multipleVarNodePotential(graph.getNode( var.getId() ), jtNode.getPotentials(), vars, multipliers);
        }

        List<JunctionTreeSeparator> seps = jtNode.getChildren();
        for ( JunctionTreeSeparator sep : seps ) {
            recurseJTNodesAndInitialisePotentials(graph, sep.getChild());
        }
    }

    public void multipleVarNodePotential(GraphNode<BayesVariable> varNode, double[] potentials, BayesVariable[] vars, int[] multipliers ) {

        BayesVariable[] parents = new BayesVariable[varNode.getInEdges().size()];
        for ( int i = 0; i < parents.length; i++ ) {
            parents[i] = (BayesVariable) varNode.getInEdges().get(i).getOutGraphNode().getContent();
        }

        int[] parentVarPos = PotentialMultiplier.createSubsetVarPos(vars, parents);

        int parentsNumberOfStates = PotentialMultiplier.createNumberOfStates(parents);
        int[] parentIndexMultipliers = PotentialMultiplier.createIndexMultipliers(parents, parentsNumberOfStates);

        BayesVariable var = varNode.getContent();
        int varPos = -1;
        for( int i = 0; i < vars.length; i++) {
            if ( vars[i] == var)  {
                varPos = i;
                break;
            }
        }
        if ( varPos == -1 || varPos == vars.length ) {
            throw new IllegalStateException( "Unable to find Variable in set" );
        }

        PotentialMultiplier m = new PotentialMultiplier(varNode.getContent().getProbabilityTable(), varPos, parentVarPos, parentIndexMultipliers,
                                                        vars, multipliers, potentials);

        m.multiple();
    }

    public JunctionTreeClique[] getJunctionTreeNodes() {
        return jtNodes;
    }

    public JunctionTreeSeparator[] getJunctionTreeSeparators() {
        return jtSeps;
    }

    public void setJunctionTreeNodes(JunctionTreeClique[] jtNodes) {
        this.jtNodes = jtNodes;
    }


}
