package com.project.letstalk;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.letstalk.User.UserListAdapter;
import com.project.letstalk.User.UserObject;
import com.project.letstalk.Utils.CountryToPhonePrefix;

import java.util.ArrayList;

public class ContactsFragment extends Fragment{
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList, contactList;


    private View contactFragmentView;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("WrongConstant")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userList = new ArrayList<>();
        contactList = new ArrayList<>();

        contactFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);

        initializeRecyclerView();
        getContactList();

        return contactFragmentView;
    }


    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        contactList.clear();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");


            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            UserObject mContact = new UserObject("", name, phone);
            contactList.add(mContact);
            if(!userList.contains(mContact)) getUserDetails(mContact);
        }
    }

    private void getUserDetails(final UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String phone = "", name = "";
                    UserObject mUser = null;
                    userList.clear();
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                        Log.d("contactData", "onDataChange: " + dataSnapshot);

                        Log.d("contact", "onDataChange: " + childSnapshot);

                        if(childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if(childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();

                        mUser = new UserObject(childSnapshot.getKey(), name, phone);
                        if(name.equals(phone)) {
                            for(UserObject mContactIterator : contactList) {
                                if(mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(mContactIterator.getName());
                                }
                            }
                        }


                        if(mUser != null && !userList.contains(mUser)) {
                            userList.add(mUser);

                        }

                    }
                    mUserListAdapter = new UserListAdapter(getActivity(), userList);
                    mUserList.setAdapter(mUserListAdapter);
                  //  mUserListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null )
            if(!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();


        return CountryToPhonePrefix.getPhone(iso);
    }

    @SuppressLint("WrongConstant")
    private void initializeRecyclerView() {
        mUserList = contactFragmentView.findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
       // mUserListAdapter = new UserListAdapter(getActivity(), userList);
        //mUserList.setAdapter(mUserListAdapter);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
