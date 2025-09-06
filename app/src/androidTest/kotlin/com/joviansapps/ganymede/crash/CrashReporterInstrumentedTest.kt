package com.joviansapps.ganymede.crash

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrashReporterInstrumentedTest {
    @Before
    fun setup() {
        // clear buffer before each test
        CrashReporter.clearBufferedReports()
    }

    @Test
    fun crashReporter_disabled_doesNotRecord() {
        CrashReporter.updateEnabled(false)
        CrashReporter.logMessage("Should not record")
        CrashReporter.log(RuntimeException("should not record"), message = "Should not record")
        val reports = CrashReporter.getBufferedReports()
        assertTrue("Buffer should be empty when disabled", reports.isEmpty())
    }

    @Test
    fun crashReporter_enabled_recordsMessages() {
        CrashReporter.updateEnabled(true)
        CrashReporter.clearBufferedReports()
        CrashReporter.logMessage("Test message")
        CrashReporter.log(RuntimeException("Test ex"), message = "Test crash")
        val reports = CrashReporter.getBufferedReports()
        assertEquals(2, reports.size)
        assertTrue(reports[0].startsWith("MSG:"))
        assertTrue(reports[1].startsWith("CRASH:"))
    }
}
