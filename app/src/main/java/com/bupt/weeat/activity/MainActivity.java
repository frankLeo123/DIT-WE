package com.bupt.weeat.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.weeat.R;
import com.bupt.weeat.db.SharedPreferencesHelper;
import com.bupt.weeat.entity.User;
import com.bupt.weeat.fragment.FindFragment;
import com.bupt.weeat.fragment.HeathFragment;
import com.bupt.weeat.fragment.SquareFragment;
import com.bupt.weeat.ui.CircleTransformation;
import com.bupt.weeat.ui.RoundImageView;
import com.bupt.weeat.utils.ActivityUtils;
import com.bupt.weeat.utils.LogUtils;
import com.bupt.weeat.utils.NetUtils;
import com.bupt.weeat.utils.ToastUtils;
import com.bupt.weeat.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

/////////////////////////////////////////////////
//isLogin()改了
//////////////////////////////////////////////
public class MainActivity extends BaseActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private User user;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int ANIM_DURATION_TOOLBAR = 500;
    private static final int FIND_FRAGMENT = 0;
    private static final int SEARCH_ACTIVITY = 1;
    private static final int SQUARE_FRAGMENT = 2;
    private static final int USER_ACTIVITY = 3;
    private static final int HEATH_FRAGMENT = 4;
    private static final int SETTING_ACTIVITY = 5;
    private static final  int CHANGE_THEME=6;
    private static final int TIME_INTERVAL = 2000;
    private static final int REQUEST_LOGIN_CODE = 0;
    private static final int REQUEST_USER_CODE = 1;
    private int drawerPosition = FIND_FRAGMENT;
    private long mBackPressed;
    private boolean pendingIntroAnim;
    private MenuItem menuItem;
    int avatarSize;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.user_avatar_iv)
    RoundImageView navUserAvatar;
    @InjectView(R.id.user_name_tv)
    TextView navUserName;
    @InjectView(R.id.nav)
    NavigationView nav;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.main;
    }

    @Override
    protected void initData() {
        super.initData();
        selectItems(FIND_FRAGMENT);
        pendingIntroAnim = true;
        avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setNavContent(nav);
        updateUserHeader();
    }


    @Override
    protected void setListener() {
        super.setListener();
        navUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                    startActivityForResult(userIntent, REQUEST_USER_CODE);
                } else {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, REQUEST_LOGIN_CODE);
                }
            }
        });
    }

    private void setNavContent(NavigationView nav) {
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_home:
                        selectItems(FIND_FRAGMENT);
                        break;
                    case R.id.nav_item_search:
                        selectItems(SEARCH_ACTIVITY);
                        break;
                    case R.id.nav_item_square:
                        selectItems(SQUARE_FRAGMENT);
                        break;
                    case R.id.nav_item_person:
                        selectItems(USER_ACTIVITY);
                        break;
                    case R.id.nav_item_heath:
                        selectItems(HEATH_FRAGMENT);
                        break;
                    case R.id.menu_settings:
                        selectItems(SETTING_ACTIVITY);
                        break;
                    case  R.id.menu_change_theme:
                        selectItems(CHANGE_THEME);
                    default:
                        break;

                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "onActivityResult");
        if (resultCode == 2 || requestCode == 1) {
            switch (requestCode) {
                case REQUEST_LOGIN_CODE:
                    LogUtils.i(TAG, "onActivityResult" + REQUEST_LOGIN_CODE);
                    updateUserHeader();
                    drawerPosition = FIND_FRAGMENT;
                    selectItems(drawerPosition);
                    break;
                case REQUEST_USER_CODE:
                    LogUtils.i(TAG, "onActivityResult" + REQUEST_USER_CODE);
                    updateUserHeader();
                    drawerPosition = FIND_FRAGMENT;
                    selectItems(drawerPosition);
                    break;
                default:
                    break;
            }
        }
    }


    public void updateUserHeader() {
        user = BmobUser.getCurrentUser(this, User.class);
        LogUtils.i(TAG, user + "updateUserHeader()");
        if (user != null) {
            if (user.getUserImage() != null) {
                String imageUrl = user.getUserImage().getFileUrl(this);

                Picasso.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .resize(avatarSize, avatarSize)
                        .placeholder(R.drawable.tou_xiang)
                        .transform(new CircleTransformation())
                        .into(navUserAvatar);
            }
            LogUtils.i(TAG, user + "");
            if (user.getUsername() != null) {
                navUserName.setText(user.getUsername());
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean connectedNet = NetUtils.checkNet(this);
        if (!connectedNet) {
            ToastUtils.showToast(this, R.string.check_net, Toast.LENGTH_SHORT);
        }
        LogUtils.i(TAG, "onResume" + drawerPosition);
        mBackPressed = 0L;
        selectItems(drawerPosition);
    }
//////////////////////////////////////////////////
    ///再来一波
    public boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            return true;
        }
//        return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if (drawerPosition != FIND_FRAGMENT) {
            drawerPosition = FIND_FRAGMENT;
            selectItems(drawerPosition);
        } else {
            if (System.currentTimeMillis() - mBackPressed > TIME_INTERVAL) {
                mBackPressed = System.currentTimeMillis();
                ToastUtils.showToast(this, R.string.one_more_press_exit, Toast.LENGTH_SHORT);
            } else {
                ToastUtils.clearToast();
                ActivityUtils.finishAll();
            }

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menuItem = menu.findItem(R.id.actionbar_take_picture);
        menuItem.setActionView(R.layout.menu_item_view);
        if (pendingIntroAnim) {
            startIntroAnim();
            pendingIntroAnim = false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void startIntroAnim() {
        int actionBarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionBarSize);
        menuItem.getActionView().setTranslationY(-actionBarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        menuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
    }

    public void selectItems(int position) {
        Fragment fragment = null;
        FragmentTransaction ft;
        switch (position) {
            case FIND_FRAGMENT:
                fragment = new FindFragment();
                toolbar.setTitle(R.string.drawer_home);
                drawerPosition = FIND_FRAGMENT;
                break;
            case SEARCH_ACTIVITY:
                toolbar.setTitle(R.string.drawer_search);
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case SQUARE_FRAGMENT:
                fragment = new SquareFragment();
                toolbar.setTitle(R.string.drawer_square);
                drawerPosition = SQUARE_FRAGMENT;
                break;
            case USER_ACTIVITY:
                if (!isLogin()) {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                    ToastUtils.showToast(this, R.string.please_login_first, Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent,REQUEST_LOGIN_CODE);
                } else {
                    drawerPosition = USER_ACTIVITY;
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivityForResult(intent,REQUEST_USER_CODE);
                }
                toolbar.setTitle(R.string.drawer_person);
                break;
            case HEATH_FRAGMENT:
                fragment = new HeathFragment();
                toolbar.setTitle(R.string.drawer_heath);
                drawerPosition = HEATH_FRAGMENT;
                break;
            case SETTING_ACTIVITY:
                Intent setting_intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(setting_intent);
                break;
            case  CHANGE_THEME:
                boolean currentTheme = SharedPreferencesHelper.getTheme(this);
                SharedPreferencesHelper.setTheme(this, !currentTheme);
                Intent themeIntent=new Intent(MainActivity.this,MainActivity.class);
                themeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(themeIntent);
                overridePendingTransition(0,0);
            default:
                break;
        }
        if (fragment != null) {
            if (fragment.isAdded()) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.show(fragment).commitAllowingStateLoss();
            } else {
                ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.drawer_frame, fragment).commitAllowingStateLoss();
            }
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
