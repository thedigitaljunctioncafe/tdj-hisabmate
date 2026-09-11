package com.thedigitaljunction.tdjhisabmate

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.thedigitaljunction.tdjhisabmate.ui.screens.AboutPrivacyScreen
import com.thedigitaljunction.tdjhisabmate.ui.theme.TDJHisabMateTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class AboutScreenScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun about_screen_screenshot() {
    composeTestRule.setContent {
      TDJHisabMateTheme {
        AboutPrivacyScreen()
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/about_screen.png")
  }
}
