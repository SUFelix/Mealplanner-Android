package com.felix.mealplanner20

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NavigationCrashTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateToAllBottomScreensWithoutCrash() {
        composeTestRule.onRoot().assertExists()

        val bottomNavTags = listOf(
            "bottom_nav_nutritioncockpit_screen",
            "bottom_nav_mealplan_screen",
           // "bottom_nav_profile_screen",
            "bottom_nav_shoppinglist_screen"
        )
        val screenTags = listOf(
            NUTRITION_COCKPIT_TEST_TAG,
            MEALPLAN_TEST_TAG,
           // PROFILE_TEST_TAG,
            SHOPPINGLIST_TEST_TAG
        )

        bottomNavTags.zip(screenTags).forEach { (navTag, screenTag) ->
            composeTestRule.onNodeWithTag(navTag).performClick()
            composeTestRule.onNodeWithTag(screenTag).assertExists()
        }
    }
}