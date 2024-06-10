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