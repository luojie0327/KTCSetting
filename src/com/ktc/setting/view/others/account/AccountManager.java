package com.ktc.setting.view.others.account;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    private static final String TAG = "AccountManager";

    public static List<AccountBean> getAccounts(Context context) {
        android.accounts.AccountManager am = android.accounts.AccountManager.get(context);
        AuthenticatorDescription[] authTypes = am.getAuthenticatorTypes();
        PackageManager pm = context.getPackageManager();
        List<AccountBean> accountBeans = new ArrayList<AccountBean>();

        for (AuthenticatorDescription authDesc : authTypes) {
            final Resources resources;
            try {
                resources = pm.getResourcesForApplication(authDesc.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Authenticator description with bad package name", e);
                continue;
            }

            // Main title text comes from the authenticator description (e.g. "Google").
            String authTitle = null;
            try {
                authTitle = resources.getString(authDesc.labelId);
                if (TextUtils.isEmpty(authTitle)) {
                    authTitle = null;  // Handled later when we add the row.
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Authenticator description with bad label id", e);
            }

            Account[] accounts = am.getAccountsByType(authDesc.type);
            if (accounts == null || accounts.length == 0) {
                continue;  // No point in continuing; there aren't any accounts to show.
            }

            Uri imageUri = null;
            try {
                imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        authDesc.packageName + '/' +
                        resources.getResourceTypeName(authDesc.iconId) + '/' +
                        resources.getResourceEntryName(authDesc.iconId));
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Authenticator has bad resource ids", e);
            }

            // Display an entry for each installed account we have.
            for (final Account account : accounts) {
                AccountBean accountBean = new AccountBean(authTitle, imageUri, account);
                accountBeans.add(accountBean);
            }
        }

        return accountBeans;
    }
}
