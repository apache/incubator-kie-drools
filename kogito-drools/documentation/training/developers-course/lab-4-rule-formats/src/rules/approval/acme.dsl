#ACME Discount insurance rule language
[then]Log "{message}"=System.out.println("{message}");
[when]The Driver is less than {age} years old=d : Driver(age < {age})
[when]The Driver is greater than {age} years old=d : Driver(age > {age})
[when]The Driver has had more than {prior} prior claims=d : Driver(priorClaims > {prior})
[when]The Policy type is '{type}'=Policy(type == "{type}")
[then]Reject the policy with explanation : '{reason}'=assert(new Rejection("{reason}"));
[when]The Driver has a location risk profile of '{risk}'=d : Driver(locationRiskProfile == "{risk}")
[when]The Driver has an age of at least {age}=d : Driver(age >= {age})
[when]The Driver is between {lower} and {upper} years old=d : Driver(age >= {lower}, age <= {upper})
[when]Policy has not been rejected=not Rejection()
[when]Driver has had {number} prior claims=d : Driver(priorClaims == {number})
[then]Approve the policy with the reason : '{reason}'=assert(new Approve("{reason}"));
