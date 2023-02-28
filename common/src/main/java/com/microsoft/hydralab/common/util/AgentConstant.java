// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.hydralab.common.util;

import java.util.HashMap;
import java.util.Map;

public class AgentConstant {
    public static final String UNKNOWN_IOS_MODEL = "Unknown iOS Device";
    public static final Map<String, String> iOSProductModelMap = new HashMap<String, String>() {{
        put("i386", "iPhone Simulator");
        put("x86_64", "iPhone Simulator");
        put("arm64", "iPhone Simulator");

        put("iPhone1,1", "iPhone");
        put("iPhone1,2", "iPhone 3G");
        put("iPhone2,1", "iPhone 3GS");
        put("iPhone3,1", "iPhone 4");
        put("iPhone3,2", "iPhone 4 GSM Rev A");
        put("iPhone3,3", "iPhone 4 CDMA");
        put("iPhone4,1", "iPhone 4S");
        put("iPhone5,1", "iPhone 5 (GSM)");
        put("iPhone5,2", "iPhone 5 (GSM+CDMA)");
        put("iPhone5,3", "iPhone 5C (GSM)");
        put("iPhone5,4", "iPhone 5C (Global)");
        put("iPhone6,1", "iPhone 5S (GSM)");
        put("iPhone6,2", "iPhone 5S (Global)");
        put("iPhone7,1", "iPhone 6 Plus");
        put("iPhone7,2", "iPhone 6");
        put("iPhone8,1", "iPhone 6s");
        put("iPhone8,2", "iPhone 6s Plus");
        put("iPhone8,4", "iPhone SE (GSM)");
        put("iPhone9,1", "iPhone 7");
        put("iPhone9,2", "iPhone 7 Plus");
        put("iPhone9,3", "iPhone 7");
        put("iPhone9,4", "iPhone 7 Plus");
        put("iPhone10,1", "iPhone 8");
        put("iPhone10,2", "iPhone 8 Plus");
        put("iPhone10,3", "iPhone X Global");
        put("iPhone10,4", "iPhone 8");
        put("iPhone10,5", "iPhone 8 Plus");
        put("iPhone10,6", "iPhone X GSM");
        put("iPhone11,2", "iPhone XS");
        put("iPhone11,4", "iPhone XS Max");
        put("iPhone11,6", "iPhone XS Max Global");
        put("iPhone11,8", "iPhone XR");
        put("iPhone12,1", "iPhone 11");
        put("iPhone12,3", "iPhone 11 Pro");
        put("iPhone12,5", "iPhone 11 Pro Max");
        put("iPhone12,8", "iPhone SE 2nd Gen");
        put("iPhone13,1", "iPhone 12 Mini");
        put("iPhone13,2", "iPhone 12");
        put("iPhone13,3", "iPhone 12 Pro");
        put("iPhone13,4", "iPhone 12 Pro Max");
        put("iPhone14,2", "iPhone 13 Pro");
        put("iPhone14,3", "iPhone 13 Pro Max");
        put("iPhone14,4", "iPhone 13 Mini");
        put("iPhone14,5", "iPhone 13");
        put("iPhone14,6", "iPhone SE 3rd Gen");

        put("iPod1,1", "1st Gen iPod");
        put("iPod2,1", "2nd Gen iPod");
        put("iPod3,1", "3rd Gen iPod");
        put("iPod4,1", "4th Gen iPod");
        put("iPod5,1", "5th Gen iPod");
        put("iPod7,1", "6th Gen iPod");
        put("iPod9,1", "7th Gen iPod");

        put("iPad1,1", "iPad");
        put("iPad1,2", "iPad 3G");
        put("iPad2,1", "2nd Gen iPad");
        put("iPad2,2", "2nd Gen iPad GSM");
        put("iPad2,3", "2nd Gen iPad CDMA");
        put("iPad2,4", "2nd Gen iPad New Revision");
        put("iPad3,1", "3rd Gen iPad");
        put("iPad3,2", "3rd Gen iPad CDMA");
        put("iPad3,3", "3rd Gen iPad GSM");
        put("iPad2,5", "iPad mini");
        put("iPad2,6", "iPad mini GSM+LTE");
        put("iPad2,7", "iPad mini CDMA+LTE");
        put("iPad3,4", "4th Gen iPad");
        put("iPad3,5", "4th Gen iPad GSM+LTE");
        put("iPad3,6", "4th Gen iPad CDMA+LTE");
        put("iPad4,1", "iPad Air (WiFi)");
        put("iPad4,2", "iPad Air (GSM+CDMA)");
        put("iPad4,3", "1st Gen iPad Air (China)");
        put("iPad4,4", "iPad mini Retina (WiFi)");
        put("iPad4,5", "iPad mini Retina (GSM+CDMA)");
        put("iPad4,6", "iPad mini Retina (China)");
        put("iPad4,7", "iPad mini 3 (WiFi)");
        put("iPad4,8", "iPad mini 3 (GSM+CDMA)");
        put("iPad4,9", "iPad Mini 3 (China)");
        put("iPad5,1", "iPad mini 4 (WiFi)");
        put("iPad5,2", "4th Gen iPad mini (WiFi+Cellular)");
        put("iPad5,3", "iPad Air 2 (WiFi)");
        put("iPad5,4", "iPad Air 2 (Cellular)");
        put("iPad6,3", "iPad Pro (9.7 inch, WiFi)");
        put("iPad6,4", "iPad Pro (9.7 inch, WiFi+LTE)");
        put("iPad6,7", "iPad Pro (12.9 inch, WiFi)");
        put("iPad6,8", "iPad Pro (12.9 inch, WiFi+LTE)");
        put("iPad6,11", "iPad (2017)");
        put("iPad6,12", "iPad (2017)");
        put("iPad7,1", "iPad Pro 2nd Gen (WiFi)");
        put("iPad7,2", "iPad Pro 2nd Gen (WiFi+Cellular)");
        put("iPad7,3", "iPad Pro 10.5-inch 2nd Gen");
        put("iPad7,4", "iPad Pro 10.5-inch 2nd Gen");
        put("iPad7,5", "iPad 6th Gen (WiFi)");
        put("iPad7,6", "iPad 6th Gen (WiFi+Cellular)");
        put("iPad7,11", "iPad 7th Gen 10.2-inch (WiFi)");
        put("iPad7,12", "iPad 7th Gen 10.2-inch (WiFi+Cellular)");
        put("iPad8,1", "iPad Pro 11 inch 3rd Gen (WiFi)");
        put("iPad8,2", "iPad Pro 11 inch 3rd Gen (1TB, WiFi)");
        put("iPad8,3", "iPad Pro 11 inch 3rd Gen (WiFi+Cellular)");
        put("iPad8,4", "iPad Pro 11 inch 3rd Gen (1TB, WiFi+Cellular)");
        put("iPad8,5", "iPad Pro 12.9 inch 3rd Gen (WiFi)");
        put("iPad8,6", "iPad Pro 12.9 inch 3rd Gen (1TB, WiFi)");
        put("iPad8,7", "iPad Pro 12.9 inch 3rd Gen (WiFi+Cellular)");
        put("iPad8,8", "iPad Pro 12.9 inch 3rd Gen (1TB, WiFi+Cellular)");
        put("iPad8,9", "iPad Pro 11 inch 4th Gen (WiFi)");
        put("iPad8,10", "iPad Pro 11 inch 4th Gen (WiFi+Cellular)");
        put("iPad8,11", "iPad Pro 12.9 inch 4th Gen (WiFi)");
        put("iPad8,12", "iPad Pro 12.9 inch 4th Gen (WiFi+Cellular)");
        put("iPad11,1", "iPad mini 5th Gen (WiFi)");
        put("iPad11,2", "iPad mini 5th Gen");
        put("iPad11,3", "iPad Air 3rd Gen (WiFi)");
        put("iPad11,4", "iPad Air 3rd Gen");
        put("iPad11,6", "iPad 8th Gen (WiFi)");
        put("iPad11,7", "iPad 8th Gen (WiFi+Cellular)");
        put("iPad12,1", "iPad 9th Gen (WiFi)");
        put("iPad12,2", "iPad 9th Gen (WiFi+Cellular)");
        put("iPad14,1", "iPad mini 6th Gen (WiFi)");
        put("iPad14,2", "iPad mini 6th Gen (WiFi+Cellular)");
        put("iPad13,1", "iPad Air 4th Gen (WiFi)");
        put("iPad13,2", "iPad Air 4th Gen (WiFi+Cellular)");
        put("iPad13,4", "iPad Pro 11 inch 5th Gen");
        put("iPad13,5", "iPad Pro 11 inch 5th Gen");
        put("iPad13,6", "iPad Pro 11 inch 5th Gen");
        put("iPad13,7", "iPad Pro 11 inch 5th Gen");
        put("iPad13,8", "iPad Pro 12.9 inch 5th Gen");
        put("iPad13,9", "iPad Pro 12.9 inch 5th Gen");
        put("iPad13,10", "iPad Pro 12.9 inch 5th Gen");
        put("iPad13,11", "iPad Pro 12.9 inch 5th Gen");
        put("iPad13,16", "iPad Air 5th Gen (WiFi)");
        put("iPad13,17", "iPad Air 5th Gen (WiFi+Cellular)");

        put("Watch1,1", "Apple Watch 38mm case");
        put("Watch1,2", "Apple Watch 42mm case");
        put("Watch2,6", "Apple Watch Series 1 38mm case");
        put("Watch2,7", "Apple Watch Series 1 42mm case");
        put("Watch2,3", "Apple Watch Series 2 38mm case");
        put("Watch2,4", "Apple Watch Series 2 42mm case");
        put("Watch3,1", "Apple Watch Series 3 38mm case (GPS+Cellular)");
        put("Watch3,2", "Apple Watch Series 3 42mm case (GPS+Cellular)");
        put("Watch3,3", "Apple Watch Series 3 38mm case (GPS)");
        put("Watch3,4", "Apple Watch Series 3 42mm case (GPS)");
        put("Watch4,1", "Apple Watch Series 4 40mm case (GPS)");
        put("Watch4,2", "Apple Watch Series 4 44mm case (GPS)");
        put("Watch4,3", "Apple Watch Series 4 40mm case (GPS+Cellular)");
        put("Watch4,4", "Apple Watch Series 4 44mm case (GPS+Cellular)");
        put("Watch5,1", "Apple Watch Series 5 40mm case (GPS)");
        put("Watch5,2", "Apple Watch Series 5 44mm case (GPS)");
        put("Watch5,3", "Apple Watch Series 5 40mm case (GPS+Cellular)");
        put("Watch5,4", "Apple Watch Series 5 44mm case (GPS+Cellular)");
        put("Watch5,9", "Apple Watch SE 40mm case (GPS)");
        put("Watch5,10", "Apple Watch SE 44mm case (GPS)");
        put("Watch5,11", "Apple Watch SE 40mm case (GPS+Cellular)");
        put("Watch5,12", "Apple Watch SE 44mm case (GPS+Cellular)");
        put("Watch6,1", "Apple Watch Series 6 40mm case (GPS)");
        put("Watch6,2", "Apple Watch Series 6 44mm case (GPS)");
        put("Watch6,3", "Apple Watch Series 6 40mm case (GPS+Cellular)");
        put("Watch6,4", "Apple Watch Series 6 44mm case (GPS+Cellular)");
        put("Watch6,6", "Apple Watch Series 7 41mm case (GPS)");
        put("Watch6,7", "Apple Watch Series 7 45mm case (GPS)");
        put("Watch6,8", "Apple Watch Series 7 41mm case (GPS+Cellular)");
        put("Watch6,9", "Apple Watch Series 7 45mm case (GPS+Cellular)");
    }};
}
