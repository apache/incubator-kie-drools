package org.drools.guvnor.client.modeldriven;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

public class ModelField implements PortableObject {

    public static enum FIELD_CLASS_TYPE implements PortableObject{
        REGULAR_CLASS,
        TYPE_DECLARATION_CLASS
    }

    private String name;
    private String className;
    /**
     * parametrized type of clazz
     */
    private String type;

    private FIELD_CLASS_TYPE classType;

    public ModelField() {
    }

    
    /**
     * Creates a new ModelField instance
     * @param name field's name
     * @param clazz the class of the field. For fields defined as a type declaration
     * this clazz should be null.
     * @param type the type of the clazz.
     */
    public ModelField(String name, String clazz, String type) {
        this.name = name;
        
        //if clazz==null it is considered as a Type Declaration
        if (clazz == null){
            this.classType = FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS;
        }else{
            this.classType = FIELD_CLASS_TYPE.REGULAR_CLASS;
            this.className = clazz;
        }

        this.type = type;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public FIELD_CLASS_TYPE getClassType() {
        return classType;
    }


	@Override
	public String toString() {
		return "ModelField [classType=" + classType + ", name=" + name
				+ ", type=" + type + ", className=" + className + "]";
	}

    
    
}
