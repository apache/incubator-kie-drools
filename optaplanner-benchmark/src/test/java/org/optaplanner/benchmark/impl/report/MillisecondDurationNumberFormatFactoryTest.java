package org.optaplanner.benchmark.impl.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.benchmark.impl.report.MillisecondDurationNumberFormatFactory.MillisecondDurationNumberFormat;

import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

class MillisecondDurationNumberFormatFactoryTest {

    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private static final TemplateNumberFormat NUMBER_FORMAT = MillisecondDurationNumberFormat.INSTANCE;

    private final TemplateNumberModel templateNumberModel = mock(TemplateNumberModel.class);

    @BeforeAll
    static void setExpectedLocale() {
        Locale.setDefault(Locale.forLanguageTag("cs_CZ"));
    }

    @AfterAll
    static void resetDefaultLocale() {
        Locale.setDefault(DEFAULT_LOCALE);
    }

    @ParameterizedTest(name = "{0} milliseconds is formatted as ''{1}''")
    @CsvSource(delimiter = '|', nullValues = { "null" }, value = {
            "      null | None.                                ",
            "         0 | 0 ms.                                ",
            "      2089 | 2.089 s. (2,089 ms.)                 ",
            "    346089 | 05:46.089 s. (346,089 ms.)           ",
            "  19346089 | 05:22:26.089 s. (19,346,089 ms.)     ",
            " 119346089 | 01:09:09:06.089 s. (119,346,089 ms.) "
    })
    void millisecondFormatting(Long millis, String translation) throws TemplateValueFormatException, TemplateModelException {
        when(templateNumberModel.getAsNumber())
                .thenReturn(millis);
        String result = NUMBER_FORMAT.formatToPlainText(templateNumberModel);
        assertThat(result).isEqualTo(translation);
    }

}
