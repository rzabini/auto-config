package com.github.rzabini.config

import org.junit.rules.ExternalResource
import org.slf4j.Logger
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/* reference: http://stackoverflow.com/questions/25022453/unit-testing-of-a-class-with-staticloggerbinder */
public class ReplaceSlf4jLogger extends ExternalResource {
    Field logField
    Logger logger
    Logger originalLogger

    ReplaceSlf4jLogger(Class logClass, Logger logger) {
        String prefix = AutoConfig.canonicalName.replaceAll('\\.','_')
        logField = logClass.getDeclaredField("${prefix}__LOG");
        this.logger = logger
    }

    @Override
    protected void before() throws Throwable {
        logField.accessible = true

        Field modifiersField = Field.getDeclaredField("modifiers")
        modifiersField.accessible = true
        modifiersField.setInt(logField, logField.getModifiers() & ~Modifier.FINAL)

        originalLogger = (Logger) logField.get(null)
        logField.set(null, logger)
    }

    @Override
    protected void after() {
        logField.set(null, originalLogger)
    }

}