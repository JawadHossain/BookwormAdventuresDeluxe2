package com.example.bookwormadventuresdeluxe2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
    View view;

    TextView viewUsername;
    TextView viewEmail;
    TextView viewPhoneNumber;

    UserObject viewUserObject;

//    Boolean emailInUse = false;
//    Boolean validEmail = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebase.collection("Users");

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
        signOutButton = view.findViewById(R.id.profile_logout);
        signOutButton.setOnClickListener(this);

        edit = view.findViewById(R.id.profile_edit);
        edit.setOnClickListener(this);

        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.my_profile_title);

        viewUsername = view.findViewById(R.id.view_username);
        viewEmail = view.findViewById(R.id.view_email);
        viewPhoneNumber = view.findViewById(R.id.view_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUserGetSet.getUser(UserCredentialAPI.getInstance().getUsername(), new FirebaseUserGetSet.UserCallback()
        {
            @Override
            public void onCallback(UserObject userObject)
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

                    // Set up the input
                    EditText inputEmail = editInfo.findViewById(R.id.edit_email);
                    EditText inputPhone = editInfo.findViewById(R.id.edit_phone);
                    // Specify the type of input expected
                    inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

                    inputEmail.setText(viewUserObject.getEmail());
                    inputPhone.setText(viewUserObject.getPhoneNumber());

                    final AlertDialog builder = new AlertDialog.Builder(this.getContext()).create();
                    builder.setView(editInfo);

                    // Set up the buttons
                    editInfo.findViewById(R.id.edit_confirm).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if (viewUserObject.getEmail().compareTo(inputEmail.getText().toString()) == 0)
                            {
                                builder.dismiss();
                            }
                            changeAuthEmail(inputEmail);
                            if (inputEmail.getError() != null)
                            {
                                FirebaseUserGetSet.editInfo(viewUserObject.getDocumentId(),
                                        inputEmail.getText().toString(),
                                        inputPhone.getText().toString());
                                builder.dismiss();
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
                            + "Expected: R.id.login_button Or R.idcreate_account_button");
            }
        } catch (Exception e)
        {
            /* Log message to debug*/
            Log.d(TAG, e.getMessage());
        }
    }

    public void changeAuthEmail(EditText inputEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(inputEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                        else
                        {
                            try
                            {
                                String errorCode = "";
                                errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                switch (errorCode)
                                {
                                    case "ERROR_INVALID_EMAIL":
                                        /* Set Email EditText error code and additionally check password eligibility */
                                        EditTextValidator.invalidEmail(inputEmail);
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        EditTextValidator.emailTaken(inputEmail);
                                        break;

                                    default:
                                        /* Unexpected Error code*/
                                        throw new Exception("Unexpected Firebase Error Code"
                                                + "inside click listener.");
                                }
                            }
                            catch (Exception e)
                            {
                                /* Log message to debug*/
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void onCallback(UserObject userObject)
    {

    }

}