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

[when]There is person younger than {age:[0-9]*} = $p : Person ( age > {age} )
[when]- lives in {city} on {street} on {number:[0-9]*} = address.city == "{city}" && address.street == "{street}" && address.number == {number} 
[then]Move the person to {city} on {street} on {number:[0-9]*} = Address a = new Address(); a.setCity("{city}"); a.setStreet("{street}"); a.setNumber({number}); $p.setAddress(a);
