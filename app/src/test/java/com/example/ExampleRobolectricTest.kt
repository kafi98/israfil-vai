package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.HijriCalendarUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("আমার পরিকল্পনা", appName)
    }

    @Test
    fun `test hijri date calculation`() {
        val hijri = HijriCalendarUtil.calculateHijri(2026, 10, 13)
        assertNotNull(hijri.monthName)
        val formatted = HijriCalendarUtil.formatDateLine(2026, 10, 13)
        assertNotNull(formatted)
    }
}
