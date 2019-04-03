package com.example.myproject.MyFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myproject.CustomClasses.ReportData;
import com.example.myproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * *///{@link NewFragment.OnFragmentInteractionListener} interface
 /** to handle interaction events.
 * Use the {@link NewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewFragment extends Fragment{
    FirebaseStorage storage;
    StorageReference storageReference;

    String imageEncoded;
    List<String> imagesEncodedList;
    Integer count = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
     private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private byte[] imageData;
    private Button btnChoose,btnUpload,btnTake,btnSubmit;
    private EditText descEditText;
    private TextView numberDisplay;
    private ImageView imageView;
    private Uri filePath;
    private String path;
    private ProgressBar uploadProgressBar;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int CAPTURE_IMAGE_REQUEST = 1;
    private boolean imageUploaded = false;

    FirebaseDatabase mydb;
    DatabaseReference myref;

//    private OnFragmentInteractionListener mListener;

    public NewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment NewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFragment newInstance(String param1, String param2, String param3) {
        NewFragment fragment = new NewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            path = "images/"+ mParam1+"/";
        }
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new, container, false);


    }

//     TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
         count = 0;
         Log.d("mapappdata","in onViewCreated");
         btnChoose = (Button)view.findViewById(R.id.selectButton);
         btnUpload = (Button)view.findViewById(R.id.uploadButton);
         btnTake = (Button)view.findViewById(R.id.takeButton);
         btnSubmit = (Button)view.findViewById(R.id.submitButton);
         imageView = (ImageView)view.findViewById(R.id.previewImageView);
         numberDisplay = (TextView)view.findViewById(R.id.numberDisplayTextView);
         descEditText = (EditText)view.findViewById(R.id.editText3);
         Log.d("mapappdata","in onViewCreated after initialization");
         uploadProgressBar = (ProgressBar)view.findViewById(R.id.uploadProgressBar);
         uploadProgressBar.setVisibility(View.INVISIBLE);
         btnChoose.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Log.d("mapappdata","in onViewCreated setonclick of choose btn");
                 chooseImage();
             }
         });

         btnUpload.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Log.d("mapappdata","in onViewCreated setonclick of  btn upload");
                 uploadImage();
             }
         });
         btnTake.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Log.d("mapappdata","in onViewCreated setonclick of  btn take");
                 takePicture();
             }
         });
         btnSubmit.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View v) {
                 Log.d("mapappdata","in onViewCreated setonclick of  btn submit");
                 submitData();
             }
         });

     }
     private void submitData(){
        if(imageUploaded && !descEditText.getText().toString().matches("")) {
            mydb = FirebaseDatabase.getInstance();
            myref = mydb.getReference("reports/" + mParam1);
            Log.v("mapappdata", "in submitdata myref is: " + myref.toString());

            ReportData reportData = new ReportData(descEditText.getText().toString(), mParam2, mParam3, path);

            String pattern = "ddMMyyyyHHmm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            myref.child(date).setValue(reportData);
            Toast.makeText(getContext(), "Submitted!", Toast.LENGTH_SHORT).show();
            count = 0;
            numberDisplay.setText(count.toString());
            imageView.setImageDrawable(null);
            imageUploaded = false;
            descEditText.setText(" ");
        }
        else if(!imageUploaded){
            Toast.makeText(getContext(),"Please upload an image to submit report!",Toast.LENGTH_SHORT).show();
        }
        else if(descEditText.getText().toString().matches("")){
            Toast.makeText(getContext(),"Please write some description to submit report!",Toast.LENGTH_SHORT).show();
        }

     }
     private void uploadImage(){
        Log.v("mapappdata","in upload image");
        if(filePath!=null && imageView.getDrawable()!=null){
            Toast.makeText(getContext(),"Uploading",Toast.LENGTH_SHORT).show();
            Log.v("mapappdata","in upload image after null check");
            uploadProgressBar.setVisibility(View.VISIBLE);

            StorageReference ref = storageReference.child(path+UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadProgressBar.setVisibility(View.INVISIBLE);
                            imageUploaded = true;
                            Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                            count++;
                            numberDisplay.setText(count.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadProgressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(),"Failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(imageView.getDrawable()!=null){
            Toast.makeText(getContext(),"Uploading",Toast.LENGTH_SHORT).show();
            Log.v("mapappdata","in upload image else after null check");
            uploadProgressBar.setVisibility(View.VISIBLE);

            StorageReference ref = storageReference.child(path+UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putBytes(imageData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Failed ",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgressBar.setVisibility(View.INVISIBLE);
                    imageUploaded = true;
                    Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                    count++;
                    numberDisplay.setText(count.toString());
                }
            });
        }
        else{
            Toast.makeText(getContext(),"Please select an image to upload!",Toast.LENGTH_SHORT).show();
        }
     }

     private void takePicture(){
         Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
             startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
         }
     }

     private void chooseImage(){
         Intent intent = new Intent();
         intent.setType("image/*");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                 && data != null && data.getData() != null )
         {
             filePath = data.getData();
             try {
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                 imageView.setImageBitmap(bitmap);
             }
             catch (IOException e)
             {
                 e.printStackTrace();
             }
         }
         if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
             filePath = data.getData();
             Bundle extras = data.getExtras();
             Bitmap imageBitmap = (Bitmap) extras.get("data");
             imageView.setImageBitmap(imageBitmap);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             imageData = baos.toByteArray();
         }
     }


     @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
