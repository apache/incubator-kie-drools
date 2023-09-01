package org.kie.api.task.model;

import java.io.Externalizable;

public interface I18NText extends Externalizable {

    Long getId();

    String getLanguage();

    String getText();

}
