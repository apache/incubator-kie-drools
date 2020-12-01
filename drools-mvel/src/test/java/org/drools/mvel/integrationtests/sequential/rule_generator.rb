OUTPUT="rules.drl"

if File.exists? OUTPUT then File.delete(OUTPUT) end

f = File.new(OUTPUT, "w")


for i in 1..200

    s =  "

    rule \"Cheese_#{i}\"
        when
            $c : Cheese( price == #{i} )
        then
            list.add( $c.getType() );
    end

    rule \"Person and cheese_#{i}\"
        when
            $p : Person(name == \"p#{i}\")
            $c : Cheese(price == 1)
        then
            list.add($p.getName());

    end
    "
    f.write s



end

f.close
