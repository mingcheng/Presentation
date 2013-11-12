package com.gracecode.android.presentation.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.gracecode.android.presentation.Huaban;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.*;

class BitmapDiskLruCache {
    private static final int BUFFER_SIZE = 8 * 1024;
    private final DiskLruCache mDiskLruCache;

    private class SaveBitmapTask extends AsyncTask<Void, Void, Boolean> {
        private final Bitmap mBitmap;
        private final DiskLruCache.Editor mEditor;

        SaveBitmapTask(Bitmap bitmap, DiskLruCache.Editor editor) {
            mBitmap = bitmap;
            mEditor = editor;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(mEditor.newOutputStream(0), BUFFER_SIZE);
                return mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean written) {
            try {
                if (written) {
                    mDiskLruCache.flush();
                    mEditor.commit();
                } else {
                    mEditor.abort();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    BitmapDiskLruCache(File cacheDir, long diskCacheSize) throws IOException {
        mDiskLruCache = DiskLruCache.open(cacheDir, Huaban.APP_VERSION, 1, diskCacheSize);
    }


    public void put(String key, Bitmap data) throws IOException {
        (new SaveBitmapTask(data, mDiskLruCache.edit(key))).execute();
    }


    public boolean isContains(String key) {
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = mDiskLruCache.get(key);
            contained = (snapshot != null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;
    }


    public Bitmap get(String key) throws IOException {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot == null) {
            return null;
        }

        final InputStream in = snapshot.getInputStream(0);
        if (in != null) {
            final BufferedInputStream buffIn = new BufferedInputStream(in, BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(buffIn);
            buffIn.close();
        }

        snapshot.close();
        return bitmap;
    }


    public void clear() throws IOException {
        mDiskLruCache.delete();
    }
}
