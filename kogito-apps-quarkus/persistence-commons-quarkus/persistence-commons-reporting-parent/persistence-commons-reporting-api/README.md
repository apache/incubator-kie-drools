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
# Reporting extension 

This module provides a framework to extract discrete values from a JSON column within 
a RDBMS table and populate secondary tables with said values.

The extractions are defined by mappings. These can either be provided in a configuration file at startup,
e.g. `bootstrap.json` or defined dynamically using a REST API for which the scafolding is provided in this 
module and a concrete implementation in `org.kie.kogito:persistence-commons-postgresql-reporting`.

## Mapping definitions

Mappings are defined with the following  JSON structure:
```json lines
{
  "mappingDefinitions": [ MAPPING_DEFINITON ]
}
```
### `MAPPING_DEFINITION`
```json lines
{
  "mappingId": STRING,
  "sourceTableName": STRING,
  "sourceTableJsonFieldName": STRING,
  "sourceTableIdentityFields": [
    MAPPING_FIELD
  ],
  "sourceTablePartitionFields": [
    MAPPING_PARTITION
  ],
  "targetTableName": STRING,
  "fieldMappings": [
    MAPPING
  ]
}
```
| Property | Type | Definition
|----------|------|--------
| `mappingId` | `string` | A unique identifier for the mapping.
| `sourceTableName` | `string` | The name of the source table.
| `sourceTableJsonFieldName` | `string` | The name of the JSON field in the source table.
| `sourceTableIdentityFields` | `array` | An array of identify field(s) in the source table.
| `sourceTablePartitionFields` | `array` | An array of fields used to partition the source table.
| `targetTableName` | `string` | The name of the target table to create to hold the result of the mapping(s). 
| `fieldMappings` | `array` | The mappings.
 
### `MAPPING_FIELD`
```json lines
{
  "fieldName": STRING,
  "fieldType": STRING
}
```
| Property | Type | Definition
|----------|------|--------
| `fieldName` | `string` | The name of the field.
| `fieldType` | `string` | The JSON type of the field.
### `MAPPING_PARTITION`
```json lines
{
  "fieldName": STRING,
  "fieldType": STRING,
  "fieldValue": STRING
}
```
| Property | Type | Definition
|----------|------|--------
| `fieldName` | `string` | The name of the field.
| `fieldType` | `string` | The JSON type of the field.
| `fieldValue` | `string` | The value of the field to partition the table.
### `MAPPING`
```json lines
{
  "sourceJsonPath": STRING,
  "targetField": MAPPING_FIELD
}
```
| Property | Type | Definition
|----------|------|--------
| `sourceJsonPath` | `string` | A simplified `JSONPath`-like path.
| `targetField` | `string` | The name of the field in the target table.

### JSONPath-like syntax

Mappings follow a (very) simplified `JSONPath` syntax!

The `JSONPath`s  are used to navigate or identify siblings with a JSON document.

The following limitations apply:
- Only the dot notation is supported.
- A `[]` operator can be used to denote `collection`.
- There is no support for functions.
- There is no support for filters.

## Examples

All examples assume the following schema:
```postgresql
create table kogito_data_cache (
   var_name varchar(255) not null,
   cache_name varchar(255) not null,
   json_value jsonb,
   primary key (key, name)
);
```
All examples also shown the SQL for simplicity. The results are the same if you add, edit or delete 
entities through JPA. There are code examples in `org.kie.kogito:persistence-commons-postgresql-reporting`.
### Mapping a single source JSON field
```json
{
  "mappingId": "BasicTypeMappingId",
  "sourceTableName": "kogito_data_cache",
  "sourceTableJsonFieldName": "json_value",
  "sourceTableIdentityFields": [
    {
      "fieldName":"var_name",
      "fieldType": "STRING"
    }
  ],
  "sourceTablePartitionFields": [
    {
      "fieldName": "cache_name",
      "fieldType": "STRING",
      "fieldValue": "BasicType"
    }
  ],
  "targetTableName": "BasicTypeExtract",
  "fieldMappings": [
    {
      "sourceJsonPath": "field1",
      "targetField":{
        "fieldName": "field1MappedField",
        "fieldType": "NUMBER"
      }
    },
    {
      "sourceJsonPath": "field3",
      "targetField": {
        "fieldName": "field2MappedField",
        "fieldType": "STRING"
      }
    }
  ]
}
```
Data
```sql
insert into kogito_data_cache (
  'key',
  'BasicType', 
  '{
    "field1": 123,
    "field2": "Not mapped",
    "field3": "Hello world"
  }'
);
```
Result

|**field**| key | name | field1MappedField | field2MappedField
|---------|-----|------|-------------------|------------------
|**value**| `"key"` | `"BasicType"` | `123` | `"Hello world"`

### Mapping a collection
```json lines
{
  "mappingId": "HierarchicalTypeMappingId",
  "sourceTableName": "kogito_data_cache",
  "sourceTableJsonFieldName": "json_value",
  "sourceTableIdentityFields": [
    {
      "fieldName":"var_name",
      "fieldType": "STRING"
    }
  ],
  "sourceTablePartitionFields": [
    {
      "fieldName": "cache_name",
      "fieldType": "STRING",
      "fieldValue": "HierarchicalType"
    }
  ],
  "targetTableName": "HierarchicalTypeExtract",
  "fieldMappings": [
    {
      "sourceJsonPath": "root",
      "targetField": {
        "fieldName": "root",
        "fieldType": "STRING"
      }
    },
    {
      "sourceJsonPath": "nestedBasic.field3",
      "targetField": {
        "fieldName": "nestedBasicMappedField",
        "fieldType": "STRING"
      }
    },
    {
      "sourceJsonPath": "nestedBasicCollection[].field3",
      "targetField": {
        "fieldName": "nestedBasicCollectionMappedField",
        "fieldType": "STRING"
      }
    }
  ]
}
```
Data
```sql
insert into kogito_data_cache (
  'key',
  'HierarchicalType', 
  '{
    "root": "tools",
    "nestedBasic" : {
      "field1": 123,
      "field2": "Not mapped",   
      "field3": "Hello world"
    },
    "nestedBasicCollection": [
      {
        "field1": 456,
        "field2": "Not mapped",   
        "field3": "Axe"
      },
      {
        "field1": 789,
        "field2": "Not mapped",   
        "field3": "Spade"
      }
    ]
  }'
);
```
Result

|**field**| key | name | root | nestedBasicMappedField | nestedBasicCollectionMappedField
|---------|-----|------|------|------------------------|---------------------------------
|**value**| `"key"` | `"HierarchicalType"` | `"tools"` | `"Hello world"` | `"Axe"`
|**value**| `"key"` | `"HierarchicalType"` | `"tools"` | `"Hello world"` | `"Spade"`

### Mapping more complex nesting/collections
```json lines
{
  "mappingId": "ComplexHierarchicalTypeMappingId",
  "sourceTableName": "kogito_data_cache",
  "sourceTableJsonFieldName": "json_value",
  "sourceTableIdentityFields": [
    {
      "fieldName":"var_name",
      "fieldType": "STRING"
    }
  ],
  "sourceTablePartitionFields": [
    {
      "fieldName": "cache_name",
      "fieldType": "STRING",
      "fieldValue": "ComplexHierarchicalType"
    }
  ],
  "targetTableName": "ComplexHierarchicalTypeExtract",
  "fieldMappings": [
    {
      "sourceJsonPath": "root",
      "targetField": {
        "fieldName": "root",
        "fieldType": "STRING"
      }
    },
    {
      "sourceJsonPath": "nestedBasic.field3",
      "targetField": {
        "fieldName": "nestedBasicMappedField",
        "fieldType": "STRING"
      }
    },
    {
      "sourceJsonPath": "nestedComplexCollection[].root",
      "targetField": {
        "fieldName": "nestedComplexCollectionMappedField1",
        "fieldType": "STRING"
      }
    },
    {
      "sourceJsonPath": "nestedComplexCollection[].nestedComplexCollection[].root",
      "targetField": {
        "fieldName": "nestedComplexCollectionMappedSubField1",
        "fieldType": "STRING"
      }
    }
  ]
}
```
Data
```sql
insert into kogito_data_cache (
  'key',
  'ComplexHierarchicalType', 
  '{
    "root": "tools",
    "nestedBasic" : {
      "field1": 123,
      "field2": "Not mapped",   
      "field3": "Hello world"
    },
    "nestedComplexCollection": [
      {
        "root": "tools-sub-1",
        "nestedComplexCollection": [
          {
            "root": "tools-sub-1-sub-1"
          },
          {
            "root": "tools-sub-1-sub-2"
          }
        ]
      },
      {
        "root": "tools-sub-2",
        "nestedComplexCollection": [
          {
            "root": "tools-sub-2-sub-1"
          },
          {
            "root": "tools-sub-2-sub-2"
          }
        ]
      }
    ]
  }'
);
```
Result

|**field**| key | name | root | nestedBasicMappedField | nestedComplexCollectionMappedField1 | nestedComplexCollectionMappedSubField1
|---------|-----|------|------|------------------------|-------------------------------------|---------------------------------------
|**value**| `"key"` | `"ComplexHierarchicalType"` | `"tools"` | `"Hello world"` | `"tools-sub-1"` | `"tools-sub-1-sub-1"`
|**value**| `"key"` | `"ComplexHierarchicalType"` | `"tools"` | `"Hello world"` | `"tools-sub-1"` | `"tools-sub-1-sub-2"`
|**value**| `"key"` | `"ComplexHierarchicalType"` | `"tools"` | `"Hello world"` | `"tools-sub-2"` | `"tools-sub-2-sub-1"`
|**value**| `"key"` | `"ComplexHierarchicalType"` | `"tools"` | `"Hello world"` | `"tools-sub-2"` | `"tools-sub-2-sub-2"`
