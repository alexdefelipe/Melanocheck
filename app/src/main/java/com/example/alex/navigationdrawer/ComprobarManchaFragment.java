package com.example.alex.navigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class ComprobarManchaFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_ID_ENTRADA_SELECCIONADA = "comprobar_mancha";
    public static final int REQUEST_IMAGE_CAPTURE = 100;

    Button btn_hacerfoto;
    Button btn_repetirfoto;
    Button btn_enviarfoto;
    ImageView image_view;
    View view;

    public ComprobarManchaFragment() {
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
        view = inflater.inflate(R.layout.fragment_comprobar_mancha, container, false);

        btn_hacerfoto = view.findViewById(R.id.btn_hacerfoto);
        btn_repetirfoto = view.findViewById(R.id.btn_repetirfoto);
        btn_enviarfoto = view.findViewById(R.id.btn_enviarfoto);

        btn_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolverIntentHacerFoto();
            }
        });
        btn_repetirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolverIntentHacerFoto();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            image_view = view.findViewById(R.id.img_foto_tomada);
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mCurrentPhotoUri);
                Bitmap orientedBitmap = ExifUtil.rotateBitmap(mCurrentPhotoPath, imageBitmap);
                image_view.setImageBitmap(orientedBitmap);
                btn_hacerfoto.setVisibility(View.INVISIBLE);
                btn_repetirfoto.setVisibility(View.VISIBLE);
                btn_enviarfoto.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                Log.e("Error", "Ha susedido algo");
            }
        }
    }

    Uri mCurrentPhotoUri;
    String mCurrentPhotoPath;

    private void resolverIntentHacerFoto() {
        Intent hacerFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hacerFotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Error", "Ha susedido algo");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                        "com.example.android.fileprovider2",
                        photoFile);
                hacerFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(hacerFotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoUri = android.net.Uri.parse(image.toURI().toString());
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
