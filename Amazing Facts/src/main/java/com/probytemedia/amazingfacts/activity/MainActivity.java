package com.probytemedia.amazingfacts.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;
import com.probytemedia.amazingfacts.fragment.FavoritesListFragment;
import com.probytemedia.amazingfacts.fragment.MainFragment;
import com.probytemedia.amazingfacts.models.FavModel;
import com.probytemedia.amazingfacts.utils.AppConstants;
import com.probytemedia.amazingfacts.utils.AppRater;
import com.probytemedia.unlimitedfacts.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.banner.BannerListener;
import com.startapp.android.publish.banner.banner3d.Banner3D;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends FragmentActivity {
    private static final String IS_NEW_VERSION_INIT = "version1.2";
    private static final String IS_V3_VERSION_INIT = "version1.3";
    public static int previousSelection = 0;
    public static int selectedTab = -1;
    public static int mainTextColor;

    @InjectView(R.id.favoriteBtn)
    ToggleButton favBtn;

    @InjectView(R.id.quotesBtn)
    ToggleButton quoteBtn;

    @InjectView(R.id.main_layout)
    RelativeLayout holderLayout;

    @InjectView(R.id.startApp3DBanner)
    Banner3D banner3D;

    private String StartApp_DEV_ID = "103288416";
    private String StartApp_APP_ID = "203241607";
    private StartAppAd startAppAd;

    private static final String AdBuddiz_PUBLISHER_KEY = "53e6d566-3eaf-4b41-a309-468fec37a274";
    private boolean showInterstitial = false;
    private final String THEME_COLOR = "theme_color";
    private boolean refreshLayout = false;
    private int selectedTheme = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        previousSelection = 0;
        selectedTab = -1;
        selectedTheme = prefs.getInt(THEME_COLOR, 1);
        startAppAd = new StartAppAd(this);
        StartAppSDK.init(this, StartApp_DEV_ID, StartApp_APP_ID, true);
        AdBuddiz.setPublisherKey(AdBuddiz_PUBLISHER_KEY);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        banner3D.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View banner) {
                try {
                    banner3D.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailedToReceiveAd(View banner) {
                try {
                    banner3D.setVisibility(View.GONE);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onClick(View banner) {
            }
        });
        List<FavModel> allFavoriteList = FavModel.listAll(FavModel.class);
        if (allFavoriteList.size() == 0) {
            new StartUpManagement().execute();
        } else {
            boolean v1_2 = prefs.getBoolean(IS_NEW_VERSION_INIT, false);
            if (v1_2) {

                boolean v1_3 = prefs.getBoolean(IS_V3_VERSION_INIT, false);
                if (v1_3) {
                    AppConstants.favListDB = allFavoriteList;
                    updateFragment(selectedTheme);
                } else {
                    new AddNewVersion3Data().execute();
                }
            } else {
                new AddNewData().execute();
            }
        }
        AppRater.app_launched(MainActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startAppAd.onBackPressed();
//        if (showInterstitial) {
//			if (interstitialAd.isLoaded()) {
//				interstitialAd.show();
//			} else {
//				finish();
//			}
//        } else {
//            finish();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
        AdBuddiz.cacheAds(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    private void updateFragment(int type) {
        mainTextColor = type == 0 ? Color.parseColor("#FFD810") : Color.parseColor("#810541");
//        mainTextColor=Color.parseColor("##FFD810");
        if (Build.VERSION.SDK_INT >= 16) {
            holderLayout.setBackground(new ColorDrawable(type == 0 ? Color.BLACK : Color.WHITE));
        } else {
            holderLayout.setBackgroundDrawable(new ColorDrawable(type == 0 ? Color.BLACK : Color
                    .WHITE));
        }
        if (refreshLayout) {
            if (selectedTab != 1) {
                showQuotesTab();
            } else {
                showFavTab();
            }
        } else {
            showQuotesTab();
        }
    }


    @OnClick(R.id.favoriteBtn)
    public void onFavClick(View v) {
        showFavTab();
    }

    @OnClick(R.id.quotesBtn)
    public void onQuoteClick(View v) {
        showQuotesTab();
    }

    public void showFavTab() {
        quoteBtn.setChecked(false);
        favBtn.setChecked(true);
        initFragment(new FavoritesListFragment());
        selectedTab = 1;
        refreshLayout = false;
    }

    public void showQuotesTab() {
        quoteBtn.setChecked(true);
        favBtn.setChecked(false);
        initFragment(new MainFragment());
        selectedTab = 0;
        refreshLayout = false;
    }

    private void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.am_fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_color) {
            selectedTheme = selectedTheme == 0 ? 1 : 0;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity
                    .this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(THEME_COLOR, selectedTheme);
            editor.commit();
            updateFragment(selectedTheme);
        }
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=com.probytemedia" +
                            ".unlimitedfacts");
            startActivity(shareIntent);
        }
        return true;
    }

    public void showAdBuzzAds() {
        if (AdBuddiz.isReadyToShowAd(this)) { // this = current Activity
            AdBuddiz.showAd(this);
        }
    }

    public void showInterstitial() {
        startAppAd.showAd(); // show the ad
        startAppAd.loadAd();
    }

    public class StartUpManagement extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        private StartUpManagement async;
        private boolean asyncCancel = false;
        private ArrayList<String> quotesListLocal = new ArrayList<String>();

        StartUpManagement() {
            asyncCancel = false;
            async = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    async.cancel(true);
                    asyncCancel = true;
                    pd.dismiss();
                    finish();
                }
            });
            pd.setMessage("Initializing Data..");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            quotesListLocal = AppConstants.getAllList();
            for (int i = 0; i < quotesListLocal.size(); i++) {
                if (asyncCancel) {
                    i = quotesListLocal.size();
                    break;
                } else {
                    FavModel favoritesModel = new FavModel(MainActivity.this, i, false);
                    favoritesModel.save();
                }
            }
            List<FavModel> allFavoriteList = FavModel.listAll(FavModel.class);
            AppConstants.favListDB = allFavoriteList;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!asyncCancel) {
                pd.dismiss();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(IS_NEW_VERSION_INIT, true);
                editor.putBoolean(IS_V3_VERSION_INIT, true);
                editor.commit();
                updateFragment(selectedTheme);
            }
        }
    }

    public class AddNewData extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        private AddNewData async;
        private boolean asyncCancel = false;
        private ArrayList<String> quotesListLocal = new ArrayList<String>();

        AddNewData() {
            asyncCancel = false;
            async = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    async.cancel(true);
                    asyncCancel = true;
                    pd.dismiss();
                    finish();
                }
            });
            pd.setMessage("Processing Data..");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            quotesListLocal = AppConstants.getAllList();
            for (int i = 501; i < quotesListLocal.size(); i++) {
                if (asyncCancel) {
                    break;
                } else {
                    FavModel favoritesModel = new FavModel(MainActivity.this, i, false);
                    favoritesModel.save();
                }
            }
            List<FavModel> allFavoriteList = FavModel.listAll(FavModel.class);
            AppConstants.favListDB = allFavoriteList;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!asyncCancel) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(IS_NEW_VERSION_INIT, true);
                editor.putBoolean(IS_V3_VERSION_INIT, true);
                editor.commit();
                pd.dismiss();
                updateFragment(selectedTheme);
            }
        }
    }


    public class AddNewVersion3Data extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        private AddNewVersion3Data async;
        private boolean asyncCancel = false;
        private ArrayList<String> quotesListLocal = new ArrayList<String>();

        AddNewVersion3Data() {
            asyncCancel = false;
            async = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    async.cancel(true);
                    asyncCancel = true;
                    pd.dismiss();
                    finish();
                }
            });
            pd.setMessage("Processing Data..");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            quotesListLocal = AppConstants.getAllList();
            for (int i = 826; i < quotesListLocal.size(); i++) {
                if (asyncCancel) {
                    break;
                } else {
                    FavModel favoritesModel = new FavModel(MainActivity.this, i, false);
                    favoritesModel.save();
                }
            }
            List<FavModel> allFavoriteList = FavModel.listAll(FavModel.class);
            AppConstants.favListDB = allFavoriteList;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!asyncCancel) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(IS_NEW_VERSION_INIT, true);
                editor.putBoolean(IS_V3_VERSION_INIT, true);
                editor.commit();
                pd.dismiss();
                updateFragment(selectedTheme);
            }
        }
    }

}
