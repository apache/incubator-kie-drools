package org.kie.dmn.core.compiler.extensions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("firstNameDescription")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "content" })
public class FirstNameDescription {

    private String content;

    public String getContent() {
        return content;
    }

}
