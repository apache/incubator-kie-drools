package org.kie.drl.engine.mapinput.compilation.model.test;
public class DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C {

    public static final org.drools.model.DomainClassMetadata java_util_List_Metadata_INSTANCE = new java_util_List_Metadata();
    private static class java_util_List_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return java.util.List.class;
        }

        @Override
        public int getPropertiesSize() {
            return 1;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "empty": return 0;
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class interface java.util.List");
        }
    }

    public static final org.drools.model.DomainClassMetadata org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE = new org_kie_kogito_legacy_LoanApplication_Metadata();
    private static class org_kie_kogito_legacy_LoanApplication_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class;
        }

        @Override
        public int getPropertiesSize() {
            return 5;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "amount": return 0;
                case "applicant": return 1;
                case "approved": return 2;
                case "deposit": return 3;
                case "id": return 4;
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class class org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication");
        }
    }

    public static final org.drools.model.DomainClassMetadata java_lang_Integer_Metadata_INSTANCE = new java_lang_Integer_Metadata();
    private static class java_lang_Integer_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return Integer.class;
        }

        @Override
        public int getPropertiesSize() {
            return 0;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class class java.lang.Integer");
        }
    }

}