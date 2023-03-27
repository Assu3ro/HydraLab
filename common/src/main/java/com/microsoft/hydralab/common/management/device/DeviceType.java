// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.hydralab.common.management.device;

import java.util.Set;

public enum DeviceType {
    // Define device type and bean name
    ANDROID {
        @Override
        public Set<String> getSuffixes() {
            return Set.of("apk");
        }

        @Override
        public String getDriverName() {
            return null;
        }
    },
    WINDOWS {
        @Override
        public Set<String> getSuffixes() {
            return Set.of("appx", "appxbundle");
        }

        @Override
        public String getDriverName() {
            return null;
        }
    },
    IOS {
        @Override
        public Set<String> getSuffixes() {
            return Set.of("ipa", "app");
        }

        @Override
        public String getDriverName() {
            return null;
        }
    };

    public abstract Set<String> getSuffixes();
    public abstract String getDriverName();
}