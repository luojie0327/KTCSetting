package com.ktc.setting.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WifiItem implements Parcelable {

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private String mSSID;
    private String mBSSID;
    private int mSecurity;
    private int mLevel;
    private int mConnectedState;
    private boolean mIsSaved;

    public WifiItem(String SSID, String BSSID, int security, int level, int connectedState, boolean isSaved) {
        mSSID = SSID;
        mBSSID = BSSID;
        mSecurity = security;
        mLevel = level;
        mConnectedState = connectedState;
        mIsSaved = isSaved;
    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String SSID) {
        mSSID = SSID;
    }

    public String getBSSID() {
        return mBSSID;
    }

    public void setBSSID(String BSSID) {
        mBSSID = BSSID;
    }

    public int getSecurity() {
        return mSecurity;
    }

    public void setSecurity(int security) {
        mSecurity = security;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public int getConnectedState() {
        return mConnectedState;
    }

    public void setConnectedState(int connectedState) {
        mConnectedState = connectedState;
    }

    public boolean isSaved() {
        return mIsSaved;
    }

    public void setSaved(boolean saved) {
        mIsSaved = saved;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof WifiItem))
            return false;

        WifiItem item = (WifiItem) obj;
        if (item.mSSID.equals(mSSID)
                && item.mBSSID.equals(mBSSID)
                && item.mLevel == mLevel
                && item.mSecurity == mSecurity) {
            return true;
        }
        return false;
    }

    public static final Parcelable.Creator<WifiItem> CREATOR = new Creator<WifiItem>() {

        @Override
        public WifiItem createFromParcel(Parcel source) {
            return new WifiItem(source.readString(),
                    source.readString(),
                    source.readInt(),
                    source.readInt(),
                    source.readInt(),
                    source.readByte() != 0);
        }

        @Override
        public WifiItem[] newArray(int size) {
            return new WifiItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSSID);
        dest.writeString(mBSSID);
        dest.writeInt(mSecurity);
        dest.writeInt(mLevel);
        dest.writeInt(mConnectedState);
        dest.writeByte((byte) (mIsSaved ? 1 : 0));
    }
}
