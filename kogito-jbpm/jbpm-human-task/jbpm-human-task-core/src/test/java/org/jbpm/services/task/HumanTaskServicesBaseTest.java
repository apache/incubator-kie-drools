/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.jbpm.test.listener.task.CountDownTaskEventListener;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.utils.MVELUtils;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HumanTaskServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(HumanTaskServicesBaseTest.class);
    
    protected InternalTaskService taskService;

    @BeforeClass
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }

    public void tearDown() {
        if( taskService != null ) {
            int removeAllTasks = taskService.removeAllTasks();
            logger.debug("Number of tasks removed {}", removeAllTasks);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map fillUsersOrGroups(String mvelFileName) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Reader reader = null;
        Map<String, Object> result = null;

        try {
            reader = new InputStreamReader(HumanTaskServicesBaseTest.class.getResourceAsStream(mvelFileName));
            result = (Map<String, Object>) MVELUtils.eval(reader, vars);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result;
    }

    protected final static String mySubject = "My Subject";
    protected final static String myBody = "My Body";

    protected static Map<String, String> fillMarshalSubjectAndBodyParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", mySubject);
        params.put("body", myBody);
        return params;
    }

    protected static void checkContentSubjectAndBody(Object unmarshalledObject) {
        assertTrue("Content is null.", unmarshalledObject != null && unmarshalledObject.toString() != null);
        String content = unmarshalledObject.toString();
        boolean match = false;
        if (("{body=" + myBody + ", subject=" + mySubject + "}").equals(content)
                || ("{subject=" + mySubject + ", body=" + myBody + "}").equals(content)) {
            match = true;
        }
        assertTrue("Content does not match.", match);
    }

    protected void printTestName() {
        logger.info("Running {}.{} ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Creates date using default format - "yyyy-MM-dd"
     */
    protected Date createDate(String dateString) {
        return createDate(dateString, "yyyy-MM-dd");
    }

    protected Date createDate(String dateString, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Can't create date from string '" + dateString + "' using '" + dateFormat + "' format!", e);
        }
    }

    protected JaxbContent xmlRoundTripContent(Content content) {
        JaxbContent xmlContent = new JaxbContent(content);
        JaxbContent xmlCopy = null;
        try {
            Marshaller marshaller = JAXBContext.newInstance(JaxbContent.class).createMarshaller();

            // marshal
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(xmlContent, stringWriter);

            // unmarshal
            Unmarshaller unmarshaller = JAXBContext.newInstance(JaxbContent.class).createUnmarshaller();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
            xmlCopy = (JaxbContent) unmarshaller.unmarshal(inputStream);

            for(Field field : JaxbContent.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object orig = field.get(xmlContent);
                Object roundTrip = field.get(xmlCopy);
                if( orig instanceof byte[] ) {
                    Assert.assertTrue(Arrays.equals((byte[]) orig, (byte[]) roundTrip));
                } else {
                    Assert.assertEquals(field.getName(), orig, roundTrip);
                }
            }
        } catch(Exception e) {
            logger.error("Unable to complete round trip: " + e.getMessage(), e );
            Assert.fail("Unable to complete round trip: " + e.getMessage());
        }

        Object orig = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertNotNull( "Round tripped JaxbContent is null!", xmlCopy );
        Object roundTrip = ContentMarshallerHelper.unmarshall(xmlCopy.getContent(), null);
        Assert.assertEquals(orig, roundTrip);

        return xmlCopy;
    }

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected static final String MAX_POOL_SIZE = "maxPoolSize";
    protected static final String ALLOW_LOCAL_TXS = "allowLocalTransactions";

    protected static final String DATASOURCE_CLASS_NAME = "className";
    protected static final String DRIVER_CLASS_NAME = "driverClassName";
    protected static final String USER = "user";
    protected static final String PASSWORD = "password";
    protected static final String JDBC_URL = "url";

    protected static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = getDatasourceProperties();
        PoolingDataSource pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        pds.init();

        return pds;
    }


    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     *
     * @return Properties containing the datasource properties.
     */
    private static Properties getDatasourceProperties() {
        boolean propertiesNotFound = false;

        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");

        InputStream propsInputStream = HumanTaskServicesBaseTest.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        } else {
            propertiesNotFound = true;
        }

        String password = props.getProperty("password");
        if ("${maven.jdbc.password}".equals(password) || propertiesNotFound) {
           logger.warn( "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]" );
           // If maven filtering somehow doesn't work the way it should..
           setDefaultProperties(props);
        }

        return props;
    }

    /**
     * Return the default database/datasource properties - These properties use
     * an in-memory H2 database
     *
     * This is used when the developer is somehow running the tests but
     * bypassing the maven filtering that's been turned on in the pom.
     *
     * @return Properties containing the default properties
     */
    private static void setDefaultProperties(Properties props) {
        String[] keyArr = {
                "serverName", "portNumber", "databaseName", JDBC_URL,
                USER, PASSWORD,
                DRIVER_CLASS_NAME, DATASOURCE_CLASS_NAME,
                MAX_POOL_SIZE, ALLOW_LOCAL_TXS };
        String[] defaultPropArr = {
                "", "", "", "jdbc:h2:mem:jbpm-db;MVCC=true",
                "sa", "",
                "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource",
                "5", "true" };
        Assert.assertTrue("Unequal number of keys for default properties", keyArr.length == defaultPropArr.length);
        for (int i = 0; i < keyArr.length; ++i) {
            if( ! props.containsKey(keyArr[i]) ) {
                props.put(keyArr[i], defaultPropArr[i]);
            }
        }
    }

    protected void addCountDownListner(CountDownTaskEventListener countDownListener) {
        if (taskService instanceof EventService) {
            ((EventService<TaskLifeCycleEventListener>) taskService).registerTaskEventListener(countDownListener);
        }
    }

    protected void removeCountDownListner(CountDownTaskEventListener countDownListener) {
        if (taskService instanceof EventService) {
            ((EventService<TaskLifeCycleEventListener>) taskService).removeTaskEventListener(countDownListener);
        }
    }
}
