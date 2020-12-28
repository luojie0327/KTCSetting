/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.ktc.setting.helper;

import com.ktc.setting.model.WifiItem;

import java.util.Comparator;

/**
 * Comparator that sorts Wifi scan results by signal strength and network name.
 */
public class WifiItemComparator implements Comparator<WifiItem> {

    /**
     * 把已连接的wifi放到第一位，其他按信号强度排序
     *
     * @param item1
     * @param item2
     * @return
     */
    @Override
    public int compare(WifiItem item1, WifiItem item2) {
        if (item1 == null) {
            if (item2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (item2 == null) {
                return -1;
            } else {
                int state1 = item1.getConnectedState();
                int state2 = item2.getConnectedState();
                if (state1 == WifiItem.STATE_CONNECTED) {
                    return -1;
                } else if (state2 == WifiItem.STATE_CONNECTED) {
                    return 1;
                }

                if (state1 == WifiItem.STATE_CONNECTING) {
                    return -1;
                } else if (state2 == WifiItem.STATE_CONNECTING) {
                    return 1;
                }

                if (item1.isSaved() && !item2.isSaved()) {
                    return -1;
                } else if (!item1.isSaved() && item2.isSaved()) {
                    return 1;
                }

                int levelDiff = item2.getLevel() - item1.getLevel();
                if (levelDiff != 0) {
                    return levelDiff;
                }

                if (item1.getSSID().equals(item2.getSSID())) {
                    return item2.getSecurity() - item1.getSecurity();
                }
                return item1.getSSID().compareTo(item2.getSSID());
            }
        }
    }
}
