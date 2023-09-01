package org.drools.compiler.kproject.models;

import org.kie.api.builder.model.ChannelModel;
import org.kie.api.builder.model.QualifierModel;

public class ChannelModelImpl implements ChannelModel {

    private KieSessionModelImpl kSession;

    private String name;
    private String type;
    private QualifierModel qualifier;

    public ChannelModelImpl() { }

    public ChannelModelImpl(KieSessionModelImpl kSession, String name, String type) {
        this.kSession = kSession;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public QualifierModel getQualifierModel() {
        return qualifier;
    }

    private void setQualifierModel(QualifierModel qualifier) {
        this.qualifier = qualifier;
    }

    public QualifierModel newQualifierModel(String type) {
        QualifierModelImpl qualifier = new QualifierModelImpl(type);
        this.qualifier = qualifier;
        return qualifier;
    }

    public KieSessionModelImpl getKSession() {
        return kSession;
    }

    public void setKSession(KieSessionModelImpl kSession) {
        this.kSession = kSession;
    }
}
