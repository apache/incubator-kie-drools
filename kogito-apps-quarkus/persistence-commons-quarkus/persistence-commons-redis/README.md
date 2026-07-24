<!---
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
# Kogito persistence redis

This is the module that provides support for redis. 

## Supported storage operations
The operations of the interface `StorageService` that are currently implemented are: 
- `get`
- `put`
- `remove`
- `containsKey`
- `clear`
- `getRootType`
- `query`

And the operations that are not supported (yet) are:
- `addObjectCreatedListener`
- `addObjectUpdatedListener`
- `addObjectRemovedListener`
- `entrySet`

## Supported query operations
The operands that are not supported (yet) are: 
- `IN`
- `CONTAINS`
- `CONTAINS_ALL`
- `CONTAINS_ANY`
- `IS_NULL`
- `NOT_NULL`
- `AND`
- `OR`
- `NOT`

In addition to that, if `limit` is specified, than also `offset` has to be set (and vice versa).

Another current limitation is that it is not possible to sort over multiple attributes. This means that `sort(List<AttributeSort> sortBy)` throws an exception if `sortBy.size() > 1`.