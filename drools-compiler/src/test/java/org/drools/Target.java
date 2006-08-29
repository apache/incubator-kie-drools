package org.drools;

public class Target {

	public String label;
	public Float lat;
	public Float lon;
	public Float course;
	public Float speed;
	public Float time;
	
	public Float getCourse() {
		return course;
	}
	public void setCourse(Float course) {
		this.course = course;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Float getLat() {
		return lat;
	}
	public void setLat(Float lat) {
		this.lat = lat;
	}
	public Float getLon() {
		return lon;
	}
	public void setLon(Float lon) {
		this.lon = lon;
	}
	public Float getSpeed() {
		return speed;
	}
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	public Float getTime() {
		return time;
	}
	public void setTime(Float time) {
		this.time = time;
	}
	public String toString(){
		return "Target< label: "+label+" lat: "+lat+" lon: "+lon+" course: "+course+" speed: "+speed+" time: "+time+">";
	}
}
