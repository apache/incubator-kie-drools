package org.drools.xml.jaxb.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbListAdapter extends XmlAdapter<JaxbListWrapper, List> {

    @Override
    public JaxbListWrapper marshal(List v) throws Exception {
        return new JaxbListWrapper( v.toArray( new Object[v.size()]) );
    }

    @Override
    public List unmarshal(JaxbListWrapper v) throws Exception {
        return Arrays.asList( v.getElements() );
    }

}
