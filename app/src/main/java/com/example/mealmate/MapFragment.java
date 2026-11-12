package com.example.mealmate;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;





public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap Map;
    private EditText edtSearch;
    private Button btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Load layout
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        // Get SupportMapFragment and request asynchronous map loading
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Search button listener
        btnSearch.setOnClickListener(v -> searchLocation());

        return view;
    }

    // Search for a location based on user input
    private void searchLocation() {
        String location = edtSearch.getText().toString().trim();
        if (location.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a location name", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            // Return only one matching result
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                // Clear previous markers
                Map.clear();
                // Add a new marker on the map
                Map.addMarker(new MarkerOptions().position(latLng).title(location));
                // Move the map camera and set zoom level
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Map = googleMap;
    }
}