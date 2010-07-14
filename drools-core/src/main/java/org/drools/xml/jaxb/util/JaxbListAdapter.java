package org.drools.xml.jaxb.util;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbListAdapter extends XmlAdapter<JaxbListWrapper, List> {

    @Override
    public JaxbListWrapper marshal(List v) throws Exception {
        if ( !(v instanceof JaxbListWrapper) ) {
            JaxbListWrapper<Object> wrapper = new JaxbListWrapper<Object>( ((List< ? >) v).size() );
            for ( Object item : ((List< ? >) v) ) {
                wrapper.add( item );
            }
            return wrapper;
        }
        return (JaxbListWrapper) v;
    }

    @Override
    public List unmarshal(JaxbListWrapper v) throws Exception {
        return v;
    }

}
