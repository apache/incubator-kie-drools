package org.optaplanner.persistence.jackson.impl.testdata.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class JacksonTestdataValue extends JacksonTestdataObject {

    public JacksonTestdataValue() {
    }

    public JacksonTestdataValue(String code) {
        super(code);
    }

}
