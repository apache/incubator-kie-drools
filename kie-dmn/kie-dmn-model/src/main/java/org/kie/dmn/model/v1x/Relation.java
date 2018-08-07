package org.kie.dmn.model.v1x;

public interface Relation extends Expression {

    java.util.List<InformationItem> getColumn();

    java.util.List<List> getRow();

}
