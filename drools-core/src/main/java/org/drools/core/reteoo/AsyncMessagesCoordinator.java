package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AsyncMessagesCoordinator {

    private AsyncMessagesCoordinator() { }

    public static class Holder {
        private static final AsyncMessagesCoordinator INSTANCE = new AsyncMessagesCoordinator();
    }

    public static AsyncMessagesCoordinator get() {
        return Holder.INSTANCE;
    }

    private final Map<String, List<Consumer<AsyncMessage>>> listeners = new HashMap<>();

    public void propagate(String messageId, AsyncMessage leftTuple) {
        listeners.getOrDefault( messageId, Collections.emptyList() ).forEach( c -> c.accept( leftTuple ) );
    }

    synchronized void registerReceiver(String messageId, Consumer<AsyncMessage> receiver) {
        listeners.computeIfAbsent( messageId, s -> new ArrayList<>() ).add( receiver );
    }

    synchronized void deregisterReceiver(String messageId, Consumer<AsyncMessage> receiver) {
        List<Consumer<AsyncMessage>> consumers = listeners.get( messageId );
        consumers.remove( receiver );
        if (consumers.isEmpty()) {
            listeners.remove( messageId );
        }
    }

    public Map<String, List<Consumer<AsyncMessage>>> getListeners() {
        return listeners;
    }
}
