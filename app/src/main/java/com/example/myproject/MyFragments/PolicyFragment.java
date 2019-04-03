package com.example.myproject.MyFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link PolicyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PolicyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PolicyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private TextView limit,period,premium,premium_period;

    FirebaseDatabase mydb;
    DatabaseReference myref;
    ProgressBar pb;

    //private OnFragmentInteractionListener mListener;

    public PolicyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PolicyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PolicyFragment newInstance(String param1) {
        PolicyFragment fragment = new PolicyFragment();
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
        return inflater.inflate(R.layout.fragment_policy, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    //public void onButtonPressed(Uri uri) {
    //    if (mListener != null) {
    //        mListener.onFragmentInteraction(uri);
    //    }
    //}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        limit = (TextView)view.findViewById(R.id.limitTextView2);
        period = (TextView)view.findViewById(R.id.periodTextView2);
        premium = (TextView)view.findViewById(R.id.premiumTextView2);
        premium_period = (TextView)view.findViewById(R.id.premiumPeriodTextView2);
        Log.d("mapappdata","in onViewCreated after initialization");

        mydb = FirebaseDatabase.getInstance();
        myref = mydb.getReference();

        pb = (ProgressBar)view.findViewById(R.id.policyProgressBar);
        pb.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference policychild = myref.child("policy");
                policychild.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals(mParam1)) {
                                limit.setText(ds.child("limit").getValue().toString());
                                period.setText(ds.child("period").getValue().toString());
                                premium.setText(ds.child("premium").getValue().toString());
                                premium_period.setText(ds.child("premium_period").getValue().toString());
                                pb.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("mapappdata", "Failed to read value.", error.toException());
                    }
                });
            }
        }).start();
    }

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
//        void onPolicyFragmentInteraction(Uri uri);
//    }
}
