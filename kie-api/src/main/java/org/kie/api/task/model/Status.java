package org.kie.api.task.model;

import javax.xml.bind.annotation.XmlType;


@XmlType
public enum Status {
    Created, Ready, Reserved, InProgress, Suspended, Completed, Failed, Error, Exited, Obsolete
}
