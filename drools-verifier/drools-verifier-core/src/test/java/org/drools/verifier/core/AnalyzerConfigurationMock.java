package org.drools.verifier.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.verifier.core.checks.base.JavaCheckRunner;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.configuration.DateTimeFormatProvider;
import org.drools.verifier.core.index.keys.UUIDKeyProvider;

public class AnalyzerConfigurationMock
        extends AnalyzerConfiguration {

    public AnalyzerConfigurationMock() {
        this(CheckConfiguration.newDefault());
    }

    public AnalyzerConfigurationMock(final CheckConfiguration checkConfiguration) {
        super("UUID",
              new DateTimeFormatProvider() {
                  @Override
                  public String format(final Date dateValue) {
                      return new SimpleDateFormat("dd-MMM-yyyy").format(dateValue);
                  }

                  @Override
                  public Date parse(String dateValue) {
                      try {
                          return new SimpleDateFormat("dd-MMM-yyyy").parse(dateValue);
                      } catch (ParseException e) {
                          return null;
                      }
                  }
              },
              new UUIDKeyProvider() {

                  private long index = Long.SIZE;

                  @Override
                  protected String newUUID() {
                      return Long.toString(index--);
                  }
              },
              checkConfiguration,
              new JavaCheckRunner());
    }
}
