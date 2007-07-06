package org.drools;

public class PersonWithEquals {
    
    private String name;
    private int age;
    
    public PersonWithEquals() {
    }
    
    public PersonWithEquals(String name,
                            int age) {
        super();
        this.name = name;
        this.age = age;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + age;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final PersonWithEquals other = (PersonWithEquals) obj;
        if ( age != other.age ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }

}
