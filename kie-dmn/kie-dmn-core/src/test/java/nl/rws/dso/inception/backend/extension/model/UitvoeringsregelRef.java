package nl.rws.dso.inception.backend.extension.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created by akoufoudakis on 21/04/2017.
 */

@XStreamAlias("uitvoeringsregelRef")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "content" })
public class UitvoeringsregelRef {

    private String content;

}
