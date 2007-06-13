[*][]There exists a Person with name of {name}=Person(name=="{name}")
[condition][]Person is at least {age} years old and lives in {location}=Person(age > {age}, location=="{location}")
[consequence][]Log {message}=System.out.println("{message}");
[condition][]There is a Person=$person1 : Person ( )
[condition][]There is a second Person and they are sisters=$person2 : Person ( ) eval( $person2.hasSister($person1) )
[consequence][]Tell the world they are sisters=System.out.println($person1.getName() + " and " + $person2.getName() +" are sisters");
[consequence][]Leave a comment in the WorkingMemory=insert( $person1.getName() + " and " + $person2.getName() +" are sisters");
