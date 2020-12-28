package com.ktc.setting.view.about;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.view.about.internal.LicenseHtmlLoader;
import com.ktc.setting.view.custom.ToastFactory;

import java.io.File;

public class LicenseManager implements LoaderManager.LoaderCallbacks<File> {

    private static final String TAG = LicenseManager.class.getSimpleName();
    private static final String DEFAULT_LICENSE_PATH = "/system/etc/NOTICE.html.gz";
    private static final String PROPERTY_LICENSE_PATH = "ro.config.license_path";
    private static final String FILE_PROVIDER_AUTHORITY = "com.android.settings.files";
    private static final int LOADER_ID_LICENSE_HTML_LOADER = 0;

    private Context mContext;

    public LicenseManager(Context context) {
        mContext = context;
    }

    public void startLicense() {
        final String licenseHtmlPath =
                SystemProperties.get(PROPERTY_LICENSE_PATH, DEFAULT_LICENSE_PATH);
        if (isFilePathValid(licenseHtmlPath)) {
            showSelectedFile(licenseHtmlPath);
        } else {
            showHtmlFromDefaultXmlFiles();
        }
    }

    private void showHtmlFromDefaultXmlFiles() {
        ((Activity) mContext).getLoaderManager().initLoader(LOADER_ID_LICENSE_HTML_LOADER, Bundle.EMPTY, this);
    }

    private void showSelectedFile(final String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "The system property for the license file is empty");
            showError();
            return;
        }

        final File file = new File(path);
        if (!isFileValid(file)) {
            Log.e(TAG, "License file " + path + " does not exist");
            showError();
            return;
        }
        showHtmlFromUri(Uri.fromFile(file));
    }

    private void showHtmlFromUri(Uri uri) {
        // Kick off external viewer due to WebView security restrictions; we
        // carefully point it at HTMLViewer, since it offers to decompress
        // before viewing.
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/html");
        intent.putExtra(Intent.EXTRA_TITLE, mContext.getString(R.string.str_about_legal_license));
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage("com.android.htmlviewer");

        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to find viewer", e);
            showError();
        }
    }

    private boolean isFilePathValid(final String path) {
        return !TextUtils.isEmpty(path) && isFileValid(new File(path));
    }

    @VisibleForTesting
    boolean isFileValid(final File file) {
        return file.exists() && file.length() != 0;
    }

    private void showError() {
        ToastFactory.showToast(mContext, mContext.getString(R.string.str_about_license_activity_unavailable)
                , Toast.LENGTH_SHORT);
    }

    @VisibleForTesting
    Uri getUriFromGeneratedHtmlFile(File generatedHtmlFile) {
        return FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, generatedHtmlFile);
    }

    private void showGeneratedHtmlFile(File generatedHtmlFile) {
        if (generatedHtmlFile != null) {
            showHtmlFromUri(getUriFromGeneratedHtmlFile(generatedHtmlFile));
        } else {
            Log.e(TAG, "Failed to generate.");
            showError();
        }
    }

    @Override
    public Loader<File> onCreateLoader(int id, Bundle args) {
        return new LicenseHtmlLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<File> loader, File data) {
        showGeneratedHtmlFile(data);
    }

    @Override
    public void onLoaderReset(Loader<File> loader) {

    }
}
