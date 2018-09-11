package org.kie.pmml.pmml_4_2.model.mining;

import java.io.Serializable;

import org.dmg.pmml.pmml_4_2.descr.CompoundPredicate;
import org.dmg.pmml.pmml_4_2.descr.False;
import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.dmg.pmml.pmml_4_2.descr.SimpleSetPredicate;
import org.dmg.pmml.pmml_4_2.descr.True;

public class PredicateRuleFactory {
	
   public static PredicateRuleProducer getPredicateProducer(Serializable serializable) {
	   if (serializable instanceof SimplePredicate) return new SimpleSegmentPredicate((SimplePredicate)serializable);
	   if (serializable instanceof SimpleSetPredicate) return new SimpleSetSegmentPredicate((SimpleSetPredicate)serializable);
	   if (serializable instanceof CompoundPredicate) return new CompoundSegmentPredicate((CompoundPredicate)serializable);
	   if (serializable instanceof True) return new BooleanSegmentPredicate((True)serializable);
	   if (serializable instanceof False) return new BooleanSegmentPredicate((False)serializable);
	   throw new IllegalArgumentException("Serializable object [" + serializable.getClass().getName() + "] is not one of the known predicate types");
   }
}
