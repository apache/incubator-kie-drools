package org.drools;

public class DomainObjectHolder {

	DomainObject[] objects = new DomainObject[3];

	public DomainObjectHolder(){

	objects[0] = new DomainObject();
	objects[0].setMessage("Message1");
	objects[0].setValue(1);
	objects[0].setValue2(2);

	objects[1] = new DomainObject();
	objects[1].setMessage("Message2");
	objects[1].setValue(3);
	objects[1].setValue2(4);

	objects[2] = new DomainObject();
	objects[2].setMessage("Message3");
	objects[2].setValue(5);
	objects[2].setValue2(6);
	}

	public DomainObject[] getObjects(){
	return objects;
	}

	}
