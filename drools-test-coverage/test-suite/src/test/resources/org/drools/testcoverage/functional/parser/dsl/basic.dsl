[condition][]There is a person=$p : Person()
[condition][]- {field:\w*}  {operator}  {value:\d*}={field}  {operator}  {value}
[condition][]- named {name}=name == "{name}"
[condition][]- equals {variable}=this := {variable}
[condition][]is equal to===
[condition][]is greater than=>
[consequence][]Print hello=System.out.println("Hello world!");
[consequence][]Consequences=//consequences
[condition][]- with a {what} {attr}={attr} {what!positive?>0/negative?<0/zero?==0/ERROR}