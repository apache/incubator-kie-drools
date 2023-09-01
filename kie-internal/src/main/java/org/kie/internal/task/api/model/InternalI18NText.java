package org.kie.internal.task.api.model;

import org.kie.api.task.model.I18NText;

public interface InternalI18NText extends I18NText {

    void setId(Long id);

    void setLanguage(String language);

    void setText(String text);

}
