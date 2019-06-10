[id='dmn-conformance-levels-con_{context}']
= DMN conformance levels

The DMN specification defines three incremental levels of conformance in a software implementation. A product that claims compliance at one level must also be compliant with any preceding levels. For example, a conformance level 3 implementation must also include the supported components in conformance levels 1 and 2. For the formal definitions of each conformance level, see the OMG https://www.omg.org/spec/DMN[Decision Model and Notation specification].

The following list summarizes the three DMN conformance levels:

Conformance level 1::
A DMN conformance level 1 implementation supports decision requirement diagrams (DRDs), decision logic, and decision tables, but decision models are not executable. Any language can be used to define the expressions, including natural, unstructured languages.

Conformance level 2::
A DMN conformance level 2 implementation includes the requirements in conformance level 1, and supports Simplified Friendly Enough Expression Language (S-FEEL) expressions and fully executable decision models.

Conformance level 3::
A DMN conformance level 3 implementation includes the requirements in conformance levels 1 and 2, and supports Friendly Enough Expression Language (FEEL) expressions, the full set of boxed expressions, and fully executable decision models.

{PRODUCT} provides design and runtime support for DMN 1.2 models at conformance level 3. You can design your DMN models directly in {CENTRAL} or import existing DMN models into your {PRODUCT} projects for deployment and execution.
