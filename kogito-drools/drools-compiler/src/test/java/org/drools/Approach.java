package org.drools;

public class Approach {

	public String label;
	public String label2;
	public Float time;
	public Float time2;
	public Float distance;
	public Float distance2;
	public Float bearing;
	public Float bearing2;

	public Float getDistance2() {
		return distance2;
	}
	public void setDistance2(Float distance2) {
		this.distance2 = distance2;
	}
	public Float getTime2() {
		return time2;
	}
	public void setTime2(Float time2) {
		this.time2 = time2;
	}
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
	public Float getBearing2() {
		return bearing2;
	}
	public void setBearing2(Float bearing2) {
		this.bearing2 = bearing2;
	}
	public String toString(){
		return "Approach< label: "+label+" label2: "+label2+" time: "+time+" time2: "+time2+" distance: "+distance+" distance2: "+distance2+" bearing: "+bearing+" bearing2: "+bearing2+" >";
	}
}
