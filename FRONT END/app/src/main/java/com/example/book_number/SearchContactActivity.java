//package com.example.book_number;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.book_number.api.ApiService;
//import com.example.book_number.api.RetrofitClient;
//import com.example.book_number.models.Contact;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SearchContactActivity extends AppCompatActivity {
//
//    private EditText etPhone;
//    private Button btnSearch;
//    private TextView tvResult;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_contact);
//
//        etPhone = findViewById(R.id.etPhone);
//        btnSearch = findViewById(R.id.btnSearch);
//        tvResult = findViewById(R.id.tvResult);
//
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phone = etPhone.getText().toString().trim();
//                if (!phone.isEmpty()) {
//                    searchContact(phone);
//                } else {
//                    Toast.makeText(SearchContactActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void searchContact(String phone) {
//        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
//        Call<Contact> call = apiService.getContactByPhone(phone);
//
//        call.enqueue(new Callback<Contact>() {
//            @Override
//            public void onResponse(Call<Contact> call, Response<Contact> response) {
//                Log.d("SearchResponse", response.toString());
//                if (response.isSuccessful() && response.body() != null) {
//                    Contact contact = response.body();
//                    tvResult.setText("Name: " + contact.getName());
//                } else {
//                    tvResult.setText("Contact not found");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Contact> call, Throwable t) {
//                Log.e("SearchContact", "Error: " + t.getMessage());
//                tvResult.setText("Error fetching contact");
//            }
//        });
//    }
//}
