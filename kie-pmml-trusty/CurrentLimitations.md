<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

Current Limitations
===================

File to update while features are implemented or new uncovered ones are discovered.

rel 7.52.0
----------

Implemented models:
1) Regression model
2) Tree model
3) Scorecard model
4) Mining model
5) Clustering model

Overall limitations
===================
1. only one "target field" managed
2. Extension unimplemented

MiningSchema/MiningField
------------------------
1. only predicted/target/active usageType implemented
2. importance unimplemented
3. outliers unimplemented
4. lowValue unimplemented
5. highValue unimplemented
6. missingValueReplacement partly implemented
7. missingValueTreatment partly implemented
8. invalidValueTreatment unimplemented
9. invalidValueReplacement unimplemented


Output/OutputField
------------------
1. Decisions unimplemented
2. Value unimplemented
3. only predictedValue/probability result feature implemented
4. Rule feature unimplemented   
5. algorithm unimplemented
6. isMultiValued unimplemented
7. segmentId unimplemented
8. isFinalResult unimplemented

TransformationDictionary/LocalTransformation
--------------------------------------------
1. Only _Constant_, _FieldRef_ and _Apply_ expressions implemented
2. Default functions unimplemented

Target
------
Not implemented

Extensions
----------
Not implemented

Models limitations
==================

Regression model
----------------
1. ModelStats unimplemented
2. ModelExplanation unimplemented
3. ModelVerification unimplemented

Tree model
----------
1. verification unimplemented
2. ModelStats unimplemented
3. ModelExplanation unimplemented
4. ModelVerification unimplemented
5. IsMissing/IsNotMissing inside SimplePredicate unimplemented
6. Surrogate inside CompoundPredicate unimplemented
7. Only two _XOR_ predicates managed
8. Only _predictedValue-type_ output field managed
9. missingValueStrategy partially implemented
10. missingValuePenalty unimplemented
11. noTrueChildStrategy partially implemented
12. splitCharacteristic unimplemented
13. isScorable unimplemented

Scorecard model
---------------
1. verification unimplemented
2. ModelStats unimplemented
3. ModelExplanation unimplemented
4. ModelVerification unimplemented
5. see Tree model about predicates


Mining model
------------
1. verification unimplemented
2. ModelStats unimplemented
3. ModelExplanation unimplemented
4. ModelVerification unimplemented
5. VariableWeight unimplemented   
5. see Tree model about predicates

Clustering model
----------------
1. _distributionBased_ models not implemented
2. _table_ compare function not implemented
3. Only _euclidean_ and _squaredEuclidean_ aggregation functions are implemented
4. Input field type must be numerical (_integer_ / _double_)
5. Target field type must be _string_

