package com.example.app_usuario;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class Principal extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener  {

    private EditText et_ubicacion;
    private TextView tv_inicio, tv_final,tv_tiempo,tv_conductor, tv_disponibilidad,tv_iniciotxt,
            tv_finaltxt,tv_tiempotxt,tv_conductortxt, tv_disponibilidadtxt, tv_rutas, tv_ficha, tv_rutastxt;
    private ImageView img_conductor;

    private GoogleMap mMap;
    private boolean UbiAct = false;
    private Marker markerPerso;
    private String info= "CDMX",direccion ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        et_ubicacion = (EditText)findViewById(R.id.txt_c_ubicacion);
        /*
        tv_inicio = (TextView)findViewById(R.id.tv_c_inicio);
        tv_final = (TextView)findViewById(R.id.tv_c_final);
        tv_tiempo = (TextView)findViewById(R.id.tv_c_tiempo);
        tv_conductor = (TextView)findViewById(R.id.tv_c_conductor);
        tv_disponibilidad = (TextView)findViewById(R.id.tv_c_disponibilidad);
        tv_iniciotxt = (TextView)findViewById(R.id.tv_c_iniciotxt);
        tv_finaltxt = (TextView)findViewById(R.id.tv_c_finaltxt);
        tv_tiempotxt = (TextView)findViewById(R.id.tv_c_tiempotxt);
        tv_conductortxt = (TextView)findViewById(R.id.tv_c_conductortxt);
        tv_disponibilidadtxt = (TextView)findViewById(R.id.tv_c_disponibilidadtxt);
        */
        tv_rutas = (TextView)findViewById(R.id.tv_c_rutas);
        tv_rutastxt = (TextView)findViewById(R.id.tv_c_rutastxt);
        tv_ficha = (TextView)findViewById(R.id.tv_c_ficha);

       // img_conductor = (ImageView)findViewById(R.id.img_c_conductor);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck!= PackageManager.PERMISSION_GRANTED && permissionCheck2!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            return;
        }

        if(!mMap.isMyLocationEnabled()){
            LatLng CDMX= new LatLng(19.3168, -99.08671 );
            mMap.addMarker(new MarkerOptions().position(CDMX).title("Marcador en la CDMX"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(CDMX));
        }

        LocationManager locationManager = (LocationManager) Principal.this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(!mMap.isMyLocationEnabled()&&!UbiAct){
                    mMap.clear();
                    LatLng TiempoReal= new LatLng(location.getLatitude(), location.getLongitude() );
                    markerPerso = googleMap.addMarker(new MarkerOptions().position(TiempoReal).draggable(true).title("Tu ubicacion"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal));
                    info = Principal.this.setLocation(location);
                    UbiAct=true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        Log.d("debug", "LocationProvider.AVAILABLE");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(Principal.this,"GPS Activado", LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(Principal.this,"GPS Desactivado", LENGTH_SHORT).show();
            }
        } ;

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.equals(markerPerso)){
            com.example.app_usuario.Fragmento.newInstance(marker.getTitle(),info).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(markerPerso)){
            Toast.makeText(this,"Manten apretado para mover ubicacion",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.equals(markerPerso)){
            Toast.makeText(this,"Posicion", LENGTH_SHORT).show();
            String newTitle = String.format(Locale.getDefault(), getString(R.string.marker_detail_lating), marker.getPosition().latitude, marker.getPosition().longitude);
            setTitle(newTitle);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(marker.equals(markerPerso)){
            setTitle(R.string.app_name);
            if(markerPerso.getPosition().longitude !=0.0 && markerPerso.getPosition().latitude !=0.0) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                List<Address> list;
                try {
                    list = geocoder.getFromLocation(
                            markerPerso.getPosition().latitude, markerPerso.getPosition().longitude, 1);
                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);

                        direccion = DirCalle.getAddressLine(0);
                        info = direccion;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            markerPerso.setTitle("Tu ubicacion");
        }
    }

    public String setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (!mMap.isMyLocationEnabled()) {
            try {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                    direccion = DirCalle.getAddressLine(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return direccion;
    }
}

