package com.example.bookwormadventuresdeluxe2;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

// Todo: Rename Class to ProfileFragment or rename other fragments

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener
{
    Button edit;
    View view;

    public MyProfileFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        edit = view.findViewById(R.id.profile_edit);
        edit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        final View editInfo = LayoutInflater.from(this.getContext()).inflate(R.layout.edit_profile, null);

        // Set up the input
        final EditText inputEmail = editInfo.findViewById(R.id.edit_email);
        final EditText inputPhone = editInfo.findViewById(R.id.edit_phone);
        // Specify the type of input expected
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        final AlertDialog builder = new AlertDialog.Builder(this.getContext()).create();
        builder.setView(editInfo);

        // Set up the buttons
        editInfo.findViewById(R.id.edit_confirm).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: test input, get input, update user
                builder.dismiss();
            }
        });

        editInfo.findViewById(R.id.edit_cancel).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                builder.dismiss();
            }
        });

        builder.show();
    }
}