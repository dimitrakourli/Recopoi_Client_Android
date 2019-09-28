package com.example.user.myapplicationrecopoi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> markers = new ArrayList();
    private MarkerOptions options = new MarkerOptions();
    private LatLng now = new LatLng(MainActivity.latitude, MainActivity.longitude);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the custom marker information window
        if(mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    ImageView info_photo = (ImageView) v.findViewById(R.id.info_photo);
                    TextView info_title = (TextView) v.findViewById(R.id.info_title);
                    TextView info_category = (TextView) v.findViewById(R.id.info_category);

                    if(marker.getTitle().equals("Home")) {
                        Picasso.get().load("https://i.imgur.com/zgrt8K3.jpg").into(info_photo);

                    } else {
                        Picasso.get().load(marker.getTag().toString()).into(info_photo);
                        info_category.setText(marker.getSnippet());
                    }
                    info_title.setText(marker.getTitle());

                    return v;
                }
            });
        }

        for (int i=0; i<MainActivity.pois.size(); i++) {
            LatLng location = new LatLng(MainActivity.pois.get(i).getLatidude(), MainActivity.pois.get(i).getLongitude());
            options.position(location);
            options.title(MainActivity.pois.get(i).getPOI_name());
            options.snippet(MainActivity.pois.get(i).getPOI_category_id());
            markers.add(mMap.addMarker(options));
            markers.get(i).setTag(MainActivity.pois.get(i).getPhotos());
        }

        LatLng initial_location = new LatLng(MainActivity.latitude,MainActivity.longitude);
        options.position(initial_location);
        options.title("Home");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        markers.add(mMap.addMarker(options));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        //auto einai pou xreiazetai panta
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

}