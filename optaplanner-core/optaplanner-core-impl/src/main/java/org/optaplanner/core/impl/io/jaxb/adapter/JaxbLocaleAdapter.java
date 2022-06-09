package org.optaplanner.core.impl.io.jaxb.adapter;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbLocaleAdapter extends XmlAdapter<String, Locale> {

    @Override
    public Locale unmarshal(String localeString) {
        if (localeString == null) {
            return null;
        }
        return new Locale(localeString);
    }

    @Override
    public String marshal(Locale locale) {
        if (locale == null) {
            return null;
        }
        return locale.toString();
    }
}
