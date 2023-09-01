package org.drools.modelcompiler.facttemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.drools.base.facttemplates.Event;
import org.drools.base.facttemplates.FactTemplate;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class HashMapEventImpl extends HashMapFactImpl implements Event {

    private long timestamp = -1;
    private long expiration = Long.MAX_VALUE;

    public HashMapEventImpl(FactTemplate factTemplate) {
        this( factTemplate, new HashMap<>() );
    }

    public HashMapEventImpl(FactTemplate factTemplate, Map<String, Object> valuesMap) {
        super(factTemplate, valuesMap);
    }

    public HashMapEventImpl(UUID uuid, FactTemplate factTemplate, Map<String, Object> valuesMap, long timestamp, long expiration) {
        super(uuid, factTemplate, valuesMap);
        this.timestamp = timestamp;
        this.expiration = expiration;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public HashMapEventImpl withExpiration(long value, TimeUnit unit) {
        this.expiration = unitToLong( value, unit );
        return this;
    }

    @Override
    public String toString() {
        return "Event " + factTemplate.getName() + " with values = " + valuesMap;
    }
}
