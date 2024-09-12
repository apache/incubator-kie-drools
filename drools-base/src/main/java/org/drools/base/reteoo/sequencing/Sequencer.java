package org.drools.base.reteoo.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.steps.SubsequenceStep;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.util.CircularArrayList;

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
            if (step instanceof SubsequenceStep) {
                populateSequences(((SubsequenceStep)step).getSubsequence(), list);
            }
        }

        return list;
    }

    public Sequence[] getSequencences() {
        return sequencences;
    }

    public void start(SequencerMemory memory, ValueResolver valueResolver) {
        SequenceMemory sequenceMemory = memory.getOrCreateSequenceMemory(null, sequence, true);
        memory.setChildSequenceMemory(sequenceMemory);
        sequence.start(sequenceMemory, valueResolver);
    }


    public void tips(SequencerMemory seqrMem, ValueResolver valueResolver) {
        Sequence seq = seqrMem.getSequencer().getSequence();
        SequenceMemory seqMem = seqrMem.getSequenceMemory(seq);
        CircularArrayList<Object> data =  seqMem.getData();
        Step step = seq.getSteps()[seqMem.getStep()];
        switch (step.getType()) {
            case SUB_SEQUENCE:
                SubsequenceStep subseqStep = (SubsequenceStep) step;
                subseqStep.getIndex();
            case PARALLEL:
        }
    }

    public void stop(SequenceMemory memory, ValueResolver valueResolver) {
        while (memory != null) {
            memory.getSequence().getSteps()[memory.getStep()].deactivate(memory, valueResolver);
            memory = memory.getParent();
        }
    }

//    public void next(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
//        SequenceMemory sequenceMemory = sequencerMemory.getCurrentSequence();
//        if (sequenceMemory != null) {
//            sequenceMemory.getSequence().next(sequenceMemory, valueResolver);
//        } else {
//            // the root sequence has completed
//            sequencerMemory.match();
//        }
//    }

    public Sequence getSequence() {
        return sequence;
    }


}
