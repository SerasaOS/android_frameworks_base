/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android.systemui.keyguard.shared.model

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

/** Determines the constraints for the ConstraintSet in the lockscreen root view. */
interface KeyguardBlueprint {
    val id: String
    val sections: Array<KeyguardSection>

    fun addViews(constraintLayout: ConstraintLayout) {
        sections.forEach { it.addViews(constraintLayout) }
    }

    fun applyConstraints(constraintSet: ConstraintSet) {
        sections.forEach { it.applyConstraints(constraintSet) }
    }

    fun onDestroy() {
        sections.forEach { it.onDestroy() }
    }
}
