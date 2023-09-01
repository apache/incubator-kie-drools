package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_4.TEvery;

import com.thoughtworks.xstream.XStream;

public class EveryConverter extends QuantifiedConverter {

    public EveryConverter(XStream xstream) {
        super( xstream );
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TEvery();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TEvery.class);
	}

}
