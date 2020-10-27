package com.example.bookwormadventuresdeluxe2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

// Todo: Rename Class to ProfileFragment or rename other fragments

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener, FirebaseUserGetSet.UserCallback
{
    private static final String TAG = "MyProfileFragment";

    Button edit;
    Button signOutButton;

    MaterialTextView appHeaderText;

    TextView viewUsername;
    TextView viewEmail;
    TextView viewPhoneNumber;

    View view;

    FirebaseAuth firebaseAuth;
    UserProfileObject viewUserObject;

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
        signOutButton = view.findViewById(R.id.profile_logout);
        signOutButton.setOnClickListener(this);

        edit = view.findViewById(R.id.profile_edit);
        edit.setOnClickListener(this);

        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.my_profile_title);

        /* Set display texts */
        viewUsername = view.findViewById(R.id.view_username);
        viewEmail = view.findViewById(R.id.view_email);
        viewPhoneNumber = view.findViewById(R.id.view_phone);

        /* Theme for popup dialog fragment */
        getContext().getTheme().applyStyle(R.style.BlackTextTheme, true);

        firebaseAuth = FirebaseAuth.getInstance();

        /* Pulling UserProfileObject from database */
        FirebaseUserGetSet.getUser(UserCredentialAPI.getInstance().getUsername(), new FirebaseUserGetSet.UserCallback()
        {
            @Override
            public void onCallback(UserProfileObject userObject)
            {
                viewUserObject = userObject;

                viewUsername.setText(viewUserObject.getUsername());
                viewEmail.setText(viewUserObject.getEmail());
                viewPhoneNumber.setText(viewUserObject.getPhoneNumber());
            }
        });

        return view;
    }

    /**
     * Handle click on Profile Edit and SignOut button
     *
     * @param view View containing layout resources
     */
    @Override
    public void onClick(View view)
    {
        try
        {
            switch (view.getId())
            {
                case R.id.profile_edit:
                    final View editInfo = LayoutInflater.from(this.getContext()).inflate(R.layout.edit_profile, null);

                    /* Set up the input */
                    EditText inputEmail = editInfo.findViewById(R.id.edit_email);
                    EditText inputPhone = editInfo.findViewById(R.id.edit_phone);

                    /* Specify the type of input expected */
                    inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

                    /* Setting text to user's details */
                    inputEmail.setText(viewUserObject.getEmail());
                    inputPhone.setText(viewUserObject.getPhoneNumber());

                    /* Create popup dialog for editing profile */
                    final AlertDialog builder = new AlertDialog.Builder(this.getContext()).create();
                    builder.setView(editInfo);

                    /* Set up the buttons */
                    editInfo.findViewById(R.id.edit_confirm).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            /* Checks if no changes were made */
                            if (viewUserObject.getEmail().equals(inputEmail.getText().toString())
                                && viewUserObject.getPhoneNumber().equals(inputPhone.getText().toString()))
                            {
                                builder.dismiss();
                                return;
                            }

                            /* Checks if empty and disables confirm button */
                            if (TextUtils.isEmpty(inputEmail.getText().toString()))
                            {
                                EditTextValidator.isEmpty(inputEmail);
                                return;
                            }

                            /* Checks if error is present and disables confirm button */
                            if (inputEmail.getError() != null)
                            {
                                return;
                            }
                            else
                            {
                                /* Attempts to write new email and phone number */
                                FirebaseUserGetSet.changeAuthInfo(inputEmail,
                                                                    inputPhone,
                                                                    viewUserObject.getDocumentId());
                                if (inputEmail.getError() != null)
                                {
                                    // Closes dialog
                                    builder.dismiss();
                                }
                            }
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
                    break;

                case R.id.profile_logout:
                    /*
                     * Listener for signOut button to sign user out of firebase account
                     * Source : https://stackoverflow.com/questions/53334017/back-button-will-bring-to-home-page-after-firebase-logout-on-app
                     * */
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

                default:
                    /* Unexpected resource id*/
                    throw new Exception("Unexpected resource Id inside click listener."
                            + "Expected: R.id.login_button Or R.id create_account_button");
            }
        }
        catch (Exception e)
        {
            /* Log message to debug*/
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Callback for UserProfileObject
     */
    @Override
    public void onCallback(UserProfileObject userObject)
    {

    }
}