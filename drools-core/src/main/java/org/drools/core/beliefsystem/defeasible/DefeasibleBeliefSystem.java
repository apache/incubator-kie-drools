package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefeasibleBeliefSystem implements BeliefSystem {
    public static final String DEFEATS = Defeats.class.getSimpleName();

    private HashMap<Rule, List<String>> superiorityRelation;

    public void insert(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        ((DefeasibleLogicalDependency) node).setStatus(resolveStatus(node));
    }

    public void delete(LogicalDependency node, BeliefSet beliefSet, PropagationContext context) {
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return null;
    }

    public LogicalDependency newLogicalDependency(Activation activation, BeliefSet beliefSet, Object object, Object value) {
        return null;
    }

    public void read(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

    public boolean checkSuperiority( Rule r1, Rule r2 ) {
        if ( superiorityRelation == null ) {
            superiorityRelation = new HashMap<Rule, List<String>>();
        }
        if ( ! superiorityRelation.containsKey( r1 ) ) {
            Object defs = r1.getMetaData().get( DEFEATS );
            List<String> defList = new ArrayList<String>();
            if ( defs instanceof String ) {
                defList.add( (String) defs );
            } else if ( defs instanceof Object[] ) {
                for ( Object o : (Object[]) defs ) {
                    defList.add( (String) o );
                }
            }
            if ( defList.size() > 0 ) {
                superiorityRelation.put( r1, defList );
            }
        }
        List<String> defeats = superiorityRelation.get( r1 );
        if ( defeats == null || defeats.size() == 0 ) {
            return false;
        }
        return defeats.contains( r2.getName() ) || defeats.contains( r2.getPackageName() + "." + r2.getName() );
    }

    private DefeasibilityStatus resolveStatus( LogicalDependency node ) {
        FactHandle justified = (FactHandle) node.getJustified();
        Rule justifierRule = node.getJustifier().getRule();
        List<? extends FactHandle> premise = node.getJustifier().getFactHandles();

        DefeasibilityStatus status = DefeasibilityStatus.resolve( node.getValue() );

        if ( status == null ) {
            DefeasibleRuleNature defeasibleType = DefeasibleRuleNature.STRICT;
            if ( node.getJustifier().getRule().getMetaData().containsKey( DefeasibleRuleNature.DEFEASIBLE.getLabel() ) ) {
                defeasibleType = DefeasibleRuleNature.DEFEASIBLE;
            } else if ( node.getJustifier().getRule().getMetaData().containsKey( DefeasibleRuleNature.DEFEATER.getLabel() ) ) {
                defeasibleType = DefeasibleRuleNature.DEFEATER;
            }

            switch ( defeasibleType ) {
                case DEFEASIBLE :
                    status = checkDefeasible( justified, justifierRule, premise );
                    break;
                case DEFEATER   :
                    status = checkDefeater( justified, justifierRule, premise );
                    break;
                case STRICT     :
                default         :
                    status = checkStrict( justified, justifierRule, premise );
                    break;
            }
        }
        return status;
    }

    private DefeasibilityStatus checkDefeasible( FactHandle justified, Rule justifierRule, List<? extends FactHandle> premise ) {
        return DefeasibilityStatus.DEFEASIBLY;
    }

    private DefeasibilityStatus checkDefeater( FactHandle justified, Rule justifierRule, List<? extends FactHandle> premise ) {
        return DefeasibilityStatus.DEFEATEDLY;
    }

    private DefeasibilityStatus checkStrict( FactHandle justified, Rule justifierRule, List<? extends FactHandle> premise ) {
        // The rule is strict. To prove that the derivation is strict we have to check that all the premises are
        // either facts or strictly proved facts
        for ( FactHandle h : premise ) {
            EqualityKey key = ((DefaultFactHandle) h).getEqualityKey();
            if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
                //DefeasibleBeliefSet bs = (DefeasibleBeliefSet) getTruthMaintenanceSystem().getJustifiedMap().get(((DefaultFactHandle) h).getId());

                DefeasibleBeliefSet bs = (DefeasibleBeliefSet) key.getBeliefSet();


                if ( bs.getStatus() != DefeasibilityStatus.DEFINITELY ) {
                    // to make a fact "definitely provable", all the supporting non-factual premises must be definitely provable.
                    return DefeasibilityStatus.DEFEASIBLY;
                }
            }
            // else it's a fact, so it's a good candidate for definite entailment
        }
        return DefeasibilityStatus.DEFINITELY;
    }


}
