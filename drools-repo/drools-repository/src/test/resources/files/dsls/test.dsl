#This is a starter DSL to show off some of the features. Make sure you change it to be what you need !.
[when]There is an Instance with field of "{value}"=i: Instance(field=="{value}")
[when]Instance is at least {number} and field is "{value}"=i: Instance(number > {number}, location=="{value}")
[then]Log : "{message}"=System.out.println("{message}");
[then]Set field of instance to "{value}"=i.setField("{value}");
[then]Create instance : "{value}"=assert(new Instance("{value}"));
[when]There is no current Instance with field : "{value}"=not Instance(field == "{value}")
[then]Report error : "{error}"=System.err.println("{error}");
[then]Retract the fact : '{variable}'=retract({variable}); //this would retract bound variable {variable}
