package org.drools.impact.analysis.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class AnalysisModel {

    private final List<Package> packages = new ArrayList<>();

    public List<Package> getPackages() {
        return packages;
    }

    public void addPackage(Package pkg) {
        packages.add(pkg);
    }

    @Override
    public String toString() {
        return "AnalysisModel{" +
                "packages=" + packages.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}
