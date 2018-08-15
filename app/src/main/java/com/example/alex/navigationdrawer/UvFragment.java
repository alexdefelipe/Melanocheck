package com.example.alex.navigationdrawer;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class UvFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_ID_ENTRADA_SELECCIONADA = "niveles_uv";
    private static final String API_KEY = "c9a619d1104587cb0bba58751dfa9103";
    private static final String TAG = "UvFragment";

    //    double lat = 0;
//    double lon = 0;
    boolean pielClara;

    private ImageButton btn_ObtenerUbi;
    private Button btn_PielClara;
    private Button btn_PielOscura;

    private TextView txt_value;
    private AutoCompleteTextView mAutocompleteTextView;

    private View view;
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    PlaceAutocompleteFragment autocompleteFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    public Localizacion loc;


    public UvFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_uv, container, false);

        loc = new Localizacion();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getLocalizacion(false, true);

        btn_ObtenerUbi = view.findViewById(R.id.btn_obtenerUbi);
        btn_PielClara = view.findViewById(R.id.btn_pielClara);
        btn_PielOscura = view.findViewById(R.id.btn_pielOscura);

        txt_value = view.findViewById(R.id.txt_value);
        mAutocompleteTextView = view.findViewById(R.id.autoCompleteTextView);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);

        btn_ObtenerUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalizacion(true, false);
            }
        });

        btn_PielClara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_PielClara.setBackground(getResources().getDrawable(R.drawable.button_pielclara_pressed, null));
                btn_PielOscura.setBackgroundColor(getResources().getColor(R.color.piel_oscura, null));

                pielClara = true;
                llamarApi();
            }
        });

        btn_PielOscura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_PielClara.setBackgroundColor(getResources().getColor(R.color.piel_clara, null));
                btn_PielOscura.setBackground(getResources().getDrawable(R.drawable.button_pieloscura_pressed, null));

                pielClara = false;
                llamarApi();

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        // ****** TUTORIAL ****** //

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity());

        MaterialShowcaseView.Builder card1 = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(view.findViewById(R.id.cardview_1))
                .setDismissText("Siguiente")
                .setContentText("Aquí puedes seleccionar la ubicación donde realizar las mediciones.")
                .withRectangleShape();

        MaterialShowcaseView.Builder introduce_lugar = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(mAutocompleteTextView)
                .setDismissText("Siguiente")
                .setContentText("Puedes o bien buscar un lugar...")
                .withRectangleShape();

        MaterialShowcaseView.Builder botonUbi = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(btn_ObtenerUbi)
                .setDismissText("Siguiente")
                .setContentText("... o bien seleccionar la ubicación en la que te encuentras.");

        MaterialShowcaseView.Builder card2 = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(view.findViewById(R.id.cardview_2))
                .setDismissText("Siguiente")
                .setContentText("A continuación selecciona el color de que más se aproxima al de tu piel")
                .withRectangleShape();

        MaterialShowcaseView.Builder card3 = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(view.findViewById(R.id.cardview_3))
                .setDismissText("Finalizar")
                .setContentText("Y por último... ¡observa el factor de protección recomendado!")
                .withRectangleShape();

        sequence.addSequenceItem(card1.build());
        sequence.addSequenceItem(introduce_lugar.build());
        sequence.addSequenceItem(botonUbi.build());
        sequence.addSequenceItem(card2.build());
        sequence.addSequenceItem(card3.build());

        sequence.singleUse("tutorial_uv");

        sequence.start();


        return view;
    }

    private void getLocalizacion(final boolean getAddress, final boolean primerInicio) {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d(TAG, "Lat en mFusedLocationClient = " + location.getLatitude());
                                loc.setLat(location.getLatitude());
                                loc.setLon(location.getLongitude());
                                if (getAddress == true) {
                                    String direccion = getAdress(loc.getLat(), loc.getLon());
                                    mAutocompleteTextView.setText(direccion);
                                    mAutocompleteTextView.dismissDropDown();
                                }
                                if (primerInicio == true) {
                                    final LatLngBounds bounds = boundsWithCenterAndLatLngDistance(new LatLng(loc.getLat(), loc.getLon()), 12000, 12000); // 12000 in meters
                                    Log.d(TAG, "Bounds: " + bounds.toString());
                                    mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                                            bounds, null);
                                    mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
                                }
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);
        }
    }

    public void llamarApi() {
        NivelesUVTask task = new NivelesUVTask();
        task.execute("http://api.openweathermap.org/data/2.5/uvi?appid=" + API_KEY + "&lat=" + Double.toString(loc.getLat()) + "&lon=" + Double.toString(loc.getLon()));
    }


    private String getAdress(double lat, double lon) {
        try {
            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            } else {
                Log.e(TAG, "getAdress: mo se ha obtenido ninguna dirección");
                return null;
            }
        } catch (IOException io) {
            Log.e(TAG, "getAdress: error de IO");
            return null;
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            Log.i(TAG, "Place: " + place.getLatLng().latitude);
            loc.setLat(place.getLatLng().latitude);
            loc.setLon(place.getLatLng().longitude);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage((FragmentActivity) getActivity(), this) // this - for implementing Interface
                    .build();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
        mGoogleApiClient.disconnect();
    }

    public class NivelesUVTask extends AsyncTask<String, Void, Localizacion> {

        @Override
        protected Localizacion doInBackground(String... urls) {
            Log.d(TAG, "Consultando API");

            HttpURLConnection conexion = null;
            InputStream responseBody;
            InputStreamReader responseBodyReader;
            JsonReader jsonReader;

            String result = "nada";
            try {
                for (int i = 0; i < urls.length; i++) {
                    URL apiURL = new URL(urls[i]);
                    conexion = (HttpURLConnection) apiURL.openConnection();

                    responseBody = conexion.getInputStream();
                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);


                    jsonReader.beginObject(); // Start processing the JSON object
                    result = "";
                    String date_iso;
                    String date;
                    String value = null;
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName();
                        if (key.equals("lat")) {
                            loc.setLat(Double.parseDouble(jsonReader.nextString()));
                            Log.d(key, Double.toString(loc.getLat()));
                        } else if (key.equals("lon")) {
                            loc.setLon(Double.parseDouble(jsonReader.nextString()));
                            Log.d(key, Double.toString(loc.getLon()));
                        } else if (key.equals("date_iso")) {
                            date_iso = jsonReader.nextString();
                            Log.d(key, date_iso);
                        } else if (key.equals("date")) {
                            date = jsonReader.nextString();
                            Log.d(key, date);
                        } else if (key.equals("value")) {
                            loc.setValue(jsonReader.nextString());
                            Log.d(key, Double.toString(loc.getValue()));
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();
                    Log.d(TAG, "Localización en task: " + loc.toString());
                }
            } catch (MalformedURLException malformedURLException) {
                Log.e(TAG, malformedURLException.toString());
                result = "Nada OK";

            } catch (IOException io) {
                Log.e(TAG, io.toString());
                result = "Nada OK";
            } finally {
                if (conexion != null)
                    conexion.disconnect();

            }
            return loc;
        }

        @Override
        protected void onPostExecute(Localizacion localizacion) {
            if (localizacion != null) {
                Log.d(TAG, "Latitud en tarea: " + localizacion.getLat().toString());
                Log.d(TAG, "Longitud en tarea: " + localizacion.getLon().toString());
                String spf = getSpf(localizacion.getValue());
                TextView text = getActivity().findViewById(R.id.textView2);
                txt_value.setText(spf);
            } else {
                Log.w(TAG, "Localización en tarea es nulo");
            }
        }

        private String getSpf(Double UV) {
            String spf;
            if (pielClara) {
                if (UV < 3.0) {
                    spf = "15 spf";
                } else if (UV > 3.0 && UV < 6.0) {
                    spf = "25 spf";
                } else if (UV > 6.0 && UV < 8.0) {
                    spf = "30 spf";
                } else if (UV > 8.0 && UV < 11.0) {
                    spf = "50+ spf";
                } else {
                    spf = "50+ spf";
                }
            } else {
                if (UV < 3.0) {
                    spf = "8 spf";
                } else if (UV > 3.0 && UV < 6.0) {
                    spf = "15 spf";
                } else if (UV > 6.0 && UV < 8.0) {
                    spf = "25 spf";
                } else if (UV > 8.0 && UV < 11.0) {
                    spf = "30 spf";
                } else {
                    spf = "50+ spf";
                }
            }
            return spf;
        }
    }

    // ************* Función para obtener bounds centrados en la localización del usuario ************* //
    private static final double ASSUMED_INIT_LATLNG_DIFF = 1.0;
    private static final float ACCURACY = 0.01f;

    public static LatLngBounds boundsWithCenterAndLatLngDistance(LatLng center, float latDistanceInMeters, float lngDistanceInMeters) {
        latDistanceInMeters /= 2;
        lngDistanceInMeters /= 2;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        float[] distance = new float[1];
        {
            boolean foundMax = false;
            double foundMinLngDiff = 0;
            double assumedLngDiff = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude, center.longitude + assumedLngDiff, distance);
                float distanceDiff = distance[0] - lngDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLngDiff = assumedLngDiff;
                        assumedLngDiff *= 2;
                    } else {
                        double tmp = assumedLngDiff;
                        assumedLngDiff += (assumedLngDiff - foundMinLngDiff) / 2;
                        foundMinLngDiff = tmp;
                    }
                } else {
                    assumedLngDiff -= (assumedLngDiff - foundMinLngDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - lngDistanceInMeters) > lngDistanceInMeters * ACCURACY);
            LatLng east = new LatLng(center.latitude, center.longitude + assumedLngDiff);
            builder.include(east);
            LatLng west = new LatLng(center.latitude, center.longitude - assumedLngDiff);
            builder.include(west);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffNorth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude + assumedLatDiffNorth, center.longitude, distance);
                float distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffNorth;
                        assumedLatDiffNorth *= 2;
                    } else {
                        double tmp = assumedLatDiffNorth;
                        assumedLatDiffNorth += (assumedLatDiffNorth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffNorth -= (assumedLatDiffNorth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            LatLng north = new LatLng(center.latitude + assumedLatDiffNorth, center.longitude);
            builder.include(north);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffSouth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude - assumedLatDiffSouth, center.longitude, distance);
                float distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffSouth;
                        assumedLatDiffSouth *= 2;
                    } else {
                        double tmp = assumedLatDiffSouth;
                        assumedLatDiffSouth += (assumedLatDiffSouth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffSouth -= (assumedLatDiffSouth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            LatLng south = new LatLng(center.latitude - assumedLatDiffSouth, center.longitude);
            builder.include(south);
        }
        return builder.build();
    }
}