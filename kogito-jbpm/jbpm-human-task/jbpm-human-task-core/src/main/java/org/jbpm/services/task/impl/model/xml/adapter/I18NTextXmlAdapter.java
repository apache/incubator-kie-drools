package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;

public class I18NTextXmlAdapter extends XmlAdapter<String, I18NText> {

    @Override
    public String marshal(I18NText v) throws Exception {
        String out = v.getId() + ":" 
                + (v.getLanguage() == null ? "" : v.getLanguage()) + ":"
                + (v.getText() == null ? "" : v.getText());
        return out;
    }

    @Override
    public I18NText unmarshal(String v) throws Exception {
        String [] in = v.split(":");
        Long id = Long.parseLong(in[0]);
        String lang = null;
        if( in[1].length() > 0 ) { 
            lang = in[1];
        }
        String text = null;
        if( in[2].length() > 0 ) { 
            text = in[2];
        }
        
        I18NText i18nText = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText)i18nText).setId(id);
        ((InternalI18NText)i18nText).setLanguage(lang);
        ((InternalI18NText)i18nText).setText(text);
        return i18nText;
    }

}
