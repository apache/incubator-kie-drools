package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BayesInstance<T> {
    private Graph<BayesVariable>       graph;
    private JunctionTree               tree;
    private Map<String, BayesVariable> variables;
    private BayesLikelyhood[]          likelyhoods;
    private boolean                    dirty;

    private CliqueState[]              cliqueStates;
    private SeparatorState[]           sparatorStates;
    private BayesVariableState[]       varStates;

    private GlobalUpdateListener globalUpdateListener;
    private PassMessageListener  passMessageListener;

    private int[]                targetParameterMap;
    private Class<T>             targetClass;
    private Constructor<T>       targetConstructor;

    public BayesInstance(JunctionTree tree, Class<T> targetClass) {
        this( tree );
        this.targetClass = targetClass;
        buildParameterMapping( targetClass );
    }

    public BayesInstance(JunctionTree tree) {
        this.graph = tree.getGraph();
        this.tree = tree;
        variables = new HashMap<String, BayesVariable>();
        varStates =  new BayesVariableState[graph.size()];
        for (GraphNode<BayesVariable> node : graph) {
            BayesVariable var = node.getContent();
            variables.put(var.getName(), var);
            varStates[var.getId()] = var.createState();
        }

        cliqueStates = new CliqueState[tree.getJunctionTreeNodes().length];
        for (JunctionTreeClique clique : tree.getJunctionTreeNodes()) {
            cliqueStates[clique.getId()] = clique.createState();
        }

        sparatorStates = new SeparatorState[tree.getJunctionTreeSeparators().length];
        for ( JunctionTreeSeparator sep : tree.getJunctionTreeSeparators() ) {
            sparatorStates[sep.getId()] = sep.createState();
        }

        likelyhoods = new BayesLikelyhood[graph.size()];
    }

    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
        buildParameterMapping( targetClass );
    }

    public  void buildParameterMapping(Class<T> target) {
        Constructor[] cons = target.getConstructors();
        if ( cons != null ) {
            for ( Constructor con : cons ) {
                Annotation[] anns = con.getDeclaredAnnotations();
                for ( Annotation ann : anns ) {
                    if ( ann.annotationType() == BayesVariableConstructor.class ) {
                        Class[] paramTypes = con.getParameterTypes();

                        targetParameterMap = new int[paramTypes.length];
                        if ( paramTypes[0] != BayesInstance.class ) {
                            throw new RuntimeException( "First Argument must be " + BayesInstance.class.getSimpleName() );
                        }
                        Annotation[][] paramAnns = con.getParameterAnnotations();
                        for ( int j = 1; j < paramAnns.length; j++ ) {
                            if ( paramAnns[j][0].annotationType() == VarName.class ) {
                                String varName = ((VarName)paramAnns[j][0]).value();
                                BayesVariable var = variables.get(varName);
                                Object[] outcomes = new Object[ var.getOutcomes().length ];
                                if ( paramTypes[j].isAssignableFrom( Boolean.class) || paramTypes[j].isAssignableFrom( boolean.class) ) {
                                    for ( int k = 0; k < var.getOutcomes().length; k++ ) {
                                        outcomes[k] = Boolean.valueOf( (String) var.getOutcomes()[k]);
                                    }
                                }
                                varStates[var.getId()].setOutcomes( outcomes );
                                targetParameterMap[j] = var.getId();
                                System.out.println( paramTypes[j].getName() + " : " +  varName );
                            }
                        }
                        targetConstructor = con;
                    }
                }
            }
        }
        if ( targetConstructor == null ) {
            throw new IllegalStateException( "Unable to find Constructor" );
        }
    }

    public GlobalUpdateListener getGlobalUpdateListener() {
        return globalUpdateListener;
    }

    public void setGlobalUpdateListener(GlobalUpdateListener globalUpdateListener) {
        this.globalUpdateListener = globalUpdateListener;
    }

    public PassMessageListener getPassMessageListener() {
        return passMessageListener;
    }

    public void setPassMessageListener(PassMessageListener passMessageListener) {
        this.passMessageListener = passMessageListener;
    }

    public Map<String, BayesVariable> getVariables() {
        return variables;
    }

    public void setLikelyhood(BayesLikelyhood likelyhood) {
        BayesLikelyhood old = this.likelyhoods[likelyhood.getVariable().getId()];
        if ( old == null || !old.equals( likelyhood ) ) {
            this.likelyhoods[likelyhood.getVariable().getId()] = likelyhood;
            dirty = true;
        }
    }

    public void globalUpdate() {
        applyEvidence();
        recurseGlobalUpdate(tree.getRoot());
    }

    public void applyEvidence() {
        for ( int i = 0; i < likelyhoods.length; i++ ) {
            BayesLikelyhood l = likelyhoods[i];
            if ( l != null ) {
                int family = likelyhoods[i].getVariable().getFamily();
                JunctionTreeClique node = tree.getJunctionTreeNodes()[family];
                likelyhoods[i].multiplyInto(cliqueStates[family].getPotentials());
                BayesAbsorption.normalize(cliqueStates[family].getPotentials());
            }
        }

    }

    public void globalUpdate(JunctionTreeClique clique) {
        if ( globalUpdateListener != null ) {
            globalUpdateListener.beforeGlobalUpdate(cliqueStates[clique.getId()]);
        }
        collectEvidence( clique );
        distributeEvidence( clique );
        if ( globalUpdateListener != null ) {
            globalUpdateListener.afterGlobalUpdate(cliqueStates[clique.getId()]);
        }
    }

    public void recurseGlobalUpdate(JunctionTreeClique clique) {
        globalUpdate(clique);

        List<JunctionTreeSeparator> seps = clique.getChildren();
        for ( JunctionTreeSeparator sep : seps ) {
            recurseGlobalUpdate(sep.getChild());
        }
    }

    public void collectEvidence(JunctionTreeClique clique) {
        if ( clique.getParentSeparator() != null ) {
            collectParentEvidence(clique.getParentSeparator().getParent(), clique.getParentSeparator(), clique, clique);
        }

        collectChildEvidence(clique, clique);
    }

    public void collectParentEvidence(JunctionTreeClique clique, JunctionTreeSeparator sep, JunctionTreeClique child, JunctionTreeClique startClique) {
        if ( clique.getParentSeparator() != null ) {
            collectParentEvidence(clique.getParentSeparator().getParent(), clique.getParentSeparator(),
                                  clique,
                                  startClique);
        }

        List<JunctionTreeSeparator> seps = clique.getChildren();
        for ( JunctionTreeSeparator childSep : seps ) {
            if ( childSep.getChild() == child )  {
                // ensure that when called from collectParentEvidence it does not re-enter the same node
                continue;
            }
            collectChildEvidence(childSep.getChild(), startClique);
        }

        passMessage(clique, child.getParentSeparator(), child );
    }


    public void collectChildEvidence(JunctionTreeClique clique, JunctionTreeClique startClique) {
        List<JunctionTreeSeparator> seps = clique.getChildren();
        for ( JunctionTreeSeparator sep : seps ) {
            collectChildEvidence(sep.getChild(), startClique);
        }

        if ( clique.getParentSeparator() != null && clique != startClique ) {
            // root has no parent, so we need to check.
            // Do not propogate the start node into another node
            passMessage(clique, clique.getParentSeparator(), clique.getParentSeparator().getParent() );
        }
    }

    public void distributeEvidence(JunctionTreeClique clique) {
        if ( clique.getParentSeparator() != null ) {
            distributeParentEvidence(clique.getParentSeparator().getParent(), clique.getParentSeparator(), clique, clique);
        }

        distributeChildEvidence(clique, clique);
    }

    public void distributeParentEvidence(JunctionTreeClique clique, JunctionTreeSeparator sep, JunctionTreeClique child, JunctionTreeClique startClique) {
        passMessage(child, child.getParentSeparator(), clique);

        if ( clique.getParentSeparator() != null ) {
            distributeParentEvidence(clique.getParentSeparator().getParent(), clique.getParentSeparator(),
                                     clique,
                                     startClique);
        }

        List<JunctionTreeSeparator> seps = clique.getChildren();
        for ( JunctionTreeSeparator childSep : seps ) {
            if ( childSep.getChild() == child )  {
                // ensure that when called from distributeParentEvidence it does not re-enter the same node
                continue;
            }
            distributeChildEvidence(childSep.getChild(), startClique);
        }
    }


    public void distributeChildEvidence(JunctionTreeClique clique, JunctionTreeClique startClique) {
        if ( clique.getParentSeparator() != null && clique != startClique ) {
            // root has no parent, so we need to check.
            // Do not propogate the start node into another node
            passMessage( clique.getParentSeparator().getParent(), clique.getParentSeparator(), clique );
        }

        List<JunctionTreeSeparator> seps = clique.getChildren();
        for ( JunctionTreeSeparator sep : seps ) {
            distributeChildEvidence(sep.getChild(), startClique);
        }
    }


    /**
     * Passes a message from node1 to node2.
     * node1 projects its trgPotentials into the separator.
     * node2 then absorbs those trgPotentials from the separator.
     * @param sourceClique
     * @param sep
     * @param targetClique
     */
    public void passMessage( JunctionTreeClique sourceClique, JunctionTreeSeparator sep, JunctionTreeClique targetClique) {
        double[] sepPots = sparatorStates[sep.getId()].getPotentials();
        double[] oldSepPots = Arrays.copyOf(sepPots, sepPots.length);

        BayesVariable[] sepVars = sep.getValues().toArray(new BayesVariable[sep.getValues().size()]);

        if ( passMessageListener != null ) {
            passMessageListener.beforeProjectAndAbsorb(sourceClique, sep, targetClique, oldSepPots);
        }

        project(sepVars, cliqueStates[sourceClique.getId()], sparatorStates[sep.getId()]);
        if ( passMessageListener != null ) {
            passMessageListener.afterProject(sourceClique, sep, targetClique, oldSepPots);
        }

        absorb(sepVars, cliqueStates[targetClique.getId()], sparatorStates[sep.getId()], oldSepPots);
        if ( passMessageListener != null ) {
            passMessageListener.afterAbsorb(sourceClique, sep, targetClique, oldSepPots);
        }
    }

    //private static void project(BayesVariable[] sepVars, JunctionTreeNode node, JunctionTreeSeparator sep) {
    private static void project(BayesVariable[] sepVars, CliqueState clique, SeparatorState separator) {
        //JunctionTreeNode node, JunctionTreeSeparator sep
        BayesVariable[] vars = clique.getJunctionTreeClique().getValues().toArray(new BayesVariable[clique.getJunctionTreeClique().getValues().size()]);
        int[] sepVarPos = PotentialMultiplier.createSubsetVarPos(vars, sepVars);

        int sepVarNumberOfStates = PotentialMultiplier.createNumberOfStates(sepVars);
        int[] sepVarMultipliers = PotentialMultiplier.createIndexMultipliers(sepVars, sepVarNumberOfStates);

        BayesProjection p = new BayesProjection(vars, clique.getPotentials(), sepVarPos, sepVarMultipliers, separator.getPotentials());
        p.project();
    }

    //private static void absorb(BayesVariable[] sepVars, JunctionTreeNode node, JunctionTreeSeparator sep, double[] oldSepPots ) {
    private static void absorb(BayesVariable[] sepVars, CliqueState clique, SeparatorState separator, double[] oldSepPots ) {
        //BayesVariable[] vars = node.getValues().toArray( new BayesVariable[node.getValues().size()] );
        BayesVariable[] vars = clique.getJunctionTreeClique().getValues().toArray(new BayesVariable[clique.getJunctionTreeClique().getValues().size()]);

        int[] sepVarPos = PotentialMultiplier.createSubsetVarPos(vars, sepVars);

        int sepVarNumberOfStates = PotentialMultiplier.createNumberOfStates(sepVars);
        int[] sepVarMultipliers = PotentialMultiplier.createIndexMultipliers(sepVars, sepVarNumberOfStates);

        BayesAbsorption p = new BayesAbsorption(sepVarPos, oldSepPots, separator.getPotentials(), sepVarMultipliers, vars, clique.getPotentials());
        p.absorb();
    }

    public BayesVariableState marginalize(String name) {
        BayesVariable var = this.variables.get(name);
        if ( var == null ) {
            throw new IllegalArgumentException("Variable name does not exist '" + name + "'" );
        }
        BayesVariableState varState = varStates[var.getId()];
        marginalize( varState );
        return varState;
    }

    public T marginalize() {
        Object[] args = new Object[targetParameterMap.length];
        args[0] = this;
        for ( int i = 1; i < targetParameterMap.length; i++) {
            int id = targetParameterMap[i];
            BayesVariableState varState = varStates[id];
            marginalize(varState);
            int highestIndex = 0;
            double highestValue = 0;
            int maximalCounts = 1;
            for (int j = 0, length = varState.getDistribution().length;j < length; j++ ){
                if ( varState.getDistribution()[j] > highestValue ) {
                    highestValue = varState.getDistribution()[j];
                    highestIndex = j;
                    maximalCounts = 1;
                }  else  if ( j != 0 && varState.getDistribution()[j] == highestValue ) {
                    maximalCounts++;
                }
            }
            if ( maximalCounts > 1 ) {
                // have maximal conflict, so choose random one
                int picked = new Random().nextInt( maximalCounts );
                int count = 0;
                for (int j = 0, length = varState.getDistribution().length;j < length; j++ ){
                    if ( varState.getDistribution()[j] == highestValue ) {
                        highestIndex = j;
                        if ( ++count > picked) {
                            break;
                        }
                    }
                }
            }
            args[i] = varState.getOutcomes()[highestIndex];
        }
        try {
            System.out.println( targetConstructor );
            return targetConstructor.newInstance( args );
        } catch (Exception e) {
           throw new RuntimeException( "Unable to instantiate " + targetClass.getSimpleName() + " " + Arrays.asList( args ), e );
        }
    }

    public void marginalize(BayesVariableState varState) {
        CliqueState cliqueState = cliqueStates[varState.getVariable().getFamily()];
        JunctionTreeClique jtNode = cliqueState.getJunctionTreeClique();
        new Marginalizer(jtNode.getValues().toArray( new BayesVariable[jtNode.getValues().size()]), cliqueState.getPotentials(), varState.getVariable(), varState.getDistribution() );
//        System.out.print( varState.getVariable().getName() + " " );
//        for ( double d : varState.getDistribution() ) {
//            System.out.print(d);
//            System.out.print(" ");
//        }
//        System.out.println(" ");
    }

    public SeparatorState[] getSparatorStates() {
        return sparatorStates;
    }

    public CliqueState[] getCliqueStates() {
        return cliqueStates;
    }

    public BayesVariableState[] getVarStates() {
        return varStates;
    }
}
