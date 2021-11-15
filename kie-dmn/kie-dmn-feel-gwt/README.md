JavaScript FEEL 
===============

The goal of this module is to provide helper classes for the JavaScript version of FEEL.

__util__ classes help with methods from `Class`, `Regexp` and `String` that can not be transpiled, by wrapping the methods. This makes it possible for this module to use JavaScript compatible solutions by overwriting the wrapped implementation in the original `kie-dmn-feel`.

__super__ package follows the GWT convention and helps to provide JavaScript compatible alternatives for classe that the JS compiler has issues with.

__rebind__ and __gwtreflection__ work around Java reflection that is used in the orinal `kie-dmn-feel` module. Generating Java code that is transpilable to JS. Reflection use is then satisfied, but only for the specific FEEL needs.