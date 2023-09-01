package org.kie.internal.task.api.model;

import javax.xml.bind.annotation.XmlType;

@XmlType
public enum SubTasksStrategy{
    NoAction, EndParentOnAllSubTasksEnd, SkipAllSubTasksOnParentSkip
}
