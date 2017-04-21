package nl.rws.dso.inception.backend.extension;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;

/**
 * Created by akoufoudakis on 07/04/17.
 */
public class UitvoeringsregelRefRegister implements DMNExtensionRegister {

    public void registerExtensionConverters(XStream xStream) {

        xStream.alias("uitvoeringsregelRef", String.class);

    }

}
