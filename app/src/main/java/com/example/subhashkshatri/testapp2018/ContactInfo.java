package com.example.subhashkshatri.testapp2018;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ContactInfo extends AppCompatActivity {

    TextView tvChar,tvName;
    ImageView ivCall,ivMail,ivEdit,ivDelete;
    EditText etMail,etName,etPhone;
    Button btnSubmit;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        tvChar = findViewById(R.id.tvChar);
        tvName = findViewById(R.id.tvName);

        ivCall = findViewById(R.id.ivCall);
        ivDelete = findViewById(R.id.ivDelete);
        ivEdit = findViewById(R.id.ivEdit);
        ivMail = findViewById(R.id.ivMail);

        etMail = findViewById(R.id.etMail);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);

        btnSubmit = findViewById(R.id.btnSubmit);

        etName.setVisibility(View.GONE);
        etMail.setVisibility(View.GONE);
        etPhone.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        final int index = getIntent().getIntExtra("index",0);

        etName.setText(TestApplication.contacts.get(index).getName());
        etMail.setText(TestApplication.contacts.get(index).getEmail());
        etPhone.setText(TestApplication.contacts.get(index).getNumber());


        tvChar.setText(TestApplication.contacts.get(index).getName().toUpperCase().charAt(0)+"");
        tvName.setText(TestApplication.contacts.get(index).getName());

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + TestApplication.contacts.get(index).getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);

            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactInfo.this);
                dialog.setMessage("Are you sure you want to delete the contact ?");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgress(true);
                        tvLoad.setText("Deleting contact...please wait...");

                        Backendless.Persistence.of(Contact.class).remove(TestApplication.contacts.get(index),
                                new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {

                                        TestApplication.contacts.remove(index);
                                        Toast.makeText(ContactInfo.this, "Contact removed successfully", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        ContactInfo.this.finish();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(ContactInfo.this, "Error : " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();
            }
        });

        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,TestApplication.contacts.get(index).getEmail());
                startActivity(Intent.createChooser(intent,"Send mail to " + TestApplication.contacts.get(index).getName()));
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit = !edit;

                if(edit)
                {
                    etName.setVisibility(View.VISIBLE);
                    etMail.setVisibility(View.VISIBLE);
                    etPhone.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    etName.setVisibility(View.GONE);
                    etMail.setVisibility(View.GONE);
                    etPhone.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty() || etMail.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty())
                {
                    Toast.makeText(ContactInfo.this, "Please enter all the details.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    TestApplication.contacts.get(index).setName(etName.getText().toString().trim());
                    TestApplication.contacts.get(index).setEmail(etMail.getText().toString().trim());
                    TestApplication.contacts.get(index).setNumber(etPhone.getText().toString().trim());

                    showProgress(true);

                    tvLoad.setText("Updating contact...please wait...");

                    Backendless.Persistence.save(TestApplication.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            tvChar.setText(TestApplication.contacts.get(index).getName().charAt(0) +"");
                            tvName.setText(TestApplication.contacts.get(index).getName());
                            Toast.makeText(ContactInfo.this, "Contact successfully updated", Toast.LENGTH_SHORT).show();
                            showProgress(false);

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ContactInfo.this, "Error : "+ fault.getMessage() , Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
