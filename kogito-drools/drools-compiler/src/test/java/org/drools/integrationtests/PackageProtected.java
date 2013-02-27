package org.drools.integrationtests;

class PackageProtected {

    private int x = 1;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
