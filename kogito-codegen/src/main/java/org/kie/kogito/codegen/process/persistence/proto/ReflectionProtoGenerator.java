package org.kie.kogito.codegen.process.persistence.proto;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.kie.internal.kogito.codegen.Generated;

public class ReflectionProtoGenerator implements ProtoGenerator<Class<?>> {

    public Proto generate(String packageName, Collection<Class<?>> dataModel, String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);
            for (Class<?> clazz : dataModel) {
                messageFromClass(proto, clazz, null, null, null);
            }
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for data model", e);
        }
    }
    

    @Override
    public Proto generate(String messageComment, String fieldComment, String packageName, Class<?> dataModel, String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);
            messageFromClass(proto, dataModel, packageName, messageComment, fieldComment);        
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for model class " + dataModel, e);
        }
    }

    public Collection<Class<?>> extractDataClasses(Collection<Class<?>> input, String targetDirectory) {

        Set<Class<?>> dataModelClasses = new HashSet<>();
        for (Class<?> modelClazz : input) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(modelClazz);
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    Class<?> propertyType = pd.getPropertyType();
                    if (propertyType.getCanonicalName().startsWith("java.lang") 
                            || propertyType.getCanonicalName().equals(Date.class.getCanonicalName())) {
                        continue;
                    }

                    dataModelClasses.add(propertyType);
                }

                generateModelClassProto(modelClazz, targetDirectory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return dataModelClasses;
    }

    protected ProtoMessage messageFromClass(Proto proto, Class<?> clazz, String packageName, String messageComment, String fieldComment) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        String name = beanInfo.getBeanDescriptor().getBeanClass().getSimpleName();
        Generated generatedData = clazz.getAnnotation(Generated.class);
        if (generatedData != null) {
            name = generatedData.name().isEmpty() ? name : generatedData.name();
        }

        ProtoMessage message = new ProtoMessage(name, packageName == null ? clazz.getPackage().getName() : packageName);

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getName().equals("class")) {
                continue;
            }
            String fieldTypeString = pd.getPropertyType().getCanonicalName();
            Class<?> fieldType = pd.getPropertyType();
            String protoType;
            if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                fieldTypeString = "Collection";
                Field f = clazz.getDeclaredField(pd.getName());
                Type type = f.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    fieldType = (Class<?>) ptype.getActualTypeArguments()[0];
                    protoType = protoType(fieldType.getCanonicalName());
                } else {
                    throw new IllegalArgumentException("Field " + f.getName() + " of class " + clazz + " uses collection without type information");
                }
            } else {
                protoType = protoType(fieldTypeString);
            }

            if (protoType == null) {
                ProtoMessage another = messageFromClass(proto, fieldType, packageName, messageComment, fieldComment);
                protoType = another.getName();
            }

            message.addField(applicabilityByType(fieldTypeString), protoType, pd.getName()).setComment(fieldComment);
        }
        message.setComment(messageComment);
        proto.addMessage(message);
        return message;
    }
    
    protected void generateModelClassProto(Class<?> modelClazz, String targetDirectory) throws Exception {

        Generated generatedData = modelClazz.getAnnotation(Generated.class);
        if (generatedData != null) {

            String processId = generatedData.reference();            
            Proto modelProto = generate("@Indexed",
                                        INDEX_COMMENT,
                                        modelClazz.getPackage().getName() + "." + processId, modelClazz, "import \"kogito-index.proto\";",                                        
                                        "import \"kogito-types.proto\";", 
                                        "option kogito_model = \"" + generatedData.name() +"\";", 
                                        "option kogito_id = \"" + processId +"\";");
            ProtoMessage modelMessage = modelProto.getMessages().stream().filter(msg -> msg.getName().equals(generatedData.name())).findFirst().orElseThrow(() -> new IllegalStateException("Unable to find model message"));
            modelMessage.addField("optional", "org.kie.kogito.index.model.KogitoMetadata", "metadata").setComment(INDEX_COMMENT);
            
            Path protoFilePath = Paths.get(targetDirectory, "classes", "/persistence/" + processId + ".proto");

            Files.createDirectories(protoFilePath.getParent());
            Files.write(protoFilePath, modelProto.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

}
