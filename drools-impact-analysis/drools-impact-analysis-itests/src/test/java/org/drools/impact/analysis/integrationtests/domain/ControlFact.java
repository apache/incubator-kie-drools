package org.drools.impact.analysis.integrationtests.domain;

import java.util.HashMap;
import java.util.Map;

public class ControlFact {

    private String orderId;
    private String keyword;
    private Map<String, String> mapData = new HashMap<>();
    private Map<String, Integer> mapDataInt = new HashMap<>();

    public ControlFact() {}

    public ControlFact(String orderId) {
        this.orderId = orderId;
    }

    public ControlFact(String orderId, String keyword) {
        this.orderId = orderId;
        this.keyword = keyword;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }

    public Map<String, Integer> getMapDataInt() {
        return mapDataInt;
    }

    public void setMapDataInt(Map<String, Integer> mapDataInt) {
        this.mapDataInt = mapDataInt;
    }

}
