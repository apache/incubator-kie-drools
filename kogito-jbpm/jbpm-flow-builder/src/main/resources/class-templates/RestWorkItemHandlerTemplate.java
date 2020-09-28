package org.jbpm.process.codegen;

import io.vertx.ext.web.client.WebClient;
import io.vertx.core.Vertx;
import org.kogito.workitem.rest.RestWorkItemHandler;

public class xxxRestWorkItemHandler extends RestWorkItemHandler {

    public xxxRestWorkItemHandler() {
        this(Vertx.vertx());
    }

    public xxxRestWorkItemHandler(Vertx vertx) {
        super(WebClient.create(vertx));
    }
}