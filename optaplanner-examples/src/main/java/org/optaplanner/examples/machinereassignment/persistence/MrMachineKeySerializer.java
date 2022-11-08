package org.optaplanner.examples.machinereassignment.persistence;

import java.io.IOException;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MrMachineKeySerializer extends JsonSerializer<MrMachine> {

    @Override
    public void serialize(MrMachine mrMachine, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeFieldId(mrMachine.getId());
    }
}
