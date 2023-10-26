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

package com.android.systemui.qs.tiles.base.viewmodel

import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.plugins.FalsingManager
import com.android.systemui.qs.tiles.base.analytics.QSTileAnalytics
import com.android.systemui.qs.tiles.base.interactor.DisabledByPolicyInteractor
import com.android.systemui.qs.tiles.base.interactor.QSTileDataInteractor
import com.android.systemui.qs.tiles.base.interactor.QSTileDataToStateMapper
import com.android.systemui.qs.tiles.base.interactor.QSTileUserActionInteractor
import com.android.systemui.qs.tiles.base.logging.QSTileLogger
import com.android.systemui.qs.tiles.impl.di.QSTileComponent
import com.android.systemui.qs.tiles.viewmodel.QSTileConfig
import com.android.systemui.qs.tiles.viewmodel.QSTileState
import com.android.systemui.user.data.repository.UserRepository
import com.android.systemui.util.time.SystemClock
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Factory to create an appropriate [BaseQSTileViewModel] instance depending on your circumstances.
 *
 * @see [QSViewModelFactory.Component]
 * @see [QSViewModelFactory.Static]
 */
sealed interface QSViewModelFactory<T> {

    /**
     * This factory allows you to pass an instance of [QSTileComponent] to a view model effectively
     * binding them together. This achieves a DI scope that lives along the instance of
     * [BaseQSTileViewModel].
     */
    class Component<T>
    @Inject
    constructor(
        private val disabledByPolicyInteractor: DisabledByPolicyInteractor,
        private val userRepository: UserRepository,
        private val falsingManager: FalsingManager,
        private val qsTileAnalytics: QSTileAnalytics,
        private val qsTileLogger: QSTileLogger,
        private val systemClock: SystemClock,
        @Background private val backgroundDispatcher: CoroutineDispatcher,
    ) : QSViewModelFactory<T> {

        /**
         * Creates [BaseQSTileViewModel] based on the interactors obtained from [component].
         * Reference of that [component] is then stored along the view model.
         */
        fun create(component: QSTileComponent<T>): BaseQSTileViewModel<T> =
            BaseQSTileViewModel(
                component::config,
                component::userActionInteractor,
                component::dataInteractor,
                component::dataToStateMapper,
                disabledByPolicyInteractor,
                userRepository,
                falsingManager,
                qsTileAnalytics,
                qsTileLogger,
                systemClock,
                backgroundDispatcher,
            )
    }

    /**
     * This factory passes by necessary implementations to the [BaseQSTileViewModel]. This is a
     * default choice for most of the tiles.
     */
    class Static<T>
    @Inject
    constructor(
        private val disabledByPolicyInteractor: DisabledByPolicyInteractor,
        private val userRepository: UserRepository,
        private val falsingManager: FalsingManager,
        private val qsTileAnalytics: QSTileAnalytics,
        private val qsTileLogger: QSTileLogger,
        private val systemClock: SystemClock,
        @Background private val backgroundDispatcher: CoroutineDispatcher,
    ) : QSViewModelFactory<T> {

        /**
         * @param config contains all the static information (like TileSpec) about the tile.
         * @param userActionInteractor encapsulates user input processing logic. Use it to start
         *   activities, show dialogs or otherwise update the tile state.
         * @param tileDataInteractor provides [DATA_TYPE] and its availability.
         * @param mapper maps [DATA_TYPE] to the [QSTileState] that is then displayed by the View
         *   layer. It's called in [backgroundDispatcher], so it's safe to perform long running
         *   operations there.
         */
        fun create(
            config: QSTileConfig,
            userActionInteractor: QSTileUserActionInteractor<T>,
            tileDataInteractor: QSTileDataInteractor<T>,
            mapper: QSTileDataToStateMapper<T>,
        ): BaseQSTileViewModel<T> =
            BaseQSTileViewModel(
                { config },
                { userActionInteractor },
                { tileDataInteractor },
                { mapper },
                disabledByPolicyInteractor,
                userRepository,
                falsingManager,
                qsTileAnalytics,
                qsTileLogger,
                systemClock,
                backgroundDispatcher,
            )
    }
}
