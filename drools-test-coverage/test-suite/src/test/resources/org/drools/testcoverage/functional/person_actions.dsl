[when]There is person younger than {age:[0-9]*} = $p : Person ( age > {age} )
[when]- lives in {city} on {street} on {number:[0-9]*} = address.city == "{city}" && address.street == "{street}" && address.number == {number} 
[then]Move the person to {city} on {street} on {number:[0-9]*} = Address a = new Address(); a.setCity("{city}"); a.setStreet("{street}"); a.setNumber({number}); $p.setAddress(a);
