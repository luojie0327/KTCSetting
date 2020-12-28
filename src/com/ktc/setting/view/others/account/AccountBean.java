package com.ktc.setting.view.others.account;

import android.accounts.Account;
import android.net.Uri;

public class AccountBean {
    private String authTitle;
    private Uri iconUri;
    private Account account;

    public AccountBean(String authTitle, Uri uri, Account account) {
        this.authTitle = authTitle;
        this.iconUri = uri;
        this.account = account;
    }


    public String getAuthTitle() {
        return authTitle;
    }

    public void setAuthTitle(String authTitle) {
        this.authTitle = authTitle;
    }

    public Uri getIconUri() {
        return iconUri;
    }

    public void setIconUri(Uri iconUri) {
        this.iconUri = iconUri;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
