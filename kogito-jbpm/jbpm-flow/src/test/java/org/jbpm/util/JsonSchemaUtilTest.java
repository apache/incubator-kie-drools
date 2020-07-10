package org.jbpm.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaUtilTest {
    
    
    private final static String example ="{\"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" + 
            "    \"type\": \"object\",\n" + 
            "    \"properties\": {\n" + 
            "        \"traveller\": {\n" + 
            "            \"type\": \"object\",\n" + 
            "            \"properties\": {\n" + 
            "                \"address\": {\n" + 
            "                    \"type\": \"object\",\n" + 
            "                    \"properties\": {\n" + 
            "                        \"city\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"country\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"street\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        },\n" + 
            "                        \"zipCode\": {\n" + 
            "                            \"type\": \"string\"\n" + 
            "                        }\n" + 
            "                    }\n" + 
            "                },\n" + 
            "                \"email\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"firstName\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"lastName\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                },\n" + 
            "                \"nationality\": {\n" + 
            "                    \"type\": \"string\"\n" + 
            "                }\n" + 
            "            },\n" + 
            "            \"input\": true\n" + 
            "        },\n" + 
            "        \"approved\": {\n" + 
            "            \"type\": \"boolean\",\n" + 
            "            \"output\": true\n" + 
            "        }\n" + 
            "    }}";
    
    @Test
    void testJsonSchema() throws IOException {
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Map<String,Object> schemaMap = JsonSchemaUtil.load(in);
        assertEquals ("object", schemaMap.get("type"));
        Map<String,Object> properties = (Map<String,Object>)schemaMap.get("properties");
        assertEquals(2,properties.size());
        assertTrue((Boolean)((Map)properties.get("approved")).get("output"));
        assertTrue((Boolean)((Map)properties.get("traveller")).get("input"));
    }
}
