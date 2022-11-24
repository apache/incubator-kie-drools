package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;

final class ShiftKeyDeserializer extends AbstractKeyDeserializer<Shift> {

    public ShiftKeyDeserializer() {
        super(Shift.class);
    }

    @Override
    protected Shift createInstance(long id) {
        return new Shift(id);
    }
}
