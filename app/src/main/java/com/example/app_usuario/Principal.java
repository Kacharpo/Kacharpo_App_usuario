package com.example.app_usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_usuario.Controlador.PagerController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class Principal extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener  {

    private AutoCompleteTextView at_ubicacion, at_destino;
    private EditText et_ubicacion;
    private TextView tv_inicio, tv_final,tv_tiempo,tv_conductor, tv_disponibilidad,tv_iniciotxt,
            tv_finaltxt,tv_tiempotxt,tv_conductortxt, tv_disponibilidadtxt, tv_rutas, tv_ficha;
    private ImageView img_conductor;
    private MarkerOptions marker1 = null , marker2 = null;
    /*private ListView lv_rutas;
    private String rutas [] = {"Inicio","Final","Tiempo","Conductor","Disponibilidad"};
    private String info_r [] = {"12:00","13:00","1h","Manuel Perez","10 lugares"};*/

    LatLng TiempoReal= null, TiempoReal2 = null;
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;

    private Spinner sp_rutas;
    private ViewPager vp_mostrar;
    private TabLayout tl_opcion;
    private TabItem ti_inicio,ti_final,ti_tiempo,ti_conductor,ti_disponibilidad;
    PagerController pagerAdapter;

    private GoogleMap mMap;
    private boolean UbiAct = false, UbiA=false;
    private Marker markerPerso ,markerPerso2;
    private String info= "CDMX",info2 = "",direccion ;

    Thread reloj;
    int seg = 0;
    Handler h = new Handler();
    boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        at_ubicacion = (AutoCompleteTextView)findViewById(R.id.atxt_c_ubicacion);
        at_destino = (AutoCompleteTextView)findViewById(R.id.atxt_c_destino);

        tv_ficha = (TextView)findViewById(R.id.tv_c_ficha);
        //lv_rutas = (ListView)findViewById(R.id.lv_c_rutas);
        vp_mostrar = (ViewPager)findViewById(R.id.vp_c_mostrar);
        tl_opcion = (TabLayout)findViewById(R.id.tl_v_opcion);
        ti_inicio = (TabItem)findViewById(R.id.ti_c_inicio);
        ti_final = (TabItem)findViewById(R.id.ti_c_final);
        ti_tiempo = (TabItem)findViewById(R.id.ti_c_tiempo);
        ti_conductor = (TabItem)findViewById(R.id.ti_c_conductor);
        ti_disponibilidad = (TabItem)findViewById(R.id.ti_c_disponibilidad);
        sp_rutas = (Spinner)findViewById(R.id.sp_c_rutas);
        //Array para spinner
        String [] tipo = {"Rutas","Euroban", "Urban", "Combi"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo);
        sp_rutas.setAdapter(adapter1);
        //Array para AutoCompliteText
        final ArrayAdapter<String > adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,COUNTRIES);
        at_ubicacion.setAdapter(adapter2);
        at_destino.setAdapter(adapter2);
        //PagerController para Fragment
        pagerAdapter = new PagerController(getSupportFragmentManager(),tl_opcion.getTabCount());
        vp_mostrar.setAdapter(pagerAdapter);
        tl_opcion.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_mostrar.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    default:
                        pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vp_mostrar.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl_opcion));
        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_item,rutas);
        lv_rutas.setAdapter(adapter);
        lv_rutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), lv_rutas.getItemAtPosition(position)+": "+info_r[position], LENGTH_SHORT).show();
            }
        }); */
        // img_conductor = (ImageView)findViewById(R.id.img_c_conductor);

        //SupportMapFragment para uso de GoogleMaps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    //Configuracion de Google Maps
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Asigna valor a variable mMap de tipo GoogleMap
        mMap = googleMap;
        //Permiso de localizacion
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //Intent para cambio de Activity
        Intent refresh = new Intent(getApplicationContext(),Principal.class);
        //Checar y pedir permiso
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permiso de ubicacion accedido", LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            try {
                for(int e=0;e<=20;e++){
                    Thread.sleep(500);
                    permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                        startActivity(refresh);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ;
        }
        //LocationManager para utilizar localizacion
        LocationManager locationManager = (LocationManager) Principal.this.getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Agregar marcador si la ubcacion esta desactivada
        if(!mMap.isMyLocationEnabled()){
            TiempoReal= new LatLng(19.3168, -99.08671 );
            marker1 = new MarkerOptions().position(TiempoReal).draggable(true).title("Punto de Inicio");
            markerPerso = googleMap.addMarker(marker1);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(CDMX));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TiempoReal,18),5000,null);
            UbiA = true;
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
        //Agregar marcador si la ubicacion esta activada
        if(loc != null){
            mMap.clear();
            TiempoReal = new LatLng(loc.getLatitude(), loc.getLongitude() );
            marker1 = new MarkerOptions().position(TiempoReal).draggable(true).title("Punto de Inicio");
            markerPerso = googleMap.addMarker(marker1);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(ubi));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TiempoReal,18),5000,null);
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
        //Configuracion de Ubicacion en tiempo real
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(UbiA==true){
                    mMap.clear();
                    TiempoReal= new LatLng(location.getLatitude(), location.getLongitude() );
                    markerPerso = googleMap.addMarker(new MarkerOptions().position(TiempoReal).draggable(true).title("Punto de Inicio"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal));
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TiempoReal,18),5000,null);
                    UbiA=false;
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
                UbiAct = true;
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(Principal.this,"GPS Desactivado", LENGTH_SHORT).show();
            }
        } ;
        //Sobreescribir funcion setOnClicklistener para AutoCompleteText Ubicacion
        at_ubicacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMap.clear();

                double lat=0.0,lon=0.0;
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> list = null;
                int j=0;
                for( int i=0;i<=COUNTRIES.length-1;i++){
                    if(COUNTRIES[i].equals(at_ubicacion.getText().toString())){
                        try {
                            lat = COR[i][0]; lon = COR[i][1];
                            list = geocoder.getFromLocation(lat,lon , 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        j=i;
                    }
                }


                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                TiempoReal= new LatLng(lat,lon );
                marker1 = new MarkerOptions().position(TiempoReal).draggable(true).title("Inicio "+COUNTRIES[j]);
                markerPerso = googleMap.addMarker(marker1);
                if(marker2 != null){
                    markerPerso2 = googleMap.addMarker(marker2);
                    points.add(TiempoReal);
                    points.add(TiempoReal2);
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.GREEN);
                    mMap.addPolyline(lineOptions);
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TiempoReal,18),5000,null);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                    direccion = DirCalle.getAddressLine(0);
                    info = direccion;
                }

            }

        });
        //Sobreescribir funcion setOnKeyListener para AutoCompleteText Ubicacion
        at_ubicacion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    int i = 0, j = 0, p = 0,c=0;
                    String ubicacion = at_ubicacion.getText().toString(), ubi = "", lat = "", lon = "";
                    if (!ubicacion.isEmpty()) {//bienestarsocial
                        for (; i <= at_ubicacion.length() - 1; i++) {
                            if (!ubicacion.substring(i, i + 1).equals(" ")&&!ubicacion.substring(i, i + 1).equals("+")&&!ubicacion.substring(i, i + 1).equals("#")&&!ubicacion.substring(i, i + 1).equals("(")&&!ubicacion.substring(i, i + 1).equals(")")&&!ubicacion.substring(i, i + 1).equals("/")&&!ubicacion.substring(i, i + 1).equals(";")&&!ubicacion.substring(i, i + 1).equals("*")&&!ubicacion.substring(i, i + 1).equals("N")) {
                                j++;
                                ubi = ubi + ubicacion.substring(i, i + 1);

                                if(ubicacion.substring(i, i + 1).equals("."))p++;
                                if (ubicacion.substring(i, i + 1).equals(",") && c == 0) {
                                    lat = ubi.substring(0, j - 1);
                                    ubi = "";

                                }
                                if(ubicacion.substring(i, i + 1).equals(","))c++;

                                lon = ubi;
                            }
                        }

                        int a, b, d,m;
                        if (!lat.equals("") && !lon.equals("")&&c==1&&p<=2) {
                            for (a = 0, c = 0,m=0; a <= lat.length() - 1; a++) {
                                if (lat.substring(a, a + 1).equals(".") ) {

                                    c++;

                                }
                                if (lat.substring(a, a + 1).equals("-") ) {

                                    m++;

                                }
                            }
                            if(m==0){
                                lat="+"+lat;
                            }
                            if(c==0){
                                lat=lat+".000000";
                            } else if(c==1){
                                lat=lat+"000000";
                            }

                            for (b = 0, d = 0; b <= lon.length() - 1; b++) {
                                if (lat.substring(b, b + 1).equals(".")){
                                    d++;

                                }
                            }
                            if (c<=1 && d<=1) {
                                Toast.makeText(getApplicationContext(), "Buscando", LENGTH_SHORT).show();

                                double latD = Double.parseDouble(lat), lonD = Double.parseDouble(lon);
                                if(latD>0)latD=+latD;
                                mMap.clear();

                                points = new ArrayList<LatLng>();
                                lineOptions = new PolylineOptions();

                                LatLng Ingresada = new LatLng(latD, lonD);
                                if(latD>-80&&lonD<80&&lonD>-180&&lonD<180){
                                    marker1 = new MarkerOptions().position(Ingresada).draggable(true).title("Inicio");
                                    markerPerso = googleMap.addMarker(marker1);
                                    if(marker2 != null){
                                        markerPerso2 = googleMap.addMarker(marker2);
                                        points.add(Ingresada);
                                        points.add(TiempoReal2);
                                        lineOptions.addAll(points);
                                        lineOptions.width(5);
                                        lineOptions.color(Color.GREEN);
                                        mMap.addPolyline(lineOptions);
                                    }
                                    markerPerso = googleMap.addMarker(new MarkerOptions().position(Ingresada).draggable(true).title("Marcador de tu Ubicacion"));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Ingresada,18),5000,null);

                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> list = null;
                                    try {
                                        list = geocoder.getFromLocation(latD, lonD, 1);
                                        if (!list.isEmpty()) {
                                            Address DirCalle = list.get(0);

                                            direccion = DirCalle.getAddressLine(0);
                                            info = direccion;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Coordenadas invalidas", LENGTH_SHORT).show();
                                }

                            }

                        }

                        if(at_ubicacion.length()==5){

                        }
                        // et_edt_Ubi.setText("Coordenadas: "+ lat +" "+lon);
                    }
                }
                return false;
            }
        });
        //Sobreescribir funcion setOnItenClickListener para AutoCompleteText Destino
        at_destino.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMap.clear();
                double lat=0.0,lon=0.0;
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> list = null;
                int j=0;
                for( int i=0;i<=COUNTRIES.length-1;i++){
                    if(COUNTRIES[i].equals(at_destino.getText().toString())){
                        try {
                            lat = COR[i][0]; lon = COR[i][1];
                            list = geocoder.getFromLocation(lat,lon , 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        j=i;
                    }
                }

                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                TiempoReal2= new LatLng(lat,lon );
                marker2 = new MarkerOptions().position(TiempoReal2).draggable(true).title("Destino "+COUNTRIES[j]);
                markerPerso = googleMap.addMarker(marker1);
                markerPerso2 = googleMap.addMarker(marker2);
                points.add(TiempoReal);
                points.add(TiempoReal2);
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
                mMap.addPolyline(lineOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TiempoReal2,18),5000,null);

                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                    direccion = DirCalle.getAddressLine(0);
                    info2 = direccion;
                }

            }

        });
        //Sobreescribir funcion setOnKeyListener para AutoCompleteText Destino
        at_destino.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    int i = 0, j = 0, p = 0,c=0;
                    String ubicacion = at_destino.getText().toString(), ubi = "", lat = "", lon = "";
                    if (!ubicacion.isEmpty()) {//bienestarsocial
                        for (; i <= at_destino.length() - 1; i++) {
                            if (!ubicacion.substring(i, i + 1).equals(" ")&&!ubicacion.substring(i, i + 1).equals("+")&&!ubicacion.substring(i, i + 1).equals("#")&&!ubicacion.substring(i, i + 1).equals("(")&&!ubicacion.substring(i, i + 1).equals(")")&&!ubicacion.substring(i, i + 1).equals("/")&&!ubicacion.substring(i, i + 1).equals(";")&&!ubicacion.substring(i, i + 1).equals("*")&&!ubicacion.substring(i, i + 1).equals("N")) {
                                j++;
                                ubi = ubi + ubicacion.substring(i, i + 1);

                                if(ubicacion.substring(i, i + 1).equals("."))p++;
                                if (ubicacion.substring(i, i + 1).equals(",") && c == 0) {
                                    lat = ubi.substring(0, j - 1);
                                    ubi = "";

                                }
                                if(ubicacion.substring(i, i + 1).equals(","))c++;

                                lon = ubi;
                            }
                        }

                        int a, b, d,m;
                        if (!lat.equals("") && !lon.equals("")&&c==1&&p<=2) {
                            for (a = 0, c = 0,m=0; a <= lat.length() - 1; a++) {
                                if (lat.substring(a, a + 1).equals(".") ) {

                                    c++;

                                }
                                if (lat.substring(a, a + 1).equals("-") ) {

                                    m++;

                                }
                            }
                            if(m==0){
                                lat="+"+lat;
                            }
                            if(c==0){
                                lat=lat+".000000";
                            } else if(c==1){
                                lat=lat+"000000";
                            }

                            for (b = 0, d = 0; b <= lon.length() - 1; b++) {
                                if (lat.substring(b, b + 1).equals(".")){
                                    d++;

                                }
                            }
                            if (c<=1 && d<=1) {
                                Toast.makeText(getApplicationContext(), "Buscando", LENGTH_SHORT).show();

                                double latD = Double.parseDouble(lat), lonD = Double.parseDouble(lon);
                                if(latD>0)latD=+latD;
                                mMap.clear();
                                points = new ArrayList<LatLng>();
                                lineOptions = new PolylineOptions();

                                LatLng Ingresada = new LatLng(latD, lonD);
                                if(latD>-80&&lonD<80&&lonD>-180&&lonD<180){
                                    marker2 = new MarkerOptions().position(Ingresada).draggable(true).title("Destino");
                                    markerPerso = googleMap.addMarker(marker1);
                                    markerPerso2 = googleMap.addMarker(marker2);
                                    points.add(TiempoReal);
                                    points.add(Ingresada);
                                    lineOptions.addAll(points);
                                    lineOptions.width(5);
                                    lineOptions.color(Color.BLUE);
                                    mMap.addPolyline(lineOptions);

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Ingresada,18),5000,null);

                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> list = null;
                                    try {
                                        list = geocoder.getFromLocation(latD, lonD, 1);
                                        if (!list.isEmpty()) {
                                            Address DirCalle = list.get(0);

                                            direccion = DirCalle.getAddressLine(0);
                                            info2 = direccion;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Coordenadas invalidas", LENGTH_SHORT).show();
                                }

                            }

                        }

                        if(at_destino.length()==5){

                        }
                        // et_edt_Ubi.setText("Coordenadas: "+ lat +" "+lon);
                    }
                }
                return false;
            }
        });
        //Actualizar ubicacion en tiempo real con uso de Internet
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        //Configuracion de interfaz del Google Maps
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        // mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    //Mostrar informacion de maracdor
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.equals(markerPerso)){
            Fragmento.newInstance(marker.getTitle(),info).show(getSupportFragmentManager(), null);
        }
        if(marker.equals(markerPerso2)){

            Fragmento.newInstance(marker.getTitle(),info2).show(getSupportFragmentManager(), null);
        }
    }
    //Mostrar mensaje al dar click en marcador
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(markerPerso)){
            Toast.makeText(this,"Manten apretado para mover ubicacion",Toast.LENGTH_LONG).show();
        }
        if(marker.equals(markerPerso2)){
            Toast.makeText(this,"Manten apretado para mover ubicacion",Toast.LENGTH_LONG).show();
        }
        return false;
    }
    //Null
    @Override
    public void onMarkerDragStart(Marker marker) {

    }
    //Obtner coordenadas de nueva posicion de marcador
    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.equals(markerPerso)){
            Toast.makeText(this,"Posicion", LENGTH_SHORT).show();
            String newTitle = String.format(Locale.getDefault(), getString(R.string.marker_detail_lating), marker.getPosition().latitude, marker.getPosition().longitude);
            setTitle(newTitle);
            markerPerso.setTitle("Punto de Inicio");
            TiempoReal= new LatLng(markerPerso.getPosition().latitude, markerPerso.getPosition().longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(marker.equals(markerPerso2)){
            Toast.makeText(this,"Posicion", LENGTH_SHORT).show();
            String newTitle = String.format(Locale.getDefault(), getString(R.string.marker_detail_lating), marker.getPosition().latitude, marker.getPosition().longitude);
            setTitle(newTitle);
            markerPerso2.setTitle("Punto de Destino");
            TiempoReal2= new LatLng(markerPerso2.getPosition().latitude, markerPerso2.getPosition().longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal2));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //Obtner coordenadas de ultima posicion de marcador
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

            markerPerso.setTitle("Punto de inicio");

            TiempoReal= new LatLng(markerPerso.getPosition().latitude, markerPerso.getPosition().longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal));

        }
        if(marker.equals(markerPerso2)){
            setTitle(R.string.app_name);
            if(markerPerso2.getPosition().longitude !=0.0 && markerPerso2.getPosition().latitude !=0.0) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                List<Address> list;
                try {
                    list = geocoder.getFromLocation(
                            markerPerso2.getPosition().latitude, markerPerso2.getPosition().longitude, 1);
                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);

                        direccion = DirCalle.getAddressLine(0);
                        info2 = direccion;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            markerPerso2.setTitle("Punto de Destino");

            TiempoReal2 = new LatLng(markerPerso2.getPosition().latitude, markerPerso2.getPosition().longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(TiempoReal2));

        }
    }
    //Matriz de lugares para AutoCompliteText
    private static final String[] COUNTRIES = new String[]{
            "Acambay de Ruíz Castañeda","Acolman","Aculco","Almoloya de Alquisiras","Almoloya de Juárez","Almoloya del Río","Amanalco","Amatepec",
            "Amecameca","Apaxco","Atenco","Atizapán","Atizapán de Zaragoza","Atlacomulco","Atlautla","Axapusco","Ayapango","Calimaya","Capulhuac",
            "Coacalco de Berriozábal","Coatepec Harinas","Cocotitlán","Coyotepec","Cuautitlán","Chalco","Chapa de Mota","Chapultepec","Chiautla",
            "Chicoloapan","Chiconcuac","Chimalhuacán", "Donato Guerra","Ecatepec de Morelos","Ecatzingo","Huehuetoca","Hueypoxtla","Huixquilucan",
            "Isidro Fabela","Ixtapaluca","Ixtapan de la Sal","Ixtapan del Oro","Ixtlahuaca","Xalatlaco","Jaltenco","Jilotepec","Jilotzingo","Jiquipilco",
            "Jocotitlán","Joquicingo","Juchitepec","Lerma","Malinalco","Melchor Ocampo","Metepec","Mexicaltzingo","Morelos","Naucalpan de Juárez",
            "Nezahualcóyotl","Nextlalpan","Nicolás Romero","Nopaltepec","Ocoyoacac","Ocuilan","El Oro","Otumba","Otzoloapan","Otzolotepec","Ozumba",
            "Papalotla","La Paz","Polotitlán","Rayón","San Antonio la Isla", "San Felipe del Progreso","San Martín de las Pirámides","San Mateo Atenco",
            "San Simón de Guerrero","Santo Tomás","Soyaniquilpan de Juárez","Sultepec","Tecámac","Tejupilco","Temamatla","Temascalapa","Temascalcingo",
            "Temascaltepec","Temoaya","Tenancingo","Tenango del Aire", "Tenango del Valle","Teoloyucan","Teotihuacán","Tepetlaoxtoc","Tepetlixpa",
            "Tepotzotlán","Tequixquiac","Texcaltitlán","Texcalyacac","Texcoco","Tezoyuca","Tianguistenco","Timilpan","Tlalmanalco","Tlalnepantla de Baz",
            "Tlatlaya","Toluca","Tonatico","Tultepec","Tultitlán","Valle de Bravo","Villa de Allende","Villa del Carbón","Villa Guerrero","Villa Victoria",
            "Xonacatlán","Zacazonapan","Zacualpan","Zinacantepec","Zumpahuacán", "Zumpango","Cuautitlán Izcalli","Valle de Chalco Solidaridad","Luvianos",
            "San José del Rincón","Tonanitla","Aurrera Express","Hospital Americas"
    };
    //Matriz de coordenadas de lugares para AutoCompliteText
    private static final double[][] COR = new double[][]{
            {19.9543,-99.8441},{19.6395,-98.9121},{20.09833,-99.8269},{18.8657,-99.894},{19.369,-99.7605},{ 19.1586, -99.4886},{ 19.25,-100.017},{18.6807,-100.181},
            {19.1224, -98.7667},{19.9833,-99.1667},{19.2703,-99.5331},{19.431,-99.4025},{19.5562,-99.2675},{19.7968,-99.8765},{19.0206,-98.7783},{19.7233,-98.758},{19.1261,-98.8039},{19.1618,-99.6129},{19.1822,-99.4565},
            {19.62923,-99.10689},{18.9236,-99.7686},{19.2312,-98.864},{19.7764,-99.2082},{19.67241,-99.17615},{19.26174,-98.89775},{19.84754,-99.47325},{19.2003,-99.5603},{19.5494,-98.8828},
            {19.4126,-98.9027},{19.5586,-98.8958},{ 19.4208,-98.949},{19.34937,-100.19412},{19.6097,-99.06},{18.95,-98.75},{19.8342,-99.2033},{19.9106,-99.0755},{ 19.3619,-99.3505},
            {19.5555,-99.4179},{19.31693,-98.89458},{18.8438,-99.6755},{19.2718,-100.268},{19.5689,-99.7669},{19.1796,-99.416},{19.751,-99.0941},{19.9521,-99.5286},{19.8699,-99.0567},{19.5572,-99.6075},
            {19.7549,-99.9165},{19.0726,-99.5111},{19.0911,-98.8816},{19.2864,-99.511},{18.9446,-99.4954},{18.9446,-99.4954},{ 19.2564,-99.6048},{19.2095,-99.5854},{19.7846,-99.6709},{19.475,-99.2374},
            {19.4116,-99.0212},{19.717,-99.067},{19.6198,-99.3114},{19.7758,-98.7123},{19.2727,-99.4597},{18.9665,-99.4138},{19.8028,-100.138},{19.6985,-98.7539},{19.1169,-100.295},{19.4203,-99.5593},{19.0392,-98.7936},
            {19.5622,-98.8578},{ 19.3606,-98.98},{20.2228,-99.8156},{19.146,-99.5819},{ 19.1626,-99.5665},{19.58154,-100.02269},{19.7058,-98.8375},{19.2703,-99.5331},
            {19.0216,-100.007},{18.8441,-100.018},{20.0154,-99.5288},{18.4878,-100.15},{ 19.7131,-98.9683},{18.9075,-100.151},{19.2028,-98.87},{ 19.8314,-98.8997},{19.9147,-100.004},
            {19.0444,-100.045},{19.4654,-99.594},{18.95954,-99.59239},{19.1576,-98.8599},{19.0985,-99.5904},{19.7442,-99.1811},{19.6897,-98.8608},{19.5724,-98.8192},{19.0247,-98.8199},
            {19.70618,-99.23913},{19.908,-99.1457},{18.9285,-99.9356},{19.1316,-99.5003},{19.5126,-98.8798},{19.5914,-98.9131},{19.1125,-99.4346},{19.867,-99.733},{19.2044,-98.8025},{19.539,-99.1933},
            {18.6158,-100.208},{19.2879,-99.6468},{18.8028,-99.67},{19.685,-99.1281},{19.6456,-99.1689},{19.1925,-100.131},{19.3736,-100.147},{19.72234,-99.46158},{18.96,-99.64},{19.4337,-99.9956},
            {19.4,-99.533},{19.0728,-100.255},{18.7836,-98.7594},{19.2833, -99.7333},{18.8346,-99.581},{19.7971,-99.0989},{19.64388,-99.21598},{19.2917,-98.9389},{18.9167,-100.4},
            {19.6115,-100.124},{19.6886,-99.053},{19.586990,-98.9984},{19.5929,-99.018375}
    };
}


