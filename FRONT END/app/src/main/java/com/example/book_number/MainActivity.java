package com.example.book_number;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_number.adapter.ContactAdapter;
import com.example.book_number.api.ApiService;
import com.example.book_number.api.RetrofitClient;
import com.example.book_number.models.Contact;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_CONTACTS = 100;
    private static final int REQUEST_CODE_CALL_PHONE = 101;

    RecyclerView recyclerView;
    List<Contact> contactList = new ArrayList<>();
    ContactAdapter adapter;
    private EditText etPhoneSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPhoneSearch = findViewById(R.id.etPhoneSearch);
        etPhoneSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                if (!phone.isEmpty()) {
                    searchContact(phone);
                } else {
                    loadContacts(); // recharge tous les contacts locaux ou distants
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check and request permissions for reading contacts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_READ_CONTACTS);
        } else {
            loadContacts();
        }

        // Check and request permissions for calling
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL_PHONE);
        }
    }

    private void searchContact(String phone) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Contact>> call = apiService.getContactByPhone(phone);

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contact> resultList = response.body();
                    adapter.updateData(resultList);
                } else {
                    adapter.updateData(new ArrayList<>()); // vider si aucun résultat
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadContacts() {
        contactList.clear();
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactList.add(new Contact(name, phone));
            }
            cursor.close();
        }

        // Set up adapter
        if (adapter == null) {
            adapter = new ContactAdapter(contactList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        // ✅ Only send contacts to server ONCE
        SharedPreferences prefs = getSharedPreferences("contact_prefs", MODE_PRIVATE);
        boolean contactsUploaded = prefs.getBoolean("contacts_uploaded", false);

        if (!contactsUploaded) {
            sendContactsToServer(contactList);
            prefs.edit().putBoolean("contacts_uploaded", true).apply();
        }
    }


    private void sendContactsToServer(List<Contact> contactList) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Loop through the contact list and send each contact one by one
        for (Contact contact : contactList) {
            Call<Void> call = apiService.createContact(contact.getName(), contact.getPhone());

            Log.d("MainActivity", "Sending contact: " + contact.toString());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("MainActivity", "Contact successfully uploaded: " + contact.getName());
                        Toast.makeText(MainActivity.this, "Contact uploaded: " + contact.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("MainActivity", "Failed to upload contact: " + contact.getName());
                        Toast.makeText(MainActivity.this, "Failed to upload contact: " + contact.getName(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("MainActivity", "Error: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error uploading contact: " + contact.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_CONTACTS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else if (requestCode == REQUEST_CODE_CALL_PHONE && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
