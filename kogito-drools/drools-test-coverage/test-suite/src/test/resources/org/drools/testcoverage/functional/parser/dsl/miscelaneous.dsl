[keyword]balík=package
[keyword]definuj=declare
[keyword]pravidlo=rule
[keyword]kdykoli=when
[keyword]potom=then
[keyword]konec=end
[keyword]priorita=salience
[keyword]necyklit=no-loop
[keyword]řetězec=String
[keyword]číslo=int
[keyword]Člověk=Person
[keyword]jméno=name
[keyword]věk=age

[when]Poznač člověka {var}={var} : Person()
[condition]Je nějaký člověk=Person()
[then]Pozdrav od {var}=System.out.println("Hello world from " + {var}.toString() + "!");
[consequence]Pozdrav=System.out.println("Hello world!");