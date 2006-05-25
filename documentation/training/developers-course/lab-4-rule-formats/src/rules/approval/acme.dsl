#ACME Discount insurance rule language
[when]Driver is less than {age} years old=d : Driver(age < {age})
[when]Driver is greater than {age} years old=d : Driver(age > {age})
[when]Driver has had more than {prior} prior claims=d : Driver(priorClaims > {prior})
[when]Policy type is '{type}'=Policy(type == "{type}")
[then]Reject Policy with explanation : '{reason}'=assert(new Rejection("{reason}"));
[when]Driver has a location risk profile of '{risk}'=d : Driver(locationRiskProfile == "{risk}")
[when]Driver has an age of at least {age}=d : Driver(age >= {age})
[when]Driver is between {lower} and {upper} years old=d : Driver(age >= {lower}, age <= {upper})
[when]Policy has not been rejected=not Rejection()
[when]Driver has had {number} prior claims=d : Driver(priorClaims == {number})
[then]Approve Policy with the reason : '{reason}'=assert(new Approve("{reason}"));
