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