# Validation Messages Reference

| Type | Description | Lifecycle | Example messages
| --- | --- | --- | ---
| INVALID_NAME | The listed name is not a valid FEEL identifier | compilation | Invalid name '%s': %s
| INVALID_SYNTAX | Invalid FEEL syntax on the referenced expression | compilation, runtime | %s: invalid syntax 
| REQ_NOT_FOUND | The referenced node was not found | compilation | Required input '%s' not found on node '%s'
| |  |  | Required Decision '%s' not found on node '%s'
| |  |  | Required Business Knowledge Model '%s' not found on node '%s'
| TYPE_REF_NOT_FOUND | the listed type reference could not be resolved | compilation | Type reference '%s' not found on node '%s'
| TYPE_DEF_NOT_FOUND | the listed type definition was not found | compilation | No '%s' type definition found on node '%s'
| |  |  | No '%s' type definition found for element '%s' on node '%s'
| UNSUPPORTED_ELEMENT | The referenced element is not supported by the implementation | compilation | Element %s with type='%s' is not supported.
  