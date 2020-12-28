package com.ktc.setting.view.universal.datetime.timezone;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ZoneUtil {

    public static ArrayList<String> getZoneInfoList(Context context) {
        ArrayList<String> zones = new ArrayList<>();
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(context.getApplicationContext());
        for (Map<String, Object> zone : zoneList) {
            if (zone.get(ZoneGetter.KEY_LONG_DISPLAY_NAME) == null){
                zones.add(zone.get(ZoneGetter.KEY_DISPLAYNAME) + "(" + zone.get(ZoneGetter.KEY_DISPLAYNAME) + ")"
                    + "\n" + zone.get(ZoneGetter.KEY_GMT));
            } else {
                zones.add(zone.get(ZoneGetter.KEY_LONG_DISPLAY_NAME) + "(" + zone.get(ZoneGetter.KEY_DISPLAYNAME) + ")"
                    + "\n" + zone.get(ZoneGetter.KEY_GMT));
            }
        }
        return zones;
    }

    public static ArrayList<String> getZoneIdList(Context context) {
        ArrayList<String> zones = new ArrayList<>();
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(context.getApplicationContext());
        for (Map<String, Object> zone : zoneList) {
            zones.add((String) zone.get(ZoneGetter.KEY_ID));
        }
        return zones;
    }

    public static String getCurrentZoneInfo(Context context) {
        TimeZone tz = TimeZone.getDefault();
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(context.getApplicationContext());
        for (Map<String, Object> zone : zoneList) {
            if (zone.get(ZoneGetter.KEY_ID).equals(tz.getID())) {
                return zone.get(ZoneGetter.KEY_DISPLAYNAME) + " " + zone.get(ZoneGetter.KEY_GMT);
            }
        }
        return null;
    }

    public static String getCurrentZoneInfoForSubtitle(Context context) {
        TimeZone tz = TimeZone.getDefault();
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(context.getApplicationContext());
        for (Map<String, Object> zone : zoneList) {
            if (zone.get(ZoneGetter.KEY_ID).equals(tz.getID())) {
                return zone.get(ZoneGetter.KEY_DISPLAYNAME) + "\n" + zone.get(ZoneGetter.KEY_GMT);
            }
        }
        return null;
    }

    public static int getCurrentZoneIndex(Context context) {
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(context.getApplicationContext());
        TimeZone tz = TimeZone.getDefault();
        for (int i = 0; i < zoneList.size(); i++) {
            if (zoneList.get(i).get(ZoneGetter.KEY_ID).equals(tz.getID())) {
                return i;
            }
        }
        return 0;
    }
}
