package org.drools.modelcompiler.domain;

import java.util.ArrayList;
import java.util.List;

public class ToysStore {

    private String cityName;
    private String storeName;

    private List<Toy> firstFloorToys = new ArrayList<>();
    private List<Toy> secondFloorToys = new ArrayList<>();

    public ToysStore(String cityName, String storeName) {
        this.cityName = cityName;
        this.storeName = storeName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Toy> getFirstFloorToys() {
        return firstFloorToys;
    }

    public void setFirstFloorToys(List<Toy> firstFloorToys) {
        this.firstFloorToys = firstFloorToys;
    }

    public List<Toy> getSecondFloorToys() {
        return secondFloorToys;
    }

    public void setSecondFloorToys(List<Toy> secondFloorToys) {
        this.secondFloorToys = secondFloorToys;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "ToysStore{" +
                "cityName='" + cityName + '\'' +
                ", storeName='" + storeName + '\'' +
                ", firstFloorToys=" + firstFloorToys +
                ", secondFloorToys=" + secondFloorToys +
                '}';
    }
}
