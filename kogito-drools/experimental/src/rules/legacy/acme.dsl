[condition][]There is a Driver=Driver()
[condition][]- age less than {age} years old=age < {age}
[condition][]- age is equal to {age} years old=age == {age}
[condition][]- age greater than {age} years old=age > {age}
[condition][]- has had more than {number} prior claims=priorClaims > {number}
[condition][]- has a location risk profile of '{risk}'=locationRiskProfile == "{risk}"
[condition][]- age is at least {age}=age >= {age}
[condition][]- age is between {lower} and {upper} years old=age >= {lower}, age <= {upper}
[condition][]- has had exactly {number} prior claims=priorClaims ==  {number}
[condition][]Policy type is '{type}'=Policy(type ==  "{type}")
[consequence][]Reject Policy with explanation : '{reason}'=insert(new Rejection("{reason}"));
[condition][]Policy has not been rejected=not Rejection()
[consequence][]Approve Policy with the reason : '{reason}'=insert(new Approve("{reason}"));
