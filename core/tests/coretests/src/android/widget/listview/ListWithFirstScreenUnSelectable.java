/*
 * Copyright (C) 2008 Google Inc.
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

package android.widget.listview;

import android.widget.ListScenario;

/**
 * The first item is unselectable, and takes up the whole screen.
 */
public class ListWithFirstScreenUnSelectable extends ListScenario {

    @Override
    protected void init(Params params) {
        params.setItemScreenSizeFactor(1.2)
                .setNumItems(2)
                .setPositionsUnselectable(0);

    }
}
