package com.example.bookwormadventuresdeluxe2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment
{
    private Button editButton;
    private Button signoutButton;
    private View view;
    private FirebaseAuth firebaseAuth;

    public MyProfileFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /* Inflate the layout for this fragment */
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        /* Find the buttons */
        editButton = view.findViewById(R.id.profile_edit);
        signoutButton = view.findViewById(R.id.profile_logout);

        /* Set the button listeners */
        editButton.setOnClickListener(this::onEditClick);
        signoutButton.setOnClickListener(this::onSignoutClick);

        /* Get the firebaseAuth instance to use for logging out when the signout button
           is clicked. */
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    /**
     * Listener for when the edit profile button is clicked
     *
     * @param view
     */
    public void onEditClick(View view)
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

        /* Set up the buttons for the edit profile dialog */
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

    /**
     * Listener for signout button to sign user out of firebase account
     *
     * @param view
     */
    /* https://stackoverflow.com/questions/53334017/back-button-will-bring-to-home-page-after-firebase-logout-on-app */
    public void onSignoutClick(View view)
    {
        if (firebaseAuth != null)
        {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /* Take User back to Login Page */
            startActivity(intent);
        }
    }
}