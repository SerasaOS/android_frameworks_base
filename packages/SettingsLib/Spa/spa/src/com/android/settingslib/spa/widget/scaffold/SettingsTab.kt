/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.settingslib.spa.widget.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.settingslib.spa.framework.theme.SettingsShape
import com.android.settingslib.spa.framework.theme.SettingsTheme

@Composable
internal fun SettingsTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clip(SettingsShape.CornerMedium)
            .background(color = when {
                selected -> SettingsTheme.colorScheme.primaryContainer
                else -> SettingsTheme.colorScheme.surface
            }),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                selected -> SettingsTheme.colorScheme.onPrimaryContainer
                else -> SettingsTheme.colorScheme.secondaryText
            },
        )
    }
}

@Preview
@Composable
private fun SettingsTabPreview() {
    SettingsTheme {
        Column {
            SettingsTab(title = "Personal", selected = true) {}
            SettingsTab(title = "Work", selected = false) {}
        }
    }
}
