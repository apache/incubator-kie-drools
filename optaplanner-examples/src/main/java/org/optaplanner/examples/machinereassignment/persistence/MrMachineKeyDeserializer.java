package org.optaplanner.examples.machinereassignment.persistence;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class MrMachineKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String value, DeserializationContext deserializationContext) {
        return new MrMachine(Long.parseLong(value));
    }
}
