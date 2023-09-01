package org.kie.dmn.backend.marshalling.v1_1.extensions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("mykieext")
public class MyKieExt {

    @XStreamAsAttribute
    private String a1;
    
    @XStreamAlias("mydroolsext")
    private MyDroolsExt content;

    public MyDroolsExt getContent() {
        return content;
    }
    
    public void setContent(MyDroolsExt content) {
        this.content = content;
    }
    
}
