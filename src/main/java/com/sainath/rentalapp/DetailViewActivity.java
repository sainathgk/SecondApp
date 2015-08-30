package com.sainath.rentalapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connection.rentalapp.NetworkConnUtility;
import com.connection.rentalapp.NetworkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class DetailViewActivity extends FragmentActivity implements ActionBar.TabListener, View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    NetworkConnUtility networkConnection = null;
    NetworkResp networkResp = new NetworkResp();

    Bundle detailBundle = null;
    private TextView mUserMail;
    private TextView mUserCall;
    private TextView mUserLocate;
    private String mUserMobileNumber;
    private String mUserEmailId;
    private String mProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0f;
        *//*params.flags = WindowManager.LayoutParams.MATCH_PARENT |
                        WindowManager.LayoutParams.MATCH_PARENT  |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;*//*

        this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        Point winSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(winSize);

        // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
        this.getWindow().setLayout((int) ((winSize.x) * 0.9f), (int) ((winSize.y) * 0.9f));*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        networkConnection = new NetworkConnUtility(getApplicationContext());
        detailBundle = getIntent().getBundleExtra("post_detail");


        // Set up the action bar.
        /*final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);*/


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
       mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), detailBundle);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
       for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            /*actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));*/
        }

        if (networkConnection != null) {
            networkConnection.setNetworkListener(networkResp);
            networkConnection.getUser(detailBundle.getString("username"));
        }

        mUserCall = (TextView) findViewById(R.id.user_call);
        mUserMail = (TextView) findViewById(R.id.user_message);
        mUserLocate = (TextView) findViewById(R.id.user_locate);

    }

    private class NetworkResp implements NetworkConnUtility.NetworkResponseListener {
        @Override
        public void onResponse(String urlString, String networkResult) {
            if (urlString.equalsIgnoreCase(NetworkConstants.GET_USER)) {
                if (networkResult != null) {
                    try {
                        JSONObject detailObj = new JSONObject(networkResult);
                        mProfileName = detailObj.getString("initials") + detailObj.getString("firstName");
                        detailBundle.putString("profile_name", mProfileName);
                        if (detailObj.has("mobileNumber")) {
                            mUserMobileNumber = detailObj.getString("mobileNumber");
                        }
                        if (detailObj.has("emailId")) {
                            mUserEmailId = detailObj.getString("emailId");
                        }
                        detailBundle.putString("user_image", detailObj.getString("profileImage"));

                        //TODO : Get Lat & Lng from Server
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "Got the User Info ", Toast.LENGTH_SHORT).show();

                /*mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), detailBundle);

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.pager);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                // When swiping between different sections, select the corresponding
                // tab. We can also use ActionBar.Tab#select() to do this if we have
                // a reference to the Tab.
                mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        //actionBar.setSelectedNavigationItem(position);
                    }
                });*/
            } else if (urlString.equalsIgnoreCase(NetworkConstants.RESERVE_ITEM)) {
                if (networkResult != null)
                    Toast.makeText(getApplicationContext(), "Post is reserved for " + detailBundle.getString("currentUser") + " Thank you ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Bundle mPostBundle = null;

        public SectionsPagerAdapter(FragmentManager fm, Bundle postBundle) {
            super(fm);
            mPostBundle = postBundle;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, mPostBundle);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onClick(View optionView) {
        int optionId = optionView.getId();

        switch (optionId) {
            case R.id.user_call:
                Toast.makeText(getApplicationContext(), "Call option clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mUserMobileNumber));
                startActivity(intent);
                break;

            case R.id.user_message:
                Toast.makeText(getApplicationContext(), "Mail option clicked", Toast.LENGTH_SHORT).show();
                String[] TO = {mUserEmailId};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Interest on your Rental Post");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mProfileName != null ? mProfileName : "" + "I am interested in your Rental Post. Please contact me, if it is available. Thanks :)");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;

            case R.id.user_locate:
                //TODO - Launch The Maps
                Toast.makeText(getApplicationContext(), "Locate option clicked", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=12.949652,77.710938&daddr=13.949652,78.710938"));
                intent.setComponent(new ComponentName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity"));
                startActivity(intent);*/
                break;

            case R.id.user_reservation:
                Toast.makeText(getApplicationContext(), "Reserve option clicked", Toast.LENGTH_SHORT).show();
                String currUser = detailBundle.getString("currentUser");
                if (currUser != null) {
                    reserveItemPost(currUser);
                } else {
                    startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data != null) {
            reserveItemPost(data.getStringExtra("User"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reserveItemPost(String current_user) {
        JSONObject resItem = new JSONObject();
        try {
            resItem.put("userName", current_user);
            resItem.put("itemId", detailBundle.getInt("itemId"));
            resItem.put("startTime", System.currentTimeMillis());
            resItem.put("endTime", System.currentTimeMillis());
            resItem.put("reservedTime", System.currentTimeMillis());
            resItem.put("price", detailBundle.getInt("price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (networkConnection != null) {
            networkConnection.reserveItem(resItem.toString());
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        NetworkConnUtility networkConnection = null;
        ImageResp imageResp = new ImageResp();
        ImageView mProfileImage = null;


        private class ImageResp implements NetworkConnUtility.ImageResponseListener {
            @Override
            public void onImageResponse(String urlString, Bitmap imageResult) {
                mProfileImage.setImageBitmap(imageResult);
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Bundle post) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putBundle("post_bundle", post);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            Bundle postDetail = getArguments().getBundle("post_bundle");
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_detail_view, container, false);
                if (postDetail != null) {
                    ((TextView) rootView.findViewById(R.id.section_label)).setText(postDetail.getString("title"));
                    ((TextView) rootView.findViewById(R.id.detail_desc_textView)).setText(postDetail.getString("description"));
                    byte[] bitmapBytes = postDetail.getByteArray("image");
                    if (bitmapBytes != null)
                        ((ImageView) rootView.findViewById(R.id.detail_imageView)).setImageBitmap(BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length));

                    String priceText = getResources().getString(R.string.price_detail_view, postDetail.getInt("price"), postDetail.getString("duration", "Hour"));
                    ((TextView) rootView.findViewById(R.id.price_textView)).setText(priceText);

                    CheckBox fav_check = (CheckBox) rootView.findViewById(R.id.fav_checkbox);
                    fav_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                buttonView.setButtonDrawable(R.drawable.ic_star_black_36dp);
                                buttonView.setText("Added to Favorites");
                            } else {
                                buttonView.setButtonDrawable(R.drawable.ic_star_border_black_36dp);
                                buttonView.setText("Add to Favorites");
                            }
                        }
                    });
                    mProfileImage = ((ImageView) rootView.findViewById(R.id.detail_profile_image));

                    networkConnection = new NetworkConnUtility(getActivity().getApplicationContext());

                    //TODO : Set the profile picture.
                    if (networkConnection != null) {
                        networkConnection.setImageListener(imageResp);
                        networkConnection.getUserProfileImage(postDetail.getString("user_image"));
                    }

                    ((TextView) rootView.findViewById(R.id.detail_profile_name)).setText(postDetail.getString("profile_name"));

                }
            } /*else {
                rootView = inflater.inflate(R.layout.fragment_user_profile_view, container, false);
                Toolbar bottomToolbar = (Toolbar) rootView.findViewById(R.id.bottom_toolbar);

                *//*bottomToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem detailMenuItem) {
                        switch (detailMenuItem.getItemId()) {
                            case R.id.user_call:
                                break;

                            case R.id.user_message:
                                break;

                            case R.id.user_locate:
                                break;
                        }
                        return false;
                    }
                });

                bottomToolbar.inflateMenu(R.menu.detail_view_menu);
                Menu bottomMenu = bottomToolbar.getMenu();
                bottomMenu.findItem(R.id.user_call).setEnabled(false);
                bottomToolbar.invalidate();*//*

                if (postDetail != null) {
                    //TODO : Set the profile picture.
                    ((TextView) rootView.findViewById(R.id.detail_profile_name)).setText(postDetail.getString("username"));
                }
            }*/
            return rootView;
        }
    }
}
