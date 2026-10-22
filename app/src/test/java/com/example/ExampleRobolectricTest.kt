package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DrivingTelemetry
import com.example.data.model.InstalledAppItem
import com.example.util.InstalledAppsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AutoDrive", appName)
  }

  @Test
  fun `telemetry speed warning detects speeding`() {
    val normalTelemetry = DrivingTelemetry(currentSpeedKmh = 75, speedLimitKmh = 80)
    assertFalse(normalTelemetry.currentSpeedKmh > normalTelemetry.speedLimitKmh)

    val speedingTelemetry = DrivingTelemetry(currentSpeedKmh = 95, speedLimitKmh = 80, isSpeedWarning = true)
    assertTrue(speedingTelemetry.currentSpeedKmh > speedingTelemetry.speedLimitKmh)
    assertTrue(speedingTelemetry.isSpeedWarning)
  }

  @Test
  fun `installed apps manager provides builtin apps and saves preferences`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = InstalledAppsManager(context)
    val apps = manager.getInstalledAndBuiltinApps()

    assertTrue(apps.isNotEmpty())
    val builtinMaps = apps.find { it.id == InstalledAppsManager.BUILTIN_MAPS.id }
    assertNotNull(builtinMaps)

    manager.savePane1AppId("test_app_id")
    assertEquals("test_app_id", manager.getSavedPane1AppId())
  }
}
