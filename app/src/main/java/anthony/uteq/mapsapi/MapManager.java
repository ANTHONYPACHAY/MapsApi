package anthony.uteq.mapsapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anthony.uteq.mapsapi.utiles.Alerts;
import anthony.uteq.mapsapi.utiles.Methods;
import anthony.uteq.mapsapi.utiles.ObjectLocation;
import anthony.uteq.mapsapi.utiles.SuperItem;

public class MapManager implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener, AdapterView.OnItemSelectedListener {

    private GoogleMap myMap = null;
    private Context context;

    private RequestQueue queue;

    public MapManager(Context ctx) {
        context = ctx;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            //cuando el mapa esté completamente cargado
            myMap = googleMap;
            // habilitar controles de zoom
            myMap.getUiSettings().setZoomControlsEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
            //esta clase tiene el método para el clic del mapa, por ello se le asigna this
            myMap.setOnMapClickListener(MapManager.this);
            //vista customizada
            myMap.setOnInfoWindowClickListener(MapManager.this);

            myMap.setInfoWindowAdapter(MapManager.this);

            //genericPoinst();
            getDataVolley();
        } else {
            Alerts.MessageToast(context, "Esta nulo el Mapa");
            //cierra el modal una vez haya cargado la api
            Alerts.closeLoading();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //clic en el mapa
    }

    public GoogleMap getMyMap() {
        return myMap;
    }

    public void setMyMap(GoogleMap myMap) {
        this.myMap = myMap;
    }

    public void setSpinner(Spinner spinner) {

        ArrayList<SuperItem> sOpciones = new ArrayList<>();
        sOpciones.add(new SuperItem("1", "Mapa Normal"));
        sOpciones.add(new SuperItem("2", "Mapa Satelital"));
        sOpciones.add(new SuperItem("3", "Mapa Topográfica"));
        sOpciones.add(new SuperItem("4", "Mapa Híbrido"));


        ArrayAdapter<SuperItem> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, sOpciones);

        spinner.setAdapter(adapter);
        spinner.setPrompt(sOpciones.get(0).getDisplay());

        spinner.setOnItemSelectedListener(MapManager.this);
    }

    /**
     * Métodos del spinner
     **/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SuperItem tmp = (SuperItem) parent.getItemAtPosition(position);
        String city = "The city is " + tmp.getValue();
        Toast.makeText(context, city, Toast.LENGTH_LONG).show();

        myMap.setMapType(Integer.parseInt(tmp.getValue()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void genericPoinst() {

        LatLng[] puntos = new LatLng[5];
        puntos[0] = new LatLng(-1.831239, -78.183406);
        puntos[1] = new LatLng(-1.833239, -78.183406);
        puntos[2] = new LatLng(-1.831239, -73.183406);
        puntos[3] = new LatLng(-1.831239, -75.183406);
        puntos[4] = new LatLng(-1.831239, -76.183406);

        for (int ind = 0; ind < puntos.length; ind++) {
            Marker map = myMap.addMarker(new
                    MarkerOptions().position(puntos[ind])
                    .title("Punto " + ind)
                    .snippet("Nombre del Lugar"));

            map.setTag(null);
        }

    }

    /************************* INFO VIEW ADAPTER ***********************************/

    @Override
    public View getInfoWindow(Marker marker) {
        View infoView = LayoutInflater.from(context).inflate(R.layout.cardinfoview, null);

        TextView fname = (TextView) infoView.findViewById(R.id.facultyName);
        TextView fdecano = (TextView) infoView.findViewById(R.id.facultyDecano);
        TextView femail = (TextView) infoView.findViewById(R.id.facultyemail);
        TextView flocat = (TextView) infoView.findViewById(R.id.facultyLocation);
        Button fbtn = (Button) infoView.findViewById(R.id.facultyvisite);
        ImageView flogo = (ImageView) infoView.findViewById(R.id.facultylogo);

        //btengo el objeto localización
        ObjectLocation tmp = (ObjectLocation) marker.getTag();
        fname.setText(tmp.getName());
        fdecano.setText(tmp.getAuthority());
        femail.setText(tmp.getContact());
        flocat.setText(tmp.getDirection());

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerts.MessageToast(context, "Porky");
                //Alerts.openBrowser(tmp.getUrl(), context);
            }
        });

        //tmp.getUrl()
        Alerts.MessageToast(context, "logo: " + tmp.getLogo());
        Picasso.get().load(tmp.getLogo())
                //.placeholder(R.drawable.portada)
                .error(R.drawable.caratula)
                .into(flogo, new MarkerCallback(marker));


        /*Glide.with(context).load(tmp.getLogo())
                .placeholder(R.drawable.caratula)
                .error(R.drawable.caratula)
                .listener(new GlideMarkerCallback(marker))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(flogo);*/
        return infoView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Alerts.MessageToast(context, "ver carta");
        /*ObjectLocation tmp = (ObjectLocation) marker.getTag();
        Glide.with(context).load(tmp.getUrl()).into(flogo);*/
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Alerts.MessageToast(context, "Click en el card");
        ObjectLocation tmp = (ObjectLocation) marker.getTag();
        Alerts.openBrowser(tmp.getUrl(), context);
    }

    private void getDataVolley() {
        queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://firebasestorage.googleapis.com/v0/b/letritas-48681.appspot.com/o/edificios_uteq.json?alt=media&token=59791cd5-96bc-41bd-a6a0-c50096e8797d",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<ObjectLocation> list = processResponse(response);
                        setPoints(list);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Alerts.MessageToast(context, "Error en Volley");
                        //cierra el modal una vez haya cargado la api
                        Alerts.closeLoading();
                    }
                }
        );
        queue.add(request);
    }

    private List<ObjectLocation> processResponse(String response) {
        List<ObjectLocation> data = new ArrayList<>();
        Gson gson = new Gson();//convertidor de jsonObjecto a Object.class
        //Convertir el string de respuesta(con formato JsonArray) en un JsonArray
        try {
            byte[] u = response.getBytes("ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (Exception e) {
            response = "[]";
        }

        JsonArray jarr = Methods.stringToJsonArray(response);
        //validar cantidad de elementos, para informar en caso de no encontrar alguno
        if (jarr.size() > 0) {
            //recorrer los items
            for (int ind = 0; ind < jarr.size(); ind++) {
                //convertir el elemento del json en jsonObject(por defecto los items dentro de
                // JsonArray son de tipo JsonElement)
                JsonObject jso = Methods.JsonElementToJSO(jarr.get(ind));
                //Verificar cantidad de keys dentor del json (si hay 0 lo mas probable es que haya
                // ocurrido algún problema durante la conversión de JsonElement a JsonObject)
                if (jso.size() > 0) {
                    //Casteo de JsonObject s Java.class (en este caso SuperItem)
                    ObjectLocation item = gson.fromJson(jso.toString(), ObjectLocation.class);
                    //agrega el item a la lista
                    data.add(item);
                }
            }
        } else {
            //Toast indicando la ausencia de elementos
            Alerts.MessageToast(context, "No hay registros.");
        }
        //retorno de la lista con todos los elementos
        return data;
    }

    public void setPoints(List<ObjectLocation> points) {
        //recorrer la lista
        LatLng uteq = null;
        for (int ind = 0; ind < points.size(); ind++) {
            //obtener un item de la lista
            ObjectLocation tmp = points.get(ind);

            Marker map = myMap.addMarker(
                    new MarkerOptions().position(new LatLng(tmp.getLat(), tmp.getLng()))
                            .title(tmp.getName())
                            .snippet(tmp.getDirection())

            );
            map.setTag(tmp);

            //cierra el modal una vez haya cargado la api
            Alerts.closeLoading();
            Bitmap bm = null;
            if (tmp.isCampus()) {
                uteq = new LatLng(tmp.getLat(), tmp.getLng());
                //cambiar icono para campus :3
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.campus);
            }else{
                //cambiar icono para facultad
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.education);
            }
            //rescalar icono
            bm = Bitmap.createScaledBitmap(bm, 100, 120, false);
            //asignar icono
            map.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
        }
        if (uteq != null) {
            CameraPosition camPos = new CameraPosition.Builder()
                    .target(uteq)
                    .zoom(13)
                    .bearing(45)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            myMap.animateCamera(camUpd3);
        }
    }

    public class MarkerCallback implements Callback {
        private Marker markerToRefresh;

        public MarkerCallback(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }

        @Override
        public void onError(Exception e) {
            Alerts.MessageToast(context, "errorPicasso:" + e.getMessage());
        }
    }

    public class GlideMarkerCallback implements RequestListener<Drawable> {

        private Marker marker;

        public GlideMarkerCallback(Marker markerToRefresh) {
            this.marker = markerToRefresh;
        }

        @Override
        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Alerts.MessageToast(context, "GlideError:" + e.getMessage());
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
            return false;
        }
    }
}
