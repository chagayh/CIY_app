package com.example.ciy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

/**
 * This activity represents the BottomNavigationBarActivity of the app. It creates 3 fragments:
 * DiscoverFragment, FavoritesFragment, and FridgeFragment, and activate the corresponding
 * fragment the user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBarActivity extends AppCompatActivity {

    /* constants */
    private static final int ADD_RECIPE_REQUEST_CODE = 2;
    private static final int SEARCH_RECIPE_REQUEST_CODE = 22;
    private static final int ERROR = -1;

    /* the Home fragment Tag */
    private static final String HOME = "Home";
    /* the Favorites fragment Tag */
    private static final String FAVORITES = "Favorites";
    /* the Discover fragment Tag */
    private static final String DISCOVER = "Discover";
    /* tags */
    private static final String FRIDGE_TAG = "FridgeFromHome";

    /* the Home fragment */
    HomeFragment homeFragment;
    /* the Discover fragment */
    DiscoverFragment discoverFragment;
    /* the Favorites fragment */
    FavoritesFragment favoritesFragment;
    /* the Search fragment */
    FridgeFragment fridgeFragment;
    /* the indicator of the last fragment we showed/added */
    int lastPushed = SharedData.DEFAULT;
    /* the tag of the last fragment we showed/added */
    private String lastTag = null;

    /* the constants of the elements that displayed in the intro */
    private static final String HOME_EXPLANATION = "Your home screen where you can change and " +
            "add ingredients";
    private static final String DISCOVER_EXPLANATION = "Discover new recipes";
    private static final String FAVORITES_EXPLANATION = "Check your favorites recipes";
    private static final String ADD_RECIPE_EXPLANATION = "Add your own recipe";
    private static final String SEARCH_RECIPES_EXPLANATION = "Here you search recipes by name, you can " +
            "find which recipes ingredients match the ones you currently have and filter the " +
            "search with multiple filter tags.";
    private static final String SEARCH_INGREDIENTS_EXPLANATION = "Search and add more ingredients here";
    private static final String BASIC_INGREDIENTS_EXPLANATION = "Here are all the must have " +
            "basic ingredients you probably have in your kitchen";
    private static final String SHELF_EXPLANATION = "Drag your basic ingredients here and add " +
            "them to your fridge";
    private static final String FRIDGE_EXPLANATION = "Tap to open your fridge swipe right or " +
            "left to remove ingredients";
    private static final int INTRO_TEXT_SIZE = 16;
    private static final String LOGIN_ACTIVITY_FLAG_VALUE = "LoginActivity";
    private static final String LOGIN_ACTIVITY_FLAG_KEY = "I_CAME_FROM";
    private static final int DISCOVER_INTRO_IDX = 1;
    private static final int FAVORITES_INTRO_IDX = 2;
    private static final int ADD_RECIPE_INTRO_IDX = 3;
    private static final int SEARCH_RECIPES_INTRO_IDX = 4;
    private static final int SEARCH_INGREDIENTS_INTRO_IDX = 5;
    private static final int BASIC_INGREDIENTS_INTRO_IDX = 6;
    private static final int SHELF_INTRO_IDX = 7;
    private static final int FRIDGE_INTRO_IDX = 8;
    private static final int FINISHED_TUTORIAL = 9;
    private static final int LOWER_BOUND_SIZE = 0;
    private static final int UPPER_BOUND_SIZE = 5;


    /* app's Bottom navigation bar */
    private BottomNavigationView bottomNav;

    /* the FireBase authenticator */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /* the url to the user's profile */
    private Uri profileUri = null;

    /* rounded frame for the user's profile */
    RoundedBitmapDrawable roundedFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            profileUri = currentUser.getPhotoUrl();
        }
        // set bottom and top bars
        setBars();

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            discoverFragment = new DiscoverFragment();
            favoritesFragment = new FavoritesFragment();
            fridgeFragment = new FridgeFragment();

        }
        //setting home fragment as default
        setsDefaultFragment();
        appTutorial();
    }

    /**
     * sets the default fragment to be the home fragment
     */
    private void setsDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .add(R.id.fragment_container, homeFragment, HOME)
                .setBreadCrumbShortTitle(HOME);
        transaction.commit();
        lastPushed = SharedData.HOME;
        lastTag = HOME;
    }


    /**
     * this function checks if the user launch from the loginActivity and if so,
     * display the app tutorial
     */
    private void appTutorial() {
        String flag = getIntent().getStringExtra(LOGIN_ACTIVITY_FLAG_KEY);
        if (flag != null) {
            if (flag.equals(LOGIN_ACTIVITY_FLAG_VALUE)) {
                displayAppTutorial(HOME_EXPLANATION,
                        bottomNav.getMenu().findItem(R.id.navHome).getItemId(), DISCOVER_INTRO_IDX);
            }
        }
    }


    /**
     * this function responsible on introduce the app for first time users
     *
     * @param title     - the title of the current element to display
     * @param viewId    - the current element id
     * @param elementId - indicates on the next element to display
     */
    private void displayAppTutorial(String title, int viewId, final int elementId) {

        final int navDiscover = bottomNav.getMenu().findItem(R.id.navDiscover).getItemId();
        final int navFavorites = bottomNav.getMenu().findItem(R.id.navFavorites).getItemId();
        final int navAddRecipe = bottomNav.getMenu().findItem(R.id.navAddRecipe).getItemId();

        new GuideView.Builder(this)
                .setTitle(title)
                .setTargetView(findViewById(viewId))
                .setTitleTextSize(INTRO_TEXT_SIZE)
                .setGravity(GuideView.Gravity.center)
                .setDismissType(GuideView.DismissType.anywhere)
                .setGuideListener(view -> {
                    switch (elementId) {
                        case DISCOVER_INTRO_IDX:
                            displayAppTutorial(DISCOVER_EXPLANATION, navDiscover,
                                    FAVORITES_INTRO_IDX);
                            break;
                        case FAVORITES_INTRO_IDX:
                            displayAppTutorial(FAVORITES_EXPLANATION, navFavorites,
                                    ADD_RECIPE_INTRO_IDX);
                            break;
                        case ADD_RECIPE_INTRO_IDX:
                            displayAppTutorial(ADD_RECIPE_EXPLANATION, navAddRecipe,
                                    SEARCH_RECIPES_INTRO_IDX);
                            break;
                        case SEARCH_RECIPES_INTRO_IDX:
                            displayAppTutorial(SEARCH_RECIPES_EXPLANATION,
                                    R.id.actionSearchNavigation, SEARCH_INGREDIENTS_INTRO_IDX);
                            break;
                        case SEARCH_INGREDIENTS_INTRO_IDX:
                            displayAppTutorial(SEARCH_INGREDIENTS_EXPLANATION,
                                    R.id.enterIngredients, BASIC_INGREDIENTS_INTRO_IDX);
                            break;
                        case BASIC_INGREDIENTS_INTRO_IDX:
                            displayAppTutorial(BASIC_INGREDIENTS_EXPLANATION,
                                    R.id.dragIngredients, SHELF_INTRO_IDX);
                            break;
                        case SHELF_INTRO_IDX:
                            displayAppTutorial(SHELF_EXPLANATION, R.id.basicIngredientsShelf,
                                    FRIDGE_INTRO_IDX);
                            break;
                        case FRIDGE_INTRO_IDX:
                            displayAppTutorial(FRIDGE_EXPLANATION, R.id.fridge_button,
                                    FINISHED_TUTORIAL);
                            break;
                    }
                })
                .build()
                .show();
    }


    /**
     * sets the bottom navigation bar and upper toolbar
     */
    private void setBars() {
        // define the bottom navigation bar to be used in the activity
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable logo = ContextCompat.getDrawable(this, R.drawable.women_logo);
        toolbar.setLogo(logo);
    }


    /**
     * create the layout of the toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_buttons, menu);

        // user not sign in, so default profile picture defined
        menu.findItem(R.id.icon_status).setIcon(ContextCompat.getDrawable(this,
                R.drawable.profile_default));

        if (profileUri != null) // user got profile photo, set it as icon
        {
            setProfileImage(menu, profileUri);
        }
        return true;
    }


    /**
     * act in response to user selection
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_status:
                return showSignOutDialog();
            case R.id.actionSearchNavigation:
                Intent intent = new Intent(getBaseContext(), SearchRecipeActivity.class);
                startActivityForResult(intent, SEARCH_RECIPE_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * moves to the corresponding fragment according to user's choise
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navHome:
                            homePressHandler();
                            break;
                        case R.id.navDiscover:
                            discoverPressHandler();
                            break;
                        case R.id.navFavorites:
                            showFragment(favoritesFragment, FAVORITES, lastTag);
                            lastPushed = SharedData.FAVORITES;
                            lastTag = FAVORITES;
                            break;
                        case R.id.navAddRecipe:
                            startActivityForResult(
                                    new Intent(BottomNavigationBarActivity.this,
                                            AddRecipeActivity.class), ADD_RECIPE_REQUEST_CODE);
                            break;
                    }
                    return true;
                }
            };

    /**
     * handles the event were we pressed at the home icon in the bottom navigation bar
     */
    private void homePressHandler() {

        showFragment(homeFragment, HOME, lastTag);
        lastPushed = SharedData.HOME;
        lastTag = HOME;
    }

    /**
     * handles the event were we pressed at the discover icon in the bottom navigation bar
     */
    private void discoverPressHandler() {
        if (lastPushed == SharedData.DISCOVER) {
            discoverFragment.scrollToTop();
        } else {
            showFragment(discoverFragment, DISCOVER, lastTag);
            lastPushed = SharedData.DISCOVER;
            lastTag = DISCOVER;
        }
    }

    /**
     * This method shows the wanted fragment and hides the previous one
     *
     * @param fragment the wanted fragment that we want to show
     * @param tag      the tag of the wanted fragment
     * @param lastTag  the tag of the previous fragment that we want to hide
     */
    private void showFragment(Fragment fragment, String tag, String lastTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastFragment != null) {
                transaction.hide(lastFragment);
            }
        }
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, tag).setBreadCrumbShortTitle(tag);
        }
        transaction.commit();
    }

    /**
     * set the layout of the dialog
     *
     * @param dialogBuilder the builder of the dialog
     * @param view     the view of the layout to be set in the dialog
     */
    private void setDialogView(AlertDialog.Builder dialogBuilder, View view) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            ImageView profile = view.findViewById(R.id.profile_image);
            if (profileUri != null) {
                try {
                    Picasso.get().load(profileUri).into(profile);
                } catch (Exception e) { // in this case we stay with the default profile photo
                }
            }
            String username = currentUser.getDisplayName();
            if (username != null) {
                TextView user_info = view.findViewById(R.id.user_details);
                if (!username.equals("")) {
                    String userInfoText = username + "\n" + currentUser.getEmail();
                    user_info.setText(userInfoText);
                } else {
                    String number = currentUser.getPhoneNumber();
                    user_info.setText(number);
                }
            }
        }
        dialogBuilder.setView(view);
    }

    /**
     * set the actions on every user selection on the dialog
     *
     * @param view        the view of the dialog
     * @param alertDialog the dialog
     */
    private void onClickDialog(View view, AlertDialog alertDialog) {
        Button signOut = view.findViewById(R.id.signout_button);
        signOut.setOnClickListener(view1 -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button addIngredients = view.findViewById(R.id.what_in_my_fridge);
        addIngredients.setOnClickListener(view12 -> {
            alertDialog.cancel();
            FragmentManager fragmentManager = Objects.requireNonNull
                    (BottomNavigationBarActivity.this).getSupportFragmentManager();
            FridgeFragment fridgeFragment = (BottomNavigationBarActivity.this).fridgeFragment;
            fridgeFragment.show(fragmentManager, FRIDGE_TAG);
        });
    }

    /**
     * show the sign out dialog on screen
     */
    private boolean showSignOutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                (BottomNavigationBarActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_user_status, null);
        setDialogView(dialogBuilder, view);
        final AlertDialog alertdialog = dialogBuilder.create();
        onClickDialog(view, alertdialog);
        Objects.requireNonNull(alertdialog.getWindow()).setBackgroundDrawable
                (new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();

        return true;
    }

    /**
     * get menu resource and url for user profile photo, and shows the image on the menu
     *
     * @param menu the menu bar object of the app or activity
     * @param profileImageUri  the url for the user's profile photo
     */
    private void setProfileImage(final Menu menu, Uri profileImageUri) {
        //create a new target to be used with picasso
        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                roundedFrame = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedFrame.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()));
                roundedFrame.setBounds(LOWER_BOUND_SIZE, LOWER_BOUND_SIZE,
                                                        UPPER_BOUND_SIZE, UPPER_BOUND_SIZE);
                menu.findItem(R.id.icon_status).setIcon(roundedFrame);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable drawable) {
                //in this case we want the profile stay as the default
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                //in this case we want the profile stay as the default
            }
        };
        // set the image to be presented on the menu bar
        Picasso.get().load(profileImageUri).into(mTarget);
    }


    /**
     * handles the case when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        if (lastPushed == SharedData.HOME) {
            super.onBackPressed();
        } else {
            homePressHandler();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.getMenu().findItem(R.id.navHome).setChecked(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == ADD_RECIPE_REQUEST_CODE) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            int navIcon = returnNavIcon();
            if (navIcon != ERROR) {
                bottomNavigationView.getMenu().findItem(navIcon).setChecked(true);
            }
        }
        if (requestCode == SEARCH_RECIPE_REQUEST_CODE) {
            // meaning we returned from the search recipe activity and we need to reset the filters
            SharedData.filterClickRecord = new boolean[]{false, false, false, false};
        }
    }

    /**
     * mark the icon of the current fragment were at in the bottom navigation bar
     *
     * @return the id of that icon, error otherwise
     */
    private int returnNavIcon() {
        switch (lastPushed) {
            case SharedData.HOME:
                return R.id.navHome;
            case SharedData.DISCOVER:
                return R.id.navDiscover;
            case SharedData.FAVORITES:
                return R.id.navFavorites;
            default:
                return ERROR;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clears the ingredients when we sign out or exit the application
        SharedData.ingredients.clear();
    }

}
