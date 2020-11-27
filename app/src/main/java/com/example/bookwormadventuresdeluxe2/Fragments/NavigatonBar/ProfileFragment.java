package com.example.bookwormadventuresdeluxe2.Fragments.NavigatonBar;

/**
 * MyProfile view fragment class for displaying and editing your contact details
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Models.User;
import com.example.bookwormadventuresdeluxe2.Utilities.FirebaseUserGetSet;
import com.example.bookwormadventuresdeluxe2.Activities.LoginActivity;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment
{
    private static final String TAG = "MyProfileFragment";

    private Button edit;
    private Button signOutButton;

    private MaterialTextView appHeaderText;

    private TextView viewUsername;
    private TextView viewEmail;
    private TextView viewPhoneNumber;

    private View view;
    private ImageButton backButton;

    private FirebaseAuth firebaseAuth;
    private User profile;

    /**
     * Required empty public constructor
     */
    public ProfileFragment()
    {

    }

    /**
     * onCreateView initializer
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /* Getting my profile */
        Bundle bundle = getArguments();
        profile = (User) bundle.getSerializable(getString(R.string.profile_object));

        /* Inflate the layout for this fragment */
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);

        /* Buttons */
        edit = view.findViewById(R.id.profile_edit);
        signOutButton = view.findViewById(R.id.profile_logout);

        /* My profile */
        if (profile.getUsername().equals(UserCredentialAPI.getInstance().getUsername()))
        {
            myProfile();
        }
        /* Not my profile */
        else
        {
            otherProfile();
        }

        /* Set display texts */
        viewUsername = view.findViewById(R.id.view_username);
        viewEmail = view.findViewById(R.id.view_email);
        viewPhoneNumber = view.findViewById(R.id.view_phone);

        /* Setting TextView */
        viewUsername.setText(profile.getUsername());
        viewEmail.setText(profile.getEmail());
        viewPhoneNumber.setText(profile.getPhoneNumber());

        /* Theme for popup dialog fragment */
        getContext().getTheme().applyStyle(R.style.BlackTextTheme, true);

        /* Getter for FirebaseAuth instance */
        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    /**
     * Initializer for viewing and editing my profile
     */
    public void myProfile()
    {
        /* Show signout and edit button */
        appHeaderText.setText(R.string.my_profile_title);
        signOutButton.setOnClickListener(this::signOut);
        edit.setOnClickListener(this::editFragment);
    }

    /**
     * Initializer for viewing other person's profile
     */
    public void otherProfile()
    {
        /* Show back button and hide myProfile buttons */
        backButton = view.findViewById(R.id.app_header_back_button);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this::onBackClick);

        appHeaderText.setText(R.string.other_user_profile_title);
        edit.setVisibility(view.GONE);
        signOutButton.setVisibility(view.GONE);
    }

    /**
     * Dialog fragment for editing email and phone number info
     *
     * @param view The view this is called from
     */
    public void editFragment(View view)
    {
        final View editInfo = LayoutInflater.from(this.getContext()).inflate(R.layout.edit_profile, null);

        /* Set up the input */
        EditText inputEmail = editInfo.findViewById(R.id.edit_email);
        EditText inputPhone = editInfo.findViewById(R.id.edit_phone);
        ProgressBar progressBar = editInfo.findViewById(R.id.edit_progressBar);

        /* Specify the type of input expected */
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        /* Setting text to user's details */
        inputEmail.setText(profile.getEmail());
        inputPhone.setText(profile.getPhoneNumber());

        /* Create popup dialog for editing profile */
        final AlertDialog builder = new AlertDialog.Builder(this.getContext()).create();
        builder.setView(editInfo);

        /* Set up the buttons */
        editInfo.findViewById(R.id.edit_confirm).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                inputEmail.setError(null);
                inputPhone.setError(null);
                boolean hasValidationError = false;

                /* Checks if no changes were made */
                if (profile.getEmail().equals(inputEmail.getText().toString())
                        && profile.getPhoneNumber().equals(inputPhone.getText().toString()))
                {
                    builder.dismiss();
                    return;
                }

                /* Checks if email was empty and sets error */
                if (TextUtils.isEmpty(inputEmail.getText().toString().trim()))
                {
                    EditTextValidator.isEmpty(inputEmail);
                    hasValidationError = true;
                }

                /* Checks if phone number was empty and sets error*/
                if (TextUtils.isEmpty(inputPhone.getText().toString().trim()))
                {
                    EditTextValidator.isEmpty(inputPhone);
                    hasValidationError = true;
                }

                /* Checks if error is present and disables confirm button */
                if ((inputEmail.getError() != null) || hasValidationError)
                {
                    return;
                }

                /* Attempts to edit FirebaseAuth account and Firebase info*/
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUserGetSet.changeAuthInfo(inputEmail,
                        inputPhone,
                        profile.getDocumentId(),
                        new FirebaseUserGetSet.EditCallback()
                        {
                            @Override
                            public void onCallback(Boolean result)
                            {
                                /* After successful edit */
                                if (inputEmail.getError() == null)
                                {
                                    /* Updating user object in Fragment*/
                                    profile.setEmail(inputEmail.getText().toString().trim());
                                    profile.setPhoneNumber(inputPhone.getText().toString().trim());

                                    /* Updating TextView in fragment */
                                    viewEmail.setText(inputEmail.getText().toString().trim());
                                    viewPhoneNumber.setText(inputPhone.getText().toString().trim());

                                    /* Closing dialog */
                                    builder.dismiss();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });

        /* Cancel edit */
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
     * Signs out of FirebaseAuth account
     *
     * @param view The view this is called from
     */
    public void signOut(View view)
    {
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
    }

    /**
     * Takes the user back to the the previous screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        Fragment otherUserProfileFragment = getFragmentManager().findFragmentByTag(getString(R.string.other_profile_fragment));
        /* Remove this fragment and show the bookDetailFragment that was hidden beneath this */
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(otherUserProfileFragment)
                .show(getFragmentManager().findFragmentByTag(getString(R.string.book_detail_fragment)))
                .commit();
    }
}