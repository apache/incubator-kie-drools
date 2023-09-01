package org.drools.scenariosimulation.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.util.ResourceHelper;
import org.kie.api.builder.Message;
import org.kie.dmn.api.core.DMNMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class used to provide commonly used method for test classes
 */
public class TestUtils {

    public static String getFileContent(String fileName) throws IOException {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filePath = ResourceHelper.getResourcesByExtension(extension)
                .stream()
                .filter(path -> path.endsWith(fileName))
                .findFirst()
                .orElse(null);
        assertThat(filePath).isNotNull();
        
        File sourceFile = new File(filePath);
        
        assertThat(sourceFile).exists();
        
        return new String(Files.readAllBytes(sourceFile.toPath()));
    }

    public static List<DMNMessage> getRandomlyGeneratedDMNMessageList() {
        return IntStream.range(0, 5).mapToObj(index -> {
            Message.Level level = Message.Level.values()[new Random().nextInt(Message.Level.values().length)];
            return createDMNMessageMock("dmnMessage-" + index, level);
        }).collect(Collectors.toList());
    }

    public static void commonCheckAuditLogLine(AuditLogLine toCheck, String expectedDecisionOrRuleName, String expectedResult, String expectedMessage) {
        assertThat(toCheck).isNotNull();
        assertThat(toCheck.getDecisionOrRuleName()).isEqualTo(expectedDecisionOrRuleName);
        assertThat(toCheck.getResult()).isEqualTo(expectedResult);
        assertThat(toCheck.getMessage().get()).isEqualTo(expectedMessage);
    }
    
    public static void commonCheckAuditLogLine(AuditLogLine toCheck, String expectedDecisionOrRuleName, String expectedResult) {
        assertThat(toCheck).isNotNull();
        assertThat(toCheck.getDecisionOrRuleName()).isEqualTo(expectedDecisionOrRuleName);
        assertThat(toCheck.getResult()).isEqualTo(expectedResult);
        assertThat(toCheck.getMessage()).isNotPresent();
    }
    

    private static DMNMessage createDMNMessageMock(String text, Message.Level level) {
        DMNMessage dmnMessageMock = mock(DMNMessage.class);
        when(dmnMessageMock.getText()).thenReturn(text);
        when(dmnMessageMock.getLevel()).thenReturn(level);
        return dmnMessageMock;
    }
}
