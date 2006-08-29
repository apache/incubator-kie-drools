package org.drools;

public class Close {

	public String label;
	public String label2;
	public Float time;
	public Float distance;
	public Float bearing;

	public Float getBearing() {
		return bearing;
	}
	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}
	public Float getDistance() {
		return distance;
	}
	public void setDistance(Float distance) {
		this.distance = distance;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel2() {
		return label2;
	}
	public void setLabel2(String label2) {
		this.label2 = label2;
	}
	public Float getTime() {
		return time;
	}
	public void setTime(Float time) {
		this.time = time;
	}
	public String toString(){
		return "Close< label: "+label+" label2: "+label2+" time: "+time+" distance: "+distance+" bearing: "+bearing+">";
	}
}
