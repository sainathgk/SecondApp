package com.sainath.rentalapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.connection.rentalapp.NetworkConnUtility;
import com.connection.rentalapp.NetworkConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RentalMainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener,
        LocationListener, AdapterView.OnItemSelectedListener {

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private Menu mDrawerMenu;

    private MenuItem mLoginAction;
    private MenuItem mLogoutAction;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    private GoogleMap mMap = null; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleLocation;
    private LatLng myLoc = new LatLng(12.949652, 77.710938);
    public final CameraPosition My_Location =
            new CameraPosition.Builder().target(myLoc)
                    .zoom(17f)
                    .bearing(100)
                    .build();

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private String accUser = null;
    private TextView mEmailText;
    private Spinner mCategorySpin;
    private String accMail = null;
    private String accUserImageUrl = null;
    private TextView mUsernameText;

    NetworkConnUtility networkConnection = new NetworkConnUtility();
    NetworkResp networkResp = new NetworkResp();

    private class PostsData {
        private String mPostId = null;
        private String mPostTitle = null;
        private String mPostDescription = null;
        private String mPostCategory = null;
        private LatLng mLocationCoords = null;
        private int mPostPrice = -1;
        private String mPostDuration = null;
        private byte[] mPostImage = null;

        public String getPostId() {
            return mPostId;
        }

        public void setPostId(String mPostId) {
            this.mPostId = mPostId;
        }

        public String getPostTitle() {
            return mPostTitle;
        }

        public void setPostTitle(String mPostTitle) {
            this.mPostTitle = mPostTitle;
        }

        public String getPostDescription() {
            return mPostDescription;
        }

        public void setPostDescription(String mPostDescription) {
            this.mPostDescription = mPostDescription;
        }

        public String getPostCategory() {
            return mPostCategory;
        }

        public void setPostCategory(String mPostCategory) {
            this.mPostCategory = mPostCategory;
        }

        public LatLng getLocationCoords() {
            return mLocationCoords;
        }

        public void setLocationCoords(LatLng mLocationCoords) {
            this.mLocationCoords = mLocationCoords;
        }

        public int getPostPrice() {
            return mPostPrice;
        }

        public void setPostPrice(int mPostPrice) {
            this.mPostPrice = mPostPrice;
        }

        public String getPostDuration() {
            return mPostDuration;
        }

        public void setPostDuration(String mPostDuration) {
            this.mPostDuration = mPostDuration;
        }

        public byte[] getPostImage() {
            return mPostImage;
        }

        public void setPostImage(byte[] mPostImage) {
            this.mPostImage = mPostImage;
        }
    }

    ;

    private List<PostsData> mPostDataList = new ArrayList<PostsData>();
    private HashMap<String, Integer> mIconsList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawerMenu = navigationView.getMenu();
        mLogoutAction = mDrawerMenu.findItem(R.id.logout);
        mLoginAction = mDrawerMenu.findItem(R.id.login_opt);

        mEmailText = ((TextView) findViewById(R.id.email));

        mUsernameText = (TextView) findViewById(R.id.username);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.login_opt:
                        startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.my_ads:
                        if (accUser != null)
                            Toast.makeText(getApplicationContext(), "Menu from Drawer is called", Toast.LENGTH_SHORT).show();
                        else
                            startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.favourites:
                        if (accUser != null)
                            Toast.makeText(getApplicationContext(), "Menu from Drawer is called", Toast.LENGTH_SHORT).show();
                        else
                            startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.invite:
                        break;

                    case R.id.rateus:
                        break;

                    case R.id.logout:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                if (mLogoutAction != null && mLoginAction != null && drawerView != null) {
                    if (accMail != null) {
                        mLogoutAction.setVisible(true);
                        mLoginAction.setVisible(false);
                    } else {
                        mLogoutAction.setVisible(false);
                        mLoginAction.setVisible(true);
                    }

                    drawerView.invalidate();
                }
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        mCategorySpin = (Spinner) findViewById(R.id.spinner_category);

        mCategorySpin.setOnItemSelectedListener(this);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mGoogleLocation = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mIconsList.put("Cars", R.drawable.ic_directions_car_black_36dp);
        mIconsList.put("Bikes", R.drawable.ic_directions_bike_black_36dp);
        mIconsList.put("Cameras", R.drawable.ic_local_see_black_36dp);
        mIconsList.put("Printers", R.drawable.ic_local_printshop_black_36dp);
        mIconsList.put("Mobiles or Tablets", R.drawable.ic_phone_android_black_36dp);
        mIconsList.put("Televisions", R.drawable.ic_tv_black_36dp);
        mIconsList.put("Others", R.mipmap.ic_launcher);

        if (networkConnection != null) {
            networkConnection.setNetworkListener(networkResp);
        }
    }

    private class NetworkResp implements NetworkConnUtility.NetworkResponseListener {
        @Override
        public void onResponse(String urlString, String networkResult) {
            //Toast.makeText(getApplicationContext(), "HTTP Connection is Done " + networkResult, Toast.LENGTH_SHORT).show();
            if (urlString.equalsIgnoreCase(accUserImageUrl)) {
                Log.i("Sainath", "Hello");
                // TODO : convert the Image string to Bitmap & set to Profile
            }

            if (urlString.equalsIgnoreCase(NetworkConstants.GET_ITEMS)) try {
                JSONObject searchResult = new JSONObject(networkResult);
                JSONObject itemDetails;
                JSONArray resultArray = searchResult.getJSONArray("items");
                int itemCount = resultArray.length();
                for (int itemIdx = 0; itemIdx < itemCount; itemIdx++) {
                    itemDetails = resultArray.getJSONObject(itemIdx);

                    PostsData postsData = new PostsData();
                    postsData.setPostTitle(itemDetails.getString("name"));
                    postsData.setPostDescription(itemDetails.getString("description"));
                    postsData.setPostCategory(itemDetails.getString("category"));
                    postsData.setPostPrice(itemDetails.getInt("price"));
                    String[] duration = getResources().getStringArray(R.array.post_duration);
                    postsData.setPostDuration(duration[itemDetails.getInt("duration")]);
                    //postsData.setPostImage(rentalCursor.getBlob(rentalCursor.getColumnIndex("POST_IMAGE")));

                       /* Double latLocation = rentalCursor.getDouble(rentalCursor.getColumnIndex("POST_LATITUDE"));
                        Double longLocation = rentalCursor.getDouble(rentalCursor.getColumnIndex("POST_LONGITUDE"));*/

                    postsData.setLocationCoords(myLoc);

                    mPostDataList.add(postsData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            addMarkersToMap();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mCategorySelected = parent.getItemAtPosition(position).toString();

        JSONObject searchList = new JSONObject();
        JSONObject category = new JSONObject();
        JSONObject subcategory = new JSONObject();
        JSONArray search = new JSONArray();

        try {
            category.put("name", "CATEGORY");
            category.put("value", mCategorySelected);
            subcategory.put("name", "SUBCATEGORY");
            subcategory.put("value", mCategorySelected);

            search.put(category);
            search.put(subcategory);

            searchList.put("searchList", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (networkConnection != null) {
            networkConnection.getItemsByCategory(searchList.toString());
        }

        Toast.makeText(getApplicationContext(), "Item Selected is " + mCategorySelected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userProfile", accUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        mSearchAction = menu.findItem(R.id.action_search);

        return super.onCreateOptionsMenu(menu);
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

        if (id == R.id.action_search) {
            handleMenuSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton) {
            startActivity(new Intent(this, PostMainActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleLocation.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleLocation.connect();
        /*if (mMap != null)
            mMap.clear();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data != null) {
            accUser = data.getStringExtra("User");
            JSONObject userObj = null;

            try {
                userObj = new JSONObject(accUser);
                if (userObj != null) {
                    if (mEmailText != null) {
                        accMail = userObj.getString("emailId");
                        mEmailText.setText(accMail);
                        mEmailText.setVisibility(View.VISIBLE);
                    }
                    if (mUsernameText != null) {
                        mUsernameText.setText("Hello " + userObj.getString("firstName"));
                    }
                    accUserImageUrl = userObj.getString("Image");
                    if (networkConnection != null && accUserImageUrl != null) {
                        networkConnection.getUserProfileImage(accUserImageUrl);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent markerIntent = new Intent(this, DetailViewActivity.class);
        Bundle detailPost = new Bundle();


        int size = mPostDataList.size();
        for (int pos = 0; pos < size; pos++) {
            if (mPostDataList.get(pos).getPostId().equalsIgnoreCase(marker.getId())) {
                detailPost.putString("title", mPostDataList.get(pos).getPostTitle());
                detailPost.putString("description", mPostDataList.get(pos).getPostDescription());
                detailPost.putInt("price", mPostDataList.get(pos).getPostPrice());
                detailPost.putString("duration", mPostDataList.get(pos).getPostDuration());
                detailPost.putByteArray("image", mPostDataList.get(pos).getPostImage());
                markerIntent.putExtra("post_detail", detailPost);
                break;
            }
        }

        startActivity(markerIntent);
    }

    private void addMarkersToMap() {
        if (mPostDataList != null && mPostDataList.size() > 0) {
            MarkerOptions myMarker_3 = new MarkerOptions();
            for (int position = 0; position < mPostDataList.size(); position++) {
                if (myMarker_3 != null) {
                    myMarker_3.position(mPostDataList.get(position).getLocationCoords());
                    myMarker_3.title(mPostDataList.get(position).getPostTitle());
                    myMarker_3.snippet(mPostDataList.get(position).getPostDescription());
                    String category = mPostDataList.get(position).getPostCategory();
                    int resId;
                    if (mIconsList.containsKey(category)) {
                        resId = mIconsList.get(category);
                    } else {
                        resId = mIconsList.get("Others");
                    }
                    myMarker_3.icon(BitmapDescriptorFactory.fromResource(resId));
                    mPostDataList.get(position).setPostId(mMap.addMarker(myMarker_3).getId());
                } else {
                    Toast.makeText(getApplicationContext(), "Marker Options is null", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocation);
        if (loc != null) {
            myLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            /*MarkerOptions myMarker =
                    new MarkerOptions()
                            .position(lng)
                            .title("Sony Cyber shot DSC-HX400V")
                            .snippet("Owned by Jeelani Basha")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_see_black_36dp));
            mMap.addMarker(myMarker);
            LatLng lng_1 = new LatLng(loc.getLatitude() + 0.00210, loc.getLongitude());
            MarkerOptions myMarker_1 =
                    new MarkerOptions()
                            .position(lng_1)
                            .title("Audi Q3")
                            .snippet("Owned by Venkata Narayana")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_36dp));
            mMap.addMarker(myMarker_1);
            LatLng lng_2 = new LatLng(loc.getLatitude(), loc.getLongitude() + 0.00210);
            MarkerOptions myMarker_2 =
                    new MarkerOptions()
                            .position(lng_2)
                            .title("Kawasaki Ninja 300")
                            .snippet("Owned by Prasada")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_36dp));
            mMap.addMarker(myMarker_2);
            LatLng lng_3 = new LatLng(loc.getLatitude() - 0.00110, loc.getLongitude() - 0.00110);
            MarkerOptions myMarker_3 =
                    new MarkerOptions()
                            .position(lng_3)
                            .title("HP LaserJet Pro 100")
                            .snippet("Owned by Sainath");

            myMarker_3.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_printshop_black_36dp));

            mMap.addMarker(myMarker_3);*/
            //LatLng lng_3 = new LatLng(loc.getLatitude() - 0.00110, loc.getLongitude() - 0.00110);
            /*if (mPostDataList != null && mPostDataList.size() > 0) {
                MarkerOptions myMarker_3 = new MarkerOptions();
                for (int position = 0; position < mPostDataList.size(); position++) {
                    if (myMarker_3 != null) {
                        myMarker_3.position(mPostDataList.get(position).getLocationCoords());
                        myMarker_3.title(mPostDataList.get(position).getPostTitle());
                        myMarker_3.snippet(mPostDataList.get(position).getPostDescription());
                        String category = mPostDataList.get(position).getPostCategory();
                        int resId;
                        if (mIconsList.containsKey(category)) {
                            resId = mIconsList.get(category);
                        } else {
                            resId = mIconsList.get("Others");
                        }
                        myMarker_3.icon(BitmapDescriptorFactory.fromResource(resId));
                        mPostDataList.get(position).setPostId(mMap.addMarker(myMarker_3).getId());
                    } else {
                        Toast.makeText(getApplicationContext(), "Marker Options is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }*/
            CameraPosition Location =
                    new CameraPosition.Builder().target(myLoc)
                            .zoom(18f)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Location));
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "My Location Button is Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            mCategorySpin.setVisibility(View.VISIBLE);
            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            mCategorySpin.setVisibility(View.GONE);
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));

            isSearchOpened = true;
        }
    }

    private void doSearch() {
        //
    }
}
