package org.drools.core.xml;

import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public interface Parser {
    void startElementBuilder(String localName, Attributes attrs);

    Element endElementBuilder();

    Object getCurrent();

    Object getParent();

    Object getParent(int i);

    Locator getLocator();

    Object getData();

    void setData(Object data);

    Map getMetaData();

    ClassLoader getClassLoader();
}
