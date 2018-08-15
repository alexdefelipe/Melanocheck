package com.example.alex.navigationdrawer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class ProtegeteFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_ID_ENTRADA_SELECIONADA = "protege";
    private final String TAG = "ProtegeteFragment";

    private TextView txt;

    private View view;

    public ProtegeteFragment() {
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
        view = inflater.inflate(R.layout.fragment_protegete, container, false);

        NivelesUVTask task = new NivelesUVTask();
        task.execute("http://192.168.1.133/melanocheck2/");

        return view;
    }

    public ArrayList readJson(JsonReader jsonReader) throws IOException {
        String id = "";
        String titulo_consejo = "";
        String texto_consejo = "";
        jsonReader.beginObject();
        ArrayList<Consejo> listaConsejos = new ArrayList<>();
        while (jsonReader.hasNext()) {
            final String nombre = jsonReader.nextName();
            if (nombre.equals("consejos")) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        final String innerNombre = jsonReader.nextName();
                        if (innerNombre.equals("id")) {
                            id = jsonReader.nextString();
                        } else if (innerNombre.equals("titulo")) {
                            titulo_consejo = jsonReader.nextString();
                        } else if (innerNombre.equals("consejo")) {
                            texto_consejo = jsonReader.nextString();
                        }
                    }
                    jsonReader.endObject();
                    Consejo consejo = new Consejo(id, titulo_consejo, texto_consejo);
                    listaConsejos.add(consejo);
                }
            }
        }
        return listaConsejos;
    }

    public void crearCarta(Consejo consejo) {
        Context contexto = getContext();

        // Añado un LinearLayout
        LinearLayout lnlayout = view.findViewById(R.id.linear_layout);
//        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        lnlayout.setLayoutParams(linearLayoutParam);

        // Configuro el LinearLayout
        lnlayout.setPadding(40,40,40,40);
        lnlayout.setOrientation(LinearLayout.VERTICAL);
        lnlayout.setBackgroundColor(getResources().getColor(R.color.background,null));
        lnlayout.setGravity(Gravity.CENTER);

        // Añado una card
        CardView cardView = new CardView(contexto);
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(cardViewParams);

        //Setting Params for Cardview Margins
        ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        cardViewMarginParams.setMargins(0, 0, 0, 20);
        cardView.requestLayout();  //Dont forget this line
        cardView.setContentPadding(20,20,20,20);

        // Al card añado in linearlayout
        LinearLayout innerLinearLayout = new LinearLayout(contexto);
        innerLinearLayout.setOrientation(LinearLayout.VERTICAL);

        // Creo los textviews
        TextView txt_titulo = new TextView(contexto);
        txt_titulo.setText(consejo.getTitulo_consejo());
        txt_titulo.setTypeface(txt_titulo.getTypeface(),Typeface.BOLD);

        TextView txt_consejo = new TextView(contexto);
        txt_consejo.setText(consejo.getTexto_consejo());

        // ... y lo añado al innerLinearLayout
        innerLinearLayout.addView(txt_titulo);
        innerLinearLayout.addView(txt_consejo);

        // añado el innerLinearLayout al carView
        cardView.addView(innerLinearLayout);

        // y añado el card al linearlayout
        lnlayout.addView(cardView);
    }

    public class NivelesUVTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... urls) {
            Log.d(TAG, "Consultando API");
            HttpURLConnection conexion = null;
            InputStream responseBody;
            InputStreamReader responseBodyReader;
            JsonReader jsonReader = null;

            ArrayList<Consejo> listaConsejos = null;

            try {
                for (int i = 0; i < urls.length; i++) {
                    URL apiURL = new URL(urls[i]);

                    conexion = (HttpURLConnection) apiURL.openConnection();

                    responseBody = conexion.getInputStream();
                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);

                    listaConsejos = readJson(jsonReader);

                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();
                }
                return listaConsejos;
            } catch (MalformedURLException malformedURLException) {
                Log.e(TAG, malformedURLException.toString());
                return null;
            } catch (IOException io) {
                Log.e(TAG, io.toString());
                return null;
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }

        protected void onPostExecute(ArrayList resp) {
            if (resp != null) {
                String texto = "";
                for (int i = 0; i < resp.size(); i++) {
                    crearCarta((Consejo) resp.get(i));
                }
            } else {
                Log.w(TAG, "No hay respuesta");
            }
        }
    }
}

// package com.cfsuman.me.androidcode;import android.content.Context;import android.graphics.Color;import android.graphics.drawable.ColorDrawable;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.support.v7.widget.CardView;import android.util.TypedValue;import android.view.View;import android.view.Window;import android.widget.Button;import android.widget.RelativeLayout;import android.widget.LinearLayout.LayoutParams;import android.widget.TextView;

//public class MainActivity extends AppCompatActivity {
//    private Context mContext;
//    RelativeLayout mRelativeLayout;
//    private Button mButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {         // Request window feature action kafe
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);          // Get the application context
//        mContext = getApplicationContext();          // Change the action kafe color
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));          // Get the widgets reference from XML layout
//        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
//        mButton = (Button) findViewById(R.id.btn);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {                 // Initialize a new CardView
//                CardView card = new CardView(mContext);                  // Set the CardView layoutParams
//                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                card.setLayoutParams(params);                  // Set CardView corner radius
//                card.setRadius(9);                  // Set cardView content padding
//                card.setContentPadding(15, 15, 15, 15);                  // Set a background color for CardView
//                card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));                  // Set the CardView maximum elevation
//                card.setMaxCardElevation(15);                  // Set CardView elevation
//                card.setCardElevation(9);                  // Initialize a new TextView to put in CardView
//                TextView tv = new TextView(mContext);
//                tv.setLayoutParams(params);
//                tv.setText("CardView\nProgrammatically");
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
//                tv.setTextColor(Color.RED);                  // Put the TextView in CardView
//                card.addView(tv);                  // Finally, add the CardView in root layout
//                mRelativeLayout.addView(card);
//            }
//        });
//    }
//}
