package org.drools.modelcompiler.consequence;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.model.BitMask;
import org.drools.model.DroolsEntryPoint;
import org.kie.api.runtime.rule.EntryPoint;

import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class DroolsEntryPointImpl implements DroolsEntryPoint {

    private final EntryPoint entryPoint;
    private final FactHandleLookup fhLookup;

    public DroolsEntryPointImpl( EntryPoint entryPoint, FactHandleLookup fhLookup ) {
        this.entryPoint = entryPoint;
        this.fhLookup = fhLookup;
    }

    @Override
    public void insert( Object object ) {
        entryPoint.insert( object );
    }

    @Override
    public void insert(Object object, boolean dynamic) {
        ((WorkingMemoryEntryPoint ) entryPoint).insert(object, dynamic);
    }

    @Override
    public void update( Object object, String... modifiedProperties ) {
        entryPoint.update( fhLookup.get(object), object, modifiedProperties );
    }

    @Override
    public void update( Object object, BitMask modifiedProperties ) {
        Class<?> modifiedClass = modifiedProperties.getPatternClass();
        (( WorkingMemoryEntryPoint ) entryPoint).update( fhLookup.get(object), object, adaptBitMask(modifiedProperties), modifiedClass, null);
    }

    @Override
    public void delete( Object object ) {
        entryPoint.delete( fhLookup.get(object) );
    }
}