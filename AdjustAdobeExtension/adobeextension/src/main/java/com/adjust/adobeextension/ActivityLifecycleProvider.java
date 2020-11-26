package com.adjust.adobeextension;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ActivityLifecycleProvider
        extends ContentProvider
{
    @Override
    public boolean onCreate() {
        return AdjustSdkApiHandler.getInstance().
                registerActivityLifecycleCallbacks(getContext());
    }

    @Override
    public Cursor query(final Uri uri,
                        final String[] projection,
                        final String selection,
                        final String[] selectionArgs,
                        final String sortOrder)
    {
        return null;
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        return null;
    }

    @Override
    public int delete(final Uri uri,
                      final String selection,
                      final String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(final Uri uri,
                      final ContentValues values,
                      final String selection,
                      final String[] selectionArgs)
    {
        return 0;
    }
}
