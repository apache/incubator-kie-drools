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

RELEASES
========

Clustering model
================

rel 7.54.0
----------
Implemented partial functionality

Currently known limitation:

1. _distributionBased_ models not implemented
2. _table_ compare function not implemented
3. Only _euclidean_ and _squaredEuclidean_ aggregation functions are implemented
4. Input field type must be numerical (_integer_ / _double_)
5. Target field type must be _string_

Mining model
===============

rel 7.43.0
----------
Implemented overall functionality

Currently known limitations:

1. IsMissing/IsNotMissing inside SimplePredicate not implemented
2. Surrogate inside CompoundPredicate not implemented

Transformations
===============

rel 7.60.0
----------
Implemented overall functionality

Currently known limitations:

1. Only _Aggregation_ and _Lag_ not implemented


rel 7.41.0
----------
Implemented basic functionality

Currently known limitations:

1. Only _Constant_, _FieldRef_ and _Apply_ expressions implemented
2. Default functions not implemented


Scorecard model implementation status
=====================================

rel 7.37.0
----------
Implemented overall functionality

Currently known limitations:

1. _TransformationDictionary_/_LocalTransformation_ not managed
2. _Expression_ not managed

Tree model implementation status
================================

rel 7.60.0
----------
Currently known limitation:

1. _ScoreDistribution_ and probability evaluation available only in codegen (not-drools) implementation

rel 7.36.0
----------
Implemented overall functionality

Currently known limitation:

1. _TransformationDictionary_/_LocalTransformation_ not managed
2. _Expression_ not managed
3. Only two _XOR_ predicates managed
4. Only _predictedValue-type_ output field managed

Regression model implementation status
======================================

rel 7.36.0
----------
Implemented overall functionality

Currently known limitation:

1. _TransformationDictionary_/_LocalTransformation_ not managed
2. _Expression_ not managed
