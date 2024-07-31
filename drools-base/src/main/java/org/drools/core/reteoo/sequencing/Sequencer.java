package org.drools.core.reteoo.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.reteoo.sequencing.steps.Step;
import org.drools.core.reteoo.sequencing.steps.SequenceStep;

import java.util.ArrayList;
import java.util.List;

public class Sequencer {

    private final Sequence     sequence;

    private final Sequence[] sequencences;

    public Sequencer(Sequence sequence) {
        this.sequence = sequence;
        this.sequencences = populateSequences(sequence, new ArrayList<>()).stream().toArray(Sequence[]::new);
    }

    public static List<Sequence> populateSequences(Sequence sequence, List<Sequence> list) {
        list.add(sequence);
        for (Step step  : sequence.getSteps()) {
            if (step instanceof SequenceStep) {
                populateSequences(((SequenceStep)step).getSequence(), list);
            }
        }

        return list;
    }

    public Sequence[] getSequencences() {
        return sequencences;
    }

    public void start(SequencerMemory memory, ValueResolver valueResolver) {
        sequence.start(memory, valueResolver);
    }

    public void stop(SequencerMemory memory, ValueResolver valueResolver) {
        // deactive each active sequence on the stack.
        ArrayList<SequenceMemory>  stack = memory.getSequenceStack();
        for (int i = stack.size()-1; i >= 0; i--) {
            SequenceMemory sequenceMemory = stack.get(i);
            sequenceMemory.getSequence().getSteps()[sequenceMemory.getStep()].deactivate(sequenceMemory, valueResolver);
        }
        stack.clear();
    }

    public void next(SequencerMemory sequencerMemory, ValueResolver valueResolver) {
        SequenceMemory sequenceMemory = sequencerMemory.getCurrentSequence();
        if (sequenceMemory != null) {
            sequenceMemory.getSequence().next(sequenceMemory, valueResolver);
        } else {
            // the root sequence has completed
            sequencerMemory.match();
        }
    }

    public void fail(SequenceMemory memory) {
        //node.fail(memory);
        // TODO reset all memory
    }

    public Sequence getSequence() {
        return sequence;
    }


}
