package anthony.uteq.mapsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import anthony.uteq.mapsapi.utiles.Alerts;
import anthony.uteq.mapsapi.utiles.MyLogs;

public class MainActivity extends AppCompatActivity {

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_MapsApi);

        super.onCreate(savedInstanceState);
        //oculta tool bar  en este activity
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Alerts.LoadingDialog(MainActivity.this);
        Alerts.showLoading();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mymap);

        if (mapFragment != null) {
            MapManager mapManager = new MapManager(MainActivity.this);
            mapFragment.getMapAsync(mapManager);
            Spinner opcion =(Spinner)findViewById(R.id.typesMaps);
            mapManager.setSpinner(opcion);

        } else {
            Alerts.MessageToast(MainActivity.this, "Esta nulo el Mapa");
        }

    }
}