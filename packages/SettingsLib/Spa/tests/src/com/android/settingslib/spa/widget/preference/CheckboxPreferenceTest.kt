/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.spa.widget.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckboxPreferenceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun title_displayed() {
        composeTestRule.setContent {
            testCheckboxPreference(changeable = true)
        }

        composeTestRule.onNodeWithText("CheckboxPreference").assertIsDisplayed()
    }

    @Test
    fun toggleable_initialStateIsCorrect() {
        composeTestRule.setContent {
            testCheckboxPreference(changeable = true)
        }

        composeTestRule.onNode(isToggleable()).assertIsOff()
    }

    @Test
    fun click_changeable_withEffect() {
        composeTestRule.setContent {
            testCheckboxPreference(changeable = true)
        }

        composeTestRule.onNodeWithText("CheckboxPreference").performClick()
        composeTestRule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun click_notChangeable_noEffect() {
        composeTestRule.setContent {
            testCheckboxPreference(changeable = false)
        }

        composeTestRule.onNodeWithText("CheckboxPreference").performClick()
        composeTestRule.onNode(isToggleable()).assertIsOff()
    }
}

@Composable
private fun testCheckboxPreference(changeable: Boolean) {
    var checked by rememberSaveable { mutableStateOf(false) }
    CheckboxPreference(remember {
        object : CheckboxPreferenceModel {
            override val title = "CheckboxPreference"
            override val checked = { checked }
            override val changeable = { changeable }
            override val onCheckedChange = { newChecked: Boolean -> checked = newChecked }
        }
    })
}