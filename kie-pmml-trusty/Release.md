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
