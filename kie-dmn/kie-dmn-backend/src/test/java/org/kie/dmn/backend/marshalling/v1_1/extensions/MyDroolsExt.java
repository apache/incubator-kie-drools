package org.kie.dmn.backend.marshalling.v1_1.extensions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("mydroolsext")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "content" })
public class MyDroolsExt {

    @XStreamAsAttribute
    private String b1;
    
    private String content;

    public String getContent() {
        return content;
    }

    
    public String getB1() {
        return b1;
    }

    
    
}
