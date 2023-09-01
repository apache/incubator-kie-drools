package org.kie.dmn.model.api;

public interface Relation extends Expression {

    java.util.List<InformationItem> getColumn();

    java.util.List<List> getRow();

}
