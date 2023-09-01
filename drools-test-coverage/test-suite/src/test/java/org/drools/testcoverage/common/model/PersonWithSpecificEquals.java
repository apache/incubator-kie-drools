package org.drools.testcoverage.common.model;

public class PersonWithSpecificEquals {
    
    private String name;
    private int age;

    public PersonWithSpecificEquals(final String name, final int age) {
        super();
        this.name = name;
        this.age = age;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(final int age) {
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + age;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final PersonWithSpecificEquals other = (PersonWithSpecificEquals) obj;
        if ( age != other.age ) return false;
        if ( name == null ) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }

}
