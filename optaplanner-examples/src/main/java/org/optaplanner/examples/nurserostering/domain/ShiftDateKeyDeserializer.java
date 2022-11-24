package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;

final class ShiftDateKeyDeserializer extends AbstractKeyDeserializer<ShiftDate> {

    public ShiftDateKeyDeserializer() {
        super(ShiftDate.class);
    }

    @Override
    protected ShiftDate createInstance(long id) {
        return new ShiftDate(id);
    }
}
