package com.sainath.rentalapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RentalMainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener,
        LocationListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
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

    private class PostsData {
        private String mPostTitle = null;
        private String mPostDescription = null;
        private String mPostCategory = null;
        private LatLng mLocationCoords = null;

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
    };

    private List<PostsData> mPostDataList = new ArrayList<PostsData>();
    private HashMap<String, Integer> mIconsList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mGoogleLocation = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mIconsList.put("Car",R.drawable.ic_directions_car_black_36dp);
        mIconsList.put("Bike",R.drawable.ic_directions_bike_black_36dp);
        mIconsList.put("Camera", R.drawable.ic_local_see_black_36dp);
        mIconsList.put("Printer", R.drawable.ic_local_printshop_black_36dp);

        /*getApplicationContext().getContentResolver().registerContentObserver(Uri.parse("content://com.rentalapp/POST_TABLE"), true, new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Toast.makeText(getApplicationContext(), "Data is Inserted", Toast.LENGTH_SHORT).show();
                Cursor rentalCursor = getApplicationContext().getContentResolver().query(Uri.parse("content://com.rentalapp/POST_TABLE"), null, null, null, null);
                if(rentalCursor != null && rentalCursor.moveToFirst())
                {
                    String name = rentalCursor.getString(rentalCursor.getColumnIndex("POST_TITLE"));

                    if(mMap != null)
                    {
                        mMap.addMarker(new MarkerOptions().position(myLoc).title(name));
                    }
                }
            }
        });*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.rental_main, menu);
            restoreActionBar();
            return true;
        }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageButton)
        {
            Toast.makeText(getApplicationContext(), "Search Activity will be launched", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.button)
        {
            //TBD Launch Add Post Activity
            //Toast.makeText(getApplicationContext(), "Post a Rental Ad Activity will be Launched ", Toast.LENGTH_SHORT).show();
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
        if(mMap != null)
            mMap.clear();

        Cursor rentalCursor = getApplicationContext().getContentResolver().query(Uri.parse("content://com.database.rentalapp/POST_TABLE"), null, null, null, null);
        if(rentalCursor != null && rentalCursor.getCount() > 0)
        {
            mPostDataList.clear();

            while (rentalCursor.moveToNext()) {
                String name = rentalCursor.getString(rentalCursor.getColumnIndex("POST_TITLE"));

                Log.i("Sainath", "DB Entry - " + name);
                PostsData postsData = new PostsData();
                postsData.setPostTitle(name);
                postsData.setPostDescription(rentalCursor.getString(rentalCursor.getColumnIndex("POST_DESCRIPTION")));
                postsData.setPostCategory(rentalCursor.getString(rentalCursor.getColumnIndex("POST_CATEGORY")));

                mPostDataList.add(postsData);
            }
            rentalCursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1001)
        {
            String accName = data.getStringExtra("Name");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getApplicationContext(), marker.getTitle() + " Detail Description View will be launched", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleLocation,
                REQUEST,
                this);*/
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocation);
        LatLng lng = new LatLng(loc.getLatitude(), loc.getLongitude());
        MarkerOptions myMarker =
                new MarkerOptions()
                        .position(lng)
                        .title("Sony Cyber shot DSC-HX400V")
                        .snippet("Owned by Jeelani Basha")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_see_black_36dp));
        mMap.addMarker(myMarker);
        LatLng lng_1 = new LatLng(loc.getLatitude()+0.00210, loc.getLongitude());
        MarkerOptions myMarker_1 =
                new MarkerOptions()
                        .position(lng_1)
                        .title("Audi Q3")
                        .snippet("Owned by Venkata Narayana")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_36dp));
        mMap.addMarker(myMarker_1);
        LatLng lng_2 = new LatLng(loc.getLatitude(), loc.getLongitude()+0.00210);
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

        mMap.addMarker(myMarker_3);
        /*LatLng lng_3 = new LatLng(loc.getLatitude() - 0.00110, loc.getLongitude() - 0.00110);
        if(mPostDataList != null && mPostDataList.size() > 0) {
            for (int position = 0; position < mPostDataList.size(); position++) {
                MarkerOptions myMarker_3 =
                        new MarkerOptions()
                                .position(lng_3)
                                .title(mPostDataList.get(position).getPostTitle())
                                .snippet(mPostDataList.get(position).getPostDescription())
                                .icon(BitmapDescriptorFactory.fromResource(mIconsList.get(mPostDataList.get(position).getPostCategory())));
                mMap.addMarker(myMarker_3);
            }
        }*/
        CameraPosition Location =
                new CameraPosition.Builder().target(lng)
                        .zoom(16f)
                        .bearing(100)
                        .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Location));
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
        //googleMap.setMyLocationEnabled(true);
        //googleMap.setOnMyLocationButtonClickListener(this);
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(My_Location));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "My Location Button is Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
          /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rental_main, container, false);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((RentalMainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
