package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_4.TSome;

import com.thoughtworks.xstream.XStream;

public class SomeConverter extends QuantifiedConverter {

    public SomeConverter(XStream xstream) {
        super( xstream );
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TSome();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TSome.class);
	}

}
