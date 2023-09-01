package org.kie.api.runtime.rule;

public interface ViewChangedEventListener {

    public void rowInserted(Row row);

    public void rowDeleted(Row row);

    public void rowUpdated(Row row);
}
