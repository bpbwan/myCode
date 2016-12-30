/*
 * Copyright (C) 2010 The Android Open Source Project
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
package com.android.launcher2;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.android.launcher.R;
import com.android.launcher2.WallpaperChooserDialogFragment.ChangeWallpaperTask;

import java.io.IOException;
import java.util.ArrayList;

public class WallpaperChooserDialogFragment extends DialogFragment implements
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = "Launcher.WallpaperChooserDialogFragment";
    private static final String EMBEDDED_KEY = "com.android.launcher2."
            + "WallpaperChooserDialogFragment.EMBEDDED_KEY";

    private boolean mEmbedded;
    private Bitmap mBitmap = null;

    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    private WallpaperLoader mLoader;
    private WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();

    private Context resPackageCtx = null;
    private static final String RES_PACKAGENAME = "com.android.launcher.res";

    public static WallpaperChooserDialogFragment newInstance() {
        WallpaperChooserDialogFragment fragment = new WallpaperChooserDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            resPackageCtx = Launcher.skinContext;
            Log.e(TAG, "resPackageCtx"+resPackageCtx);
        } catch (Exception e) {
            Log.e("Res_Update", "Create Res Apk Failed");
            try {
				resPackageCtx = ((Context)getActivity()).createPackageContext(RES_PACKAGENAME,
				        Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EMBEDDED_KEY)) {
            mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY);
        } else {
            mEmbedded = isInLayout();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EMBEDDED_KEY, mEmbedded);
    }

    private void cancelLoader() {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel(true);
            mLoader = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cancelLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelLoader();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        /* On orientation changes, the dialog is effectively "dismissed" so this is called
         * when the activity is no longer associated with this dying dialog fragment. We
         * should just safely ignore this case by checking if getActivity() returns null
         */
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /* This will only be called when in XLarge mode, since this Fragment is invoked like
     * a dialog in that mode
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        findWallpapers();

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        findWallpapers();

        /* If this fragment is embedded in the layout of this activity, then we should
         * generate a view to display. Otherwise, a dialog will be created in
         * onCreateDialog()
         */
        if (mEmbedded) {
            View view = inflater.inflate(R.layout.wallpaper_chooser, container, false);
            view.setBackground(mWallpaperDrawable);

            final Gallery gallery = (Gallery) view.findViewById(R.id.gallery);
            gallery.setCallbackDuringFling(false);
            gallery.setOnItemSelectedListener(this);
            gallery.setAdapter(new ImageAdapter(getActivity()));

            View setButton = view.findViewById(R.id.set);
            setButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectWallpaper(gallery.getSelectedItemPosition());
                }
            });
            return view;
        }
        return null;
    }

    private void selectWallpaper(int position) {
        Context resPackageCtx = null;
        try {
            Activity activity = getActivity();
            resPackageCtx = activity.createPackageContext(RES_PACKAGENAME,
                     Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
           Log.e("Res_Update", "Create Res Apk Failed");
        }

		
        try {
            if (null == resPackageCtx) {
                WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(
                        Context.WALLPAPER_SERVICE);
                Drawable d = Launcher.skinContext.getResources().getDrawable(mImages.get(position));
                BitmapDrawable bd = (BitmapDrawable) d;
                Bitmap bm = bd.getBitmap();
                wpm.setBitmap(bm);
                //wpm.setResource(mImages.get(position));
                Activity activity = getActivity();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            } else {
                new ChangeWallpaperTask().execute(mBitmap);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
		/*
            WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(
                    Context.WALLPAPER_SERVICE);
            wpm.setResource(mImages.get(position));
            Activity activity = getActivity();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
        */
    }

    // Click handler for the Dialog's GridView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectWallpaper(position);
    }

    // Selection handler for the embedded Gallery view
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel();
        }
        mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void findWallpapers() {
        mThumbs = new ArrayList<Integer>(24);
        mImages = new ArrayList<Integer>(24);

       
	    Resources resources = null;
		int wallpaperId = 0;
		int extra_wallpaperId = 0;
	   
		if (null == resPackageCtx) {
				Log.i(TAG,"resPackageCtx is null");
				resources = getResources();
				wallpaperId = R.array.wallpapers;
				extra_wallpaperId = R.array.extra_wallpapers;
			 } else {
				   try {
					 resources = Launcher.skinContext.getResources();
					 wallpaperId = resources.getIdentifier("wallpapers", "array", "com.flyaudio.skin");
					 extra_wallpaperId = resources.getIdentifier("extra_wallpapers", "array", "com.flyaudio.skin");
					   
				} catch (Exception e) {
					   // TODO: handle exception
					   
					   Log.i(TAG,"resPackageCtx = "+resPackageCtx);
					   resources = resPackageCtx.getResources();
					   wallpaperId = resources.getIdentifier("wallpapers", "array", RES_PACKAGENAME);
					   extra_wallpaperId = resources.getIdentifier("extra_wallpapers", "array", RES_PACKAGENAME);
			}
		}
	   
		// Context.getPackageName() may return the "original" package name,
	    // com.android.launcher2; Resources needs the real package name,
		// com.android.launcher. So we ask Resources for what it thinks the
		// package name should be.
		final String packageName = resources.getResourcePackageName(wallpaperId);
       

        addWallpapers(resources, packageName, wallpaperId);
        addWallpapers(resources, packageName, extra_wallpaperId);
    }

    private void addWallpapers(Resources resources, String packageName, int list) {
        final String[] extras = resources.getStringArray(list);
        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                final int thumbRes = resources.getIdentifier(extra + "_small",
                        "drawable", packageName);

                if (thumbRes != 0) {
                    mThumbs.add(thumbRes);
                    mImages.add(res);
                    // Log.d(TAG, "add: [" + packageName + "]: " + extra + " (" + res + ")");
                }
            }
        }
    }

    private class ImageAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
        private LayoutInflater mLayoutInflater;

        ImageAdapter(Activity activity) {
            mLayoutInflater = activity.getLayoutInflater();
        }

        public int getCount() {
            return mThumbs.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
            } else {
                view = convertView;
            }

            ImageView image = (ImageView) view.findViewById(R.id.wallpaper_image);

            int thumbRes = mThumbs.get(position);
          
            Drawable thumbDrawable = null;

			if (null == resPackageCtx) {
                image.setImageResource(thumbRes);
                thumbDrawable = image.getDrawable();
            } else {
                thumbDrawable = resPackageCtx.getResources().getDrawable(thumbRes);
                image.setImageDrawable(thumbDrawable);
            }

            if (thumbDrawable != null) {
                thumbDrawable.setDither(true);
            } else {
                Log.e(TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
                        + position);
            }

            return view;
        }
    }

    class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
        BitmapFactory.Options mOptions;

        WallpaperLoader() {
            mOptions = new BitmapFactory.Options();
            mOptions.inDither = false;
            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (isCancelled()) return null;
            try {
                if (null == resPackageCtx) {
                    return BitmapFactory.decodeResource(getResources(),
                            mImages.get(params[0]), mOptions);
                } else {
                    Drawable drawable = resPackageCtx.getResources().getDrawable(mImages.get(params[0]));
                    return getBitmapFromDrawable(drawable);
                }
            } catch (OutOfMemoryError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            if (b == null) return;

            if (!isCancelled() && !mOptions.mCancel) {
                // Help the GC
                if (mBitmap != null) {
                    mBitmap.recycle();
                }

                View v = getView();
                if (v != null) {
                    mBitmap = b;
                    mWallpaperDrawable.setBitmap(b);
                    v.postInvalidate();
                } else {
                    mBitmap = null;
                    mWallpaperDrawable.setBitmap(null);
                }
                mLoader = null;
            } else {
               b.recycle();
            }
        }

        void cancel() {
            mOptions.requestCancelDecode();
            super.cancel(true);
        }
    }

    /**
     * Custom drawable that centers the bitmap fed to it.
     */
    static class WallpaperDrawable extends Drawable {

        Bitmap mBitmap;
        int mIntrinsicWidth;
        int mIntrinsicHeight;

        /* package */void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mBitmap == null)
                return;
            mIntrinsicWidth = mBitmap.getWidth();
            mIntrinsicHeight = mBitmap.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            if (mBitmap == null) return;
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int x = (width - mIntrinsicWidth) / 2;
            int y = (height - mIntrinsicHeight) / 2;
            canvas.drawBitmap(mBitmap, x, y, null);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.OPAQUE;
        }

        @Override
        public void setAlpha(int alpha) {
            // Ignore
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            // Ignore
        }
    }
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    class ChangeWallpaperTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected void onPreExecute() {
            MyProgressDialog mpDialog = MyProgressDialog.newInstance(R.string.progress_msg_wallpaper,
                    R.string.progress_title_wallpaper);
            mpDialog.show(getFragmentManager(), "dialog");
        }

        @Override
        protected Void doInBackground(Bitmap... params) {

            WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(
                    Context.WALLPAPER_SERVICE);
            try {
                wpm.setBitmap(params[0]);
            } catch (IOException e) {
                Log.e(TAG, "Failed to set wallpaper: " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        }
    }

    private static class MyProgressDialog extends DialogFragment {
        private static final String TITLE = "title";
        private static final String MESSAGE = "message";

        public static MyProgressDialog newInstance(int message, int title) {
            MyProgressDialog dialog = new MyProgressDialog();
            final Bundle args = new Bundle();
            args.putInt(TITLE, title);
            args.putInt(MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int message = getArguments().getInt(MESSAGE);
            final int title = getArguments().getInt(TITLE);

            final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                    ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getString(title));
            progressDialog.setMessage(getString(message));
            progressDialog.setCancelable(true);
            return progressDialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }

}