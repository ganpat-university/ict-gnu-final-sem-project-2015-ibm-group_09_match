package com.example.myproject.MyFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.myproject.CustomClasses.MyClaimAdapter;
import com.example.myproject.CustomClasses.MyClaimData;
import com.example.myproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * */ //{@link ClaimFragment.OnFragmentInteractionListener} interface
 /** to handle interaction events.
 * Use the {@link ClaimFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClaimFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private RecyclerView recyclerView;

    FirebaseDatabase mydb;
    DatabaseReference myref;
    ProgressBar pb;

    ArrayList<MyClaimData> claimData;

    MyClaimAdapter adapter;

//    private OnFragmentInteractionListener mListener;

    public ClaimFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ClaimFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClaimFragment newInstance(String param1) {
        ClaimFragment fragment = new ClaimFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_claim, container, false);
    }

     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
         claimData = new ArrayList<MyClaimData>();
         recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
         Log.v("mapappdata","count of reports: "+claimData.size());
         adapter = new MyClaimAdapter(claimData);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         recyclerView.setAdapter(adapter);

         mydb = FirebaseDatabase.getInstance();
         myref = mydb.getReference();

         pb = (ProgressBar)view.findViewById(R.id.claimProgressBar);
         pb.setVisibility(View.VISIBLE);

         new Thread(new Runnable() {
             @Override
             public void run() {
                 DatabaseReference reportchild = myref.child("reports/" + mParam1);
                 Log.v("mapappdata", "mparam1 in claim fragment is: " + mParam1);
                 Log.v("mapappdata", "reference in claim fragment is: " + reportchild.toString());
                 reportchild.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         for (DataSnapshot ds : dataSnapshot.getChildren()) {
                             Log.v("mapappdata", "ds.getkey() in claim fragment is: " + ds.getKey());
                             String claimId = ds.getKey();
                             String description = ds.child("description").getValue().toString();
                             MyClaimData tempObject = new MyClaimData(claimId, description);
                             Log.v("mapappdata", "claim id: " + tempObject.getClaimId());
                             Log.v("mapappdata", "claim date: " + tempObject.getClaimDate());
                             Log.v("mapappdata", "claim desc: " + tempObject.getDescription());
                             claimData.add(tempObject);
                             Log.v("mapappdata", "printing claimdata: " + claimData.get(0).getClaimId());
                         }
                         adapter.notifyDataSetChanged();
                         pb.setVisibility(View.GONE);
                     }

                     @Override
                     public void onCancelled(DatabaseError error) {
                         // Failed to read value
                         Log.w("mapappdata", "Claim Fragment Failed to read value.", error.toException());
                     }
                 });
             }
         }).start();
     }

     // TODO: Rename method, update argument and hook method into UI event
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
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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
