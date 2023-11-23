package com.news.gamersky;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.BigImageViewer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.news.gamersky.database.AppDatabase;
import com.news.gamersky.fragment.GalleryFragment;
import com.news.gamersky.fragment.HandBookFragment;
import com.news.gamersky.fragment.NewsFragment;
import com.news.gamersky.fragment.ReviewsFragment;
import com.news.gamersky.fragment.UserFragment;
import com.news.gamersky.setting.AppSetting;
import com.news.gamersky.util.AppUtil;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private long exitTime;
    private BottomNavigationView navView;
    private ObjectAnimator showAnimator;
    private ObjectAnimator hideAnimator;
    private FragmentManager fragmentManager;
    private FrameLayout hostContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppSetting.init(this);
        init();
        startListen();
    }

    public void init() {

        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);


        exitTime = 0;

        showAnimator = new ObjectAnimator();
        hideAnimator = new ObjectAnimator();

        navView = findViewById(R.id.nav_view);
        hostContainer = findViewById(R.id.nav_host_container);

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag("NewsFragment") == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.nav_host_container, new NewsFragment(), "NewsFragment");
            fragmentTransaction.commit();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("no_bottombar", false)) {
            navView.setVisibility(View.GONE);
        } else {
            navView.setLabelVisibilityMode(Integer.parseInt(sharedPreferences.getString("bottom_mode", "1")));
        }

        clearGlideDiskCache(sharedPreferences.getBoolean("auto_clear_cache", true));

        AppDatabase db = Room.databaseBuilder(ThisApp.getContext(),
                AppDatabase.class, "app_database").build();

    }

    public void startListen() {
        navView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            hideAllFragment(fragmentTransaction);

            if (item.getItemId() == R.id.navigation_home) {
                if (fragmentManager.findFragmentByTag("NewsFragment") != null) {
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("NewsFragment"));
                } else {
                    fragmentTransaction.add(R.id.nav_host_container, new NewsFragment(), "NewsFragment");
                }
            } else if (item.getItemId() == R.id.navigation_game_guide) {
                if (fragmentManager.findFragmentByTag("HandBookFragment") != null) {
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("HandBookFragment"));
                } else {
                    fragmentTransaction.add(R.id.nav_host_container, new HandBookFragment(), "HandBookFragment");
                }
            } else if (item.getItemId() == R.id.navigation_game_reviews) {
                if (fragmentManager.findFragmentByTag("ReviewsFragment") != null) {
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("ReviewsFragment"));
                } else {
                    fragmentTransaction.add(R.id.nav_host_container, new ReviewsFragment(), "ReviewsFragment");
                }
            } else if (item.getItemId() == R.id.navigation_gallery) {
                if (fragmentManager.findFragmentByTag("GalleryFragment") != null) {
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("GalleryFragment"));
                } else {
                    fragmentTransaction.add(R.id.nav_host_container, new GalleryFragment(), "GalleryFragment");
                }
            } else if (item.getItemId() == R.id.navigation_user) {
                if (fragmentManager.findFragmentByTag("UserFragment") != null) {
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("UserFragment"));
                } else {
                    fragmentTransaction.add(R.id.nav_host_container, new UserFragment(), "UserFragment");
                }
            }

            Log.i("TAG", "onNavigationItemSelected: " + item + item.getItemId());
            fragmentTransaction.commit();
            return true;
        });

        navView.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (fragmentManager.findFragmentByTag("NewsFragment") != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag("NewsFragment");
                    ((NewsFragment) fragment).upTop();
                }
            } else if (item.getItemId() == R.id.navigation_game_guide) {
                if (fragmentManager.findFragmentByTag("HandBookFragment") != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag("HandBookFragment");
                    ((HandBookFragment) fragment).upTop();
                }
            } else if (item.getItemId() == R.id.navigation_game_reviews) {
                if (fragmentManager.findFragmentByTag("ReviewsFragment") != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag("ReviewsFragment");
                    ((ReviewsFragment) fragment).upTop();
                }
            } else if (item.getItemId() == R.id.navigation_gallery) {
                if (fragmentManager.findFragmentByTag("GalleryFragment") != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag("GalleryFragment");
                    ((GalleryFragment) fragment).upTop();
                }
            } else if (item.getItemId() == R.id.navigation_user) {
                if (fragmentManager.findFragmentByTag("UserFragment") != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag("UserFragment");
                    ((UserFragment) fragment).upTop();
                }
            }
        });

    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (fragmentManager.findFragmentByTag("NewsFragment") != null) {
            fragmentTransaction.hide(fragmentManager.findFragmentByTag("NewsFragment"));
        }
        if (fragmentManager.findFragmentByTag("HandBookFragment") != null) {
            fragmentTransaction.hide(fragmentManager.findFragmentByTag("HandBookFragment"));
        }
        if (fragmentManager.findFragmentByTag("ReviewsFragment") != null) {
            fragmentTransaction.hide(fragmentManager.findFragmentByTag("ReviewsFragment"));
        }
        if (fragmentManager.findFragmentByTag("GalleryFragment") != null) {
            fragmentTransaction.hide(fragmentManager.findFragmentByTag("GalleryFragment"));
        }
        if (fragmentManager.findFragmentByTag("UserFragment") != null) {
            fragmentTransaction.hide(fragmentManager.findFragmentByTag("UserFragment"));
        }
    }


    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            AppUtil.getSnackbar(this, hostContainer, "再次点击退出应用", false, true).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void showNav() {
        if (!showAnimator.isRunning() && !hideAnimator.isRunning()) {
            showAnimator = ObjectAnimator.ofFloat(navView, "translationY", navView.getTranslationY(), 0f);
            showAnimator.setDuration(300);
            showAnimator.start();
        }
    }

    public void hideNav() {
        if (!showAnimator.isRunning() && !hideAnimator.isRunning()) {
            hideAnimator = ObjectAnimator.ofFloat(navView, "translationY", navView.getTranslationY(), navView.getHeight());
            hideAnimator.setDuration(300);
            hideAnimator.start();
        }
    }

    public void clearGlideDiskCache(boolean b) {
        if (b) {
            new Thread(() -> {
                Glide.get(MainActivity.this).clearDiskCache();
                BigImageViewer.imageLoader().cancelAll();
            }).start();
            new WebView(MainActivity.this).clearCache(true);
        }
    }

}