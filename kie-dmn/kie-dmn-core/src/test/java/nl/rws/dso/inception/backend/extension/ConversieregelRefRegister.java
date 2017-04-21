package nl.rws.dso.inception.backend.extension;

import com.thoughtworks.xstream.XStream;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;

/**
 * Created by akoufoudakis on 20/04/2017.
 */
public class ConversieregelRefRegister implements DMNExtensionRegister {

    public void registerExtensionConverters(XStream xStream) {
        xStream.alias("conversieregelRef", String.class);
    }

}
