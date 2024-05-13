#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

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
