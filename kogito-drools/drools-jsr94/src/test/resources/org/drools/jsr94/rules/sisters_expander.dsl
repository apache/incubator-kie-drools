#Thats a dsl for sisters rule
There exists a Person with name of {name}=Person(name=="{name}")
[when]Person is at least {age} years old and lives in {location}=Person(age > {age}, location=="{location}")
[then]Log {message}=System.out.println("{message}");
[when]There is a Person=$person1 : Person ( )
[when]There is a second Person and they are sisters=$person2 : Person ( ) eval( $person2.hasSister($person1) )
[then]Tell the world they are sisters=System.out.println($person1.getName() + " and " + $person2.getName() +" are sisters");
[then]Leave a comment in the WorkingMemory=assert( $person1.getName() + " and " + $person2.getName() +" are sisters");
