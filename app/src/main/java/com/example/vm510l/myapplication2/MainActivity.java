package com.example.vm510l.myapplication2;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Vector;
import java.util.logging.Logger;

import io.opencensus.tags.Tag;

import static com.example.vm510l.myapplication2.GirlHackathon.body;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private EditText input;
    private TextView output;
    private Button pinChe;
    private int lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mMapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        initView();
        pinChe.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            GirlHackathon gh = new GirlHackathon();
            GirlHackathon my = new GirlHackathon();
            Vector<GirlHackathon> data = new Vector<GirlHackathon>();

            try {
                data = body(my);
            }catch(Exception e){
                Log.d("exc\n",e.toString());}
                gh = data.get(0);
            float a = gh.getGetOnLatitude();
          //  Toast.makeText(MainActivity.this, "结果："+ a, Toast.LENGTH_LONG).show();
            output.setText("    最佳拼车：路线重合率"+gh.getOverlap()+"\n    里程长度："+gh.getDuration()
              +"\n    可拼车对象："+data.size()+"位");
           // Toast.makeText(MainActivity.this, "结果："+ a, Toast.LENGTH_LONG).show();

        }
    };


@Override
public void onMapReady(GoogleMap map) {
    double lat = 39.937795;
    double lng = 116.387224;
    LatLng appointLoc = new LatLng(lat, lng);

    map.addMarker(new MarkerOptions().position(appointLoc).title("Marker"));
    map.moveCamera(CameraUpdateFactory.newLatLng(appointLoc));
}



    @Override
    protected void onResume() {
        super.onResume();
      //  mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
  //      mMapView.onStop();
    }


    @Override
    protected void onPause() {
     //   mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
      //  mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
     //   mMapView.onLowMemory();
    }

    private void initView()
    {
        input=(EditText) findViewById(R.id.txtBegin);
        pinChe=(Button) findViewById(R.id.button3);
        output = (TextView) findViewById(R.id.result);
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
                Toast.makeText(this, input.getText().toString(), Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }
}
