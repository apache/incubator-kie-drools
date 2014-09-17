package org.kie.internal.query.data;

import org.kie.api.command.Command;

public interface QueryDataCommand<T> extends Command<T> {

    public QueryData getQueryData();
    public void setQueryData( QueryData queryData );

}
