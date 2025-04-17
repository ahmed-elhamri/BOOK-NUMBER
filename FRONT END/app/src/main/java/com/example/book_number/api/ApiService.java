package com.example.book_number.api;

import com.example.book_number.models.Contact;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("createContact.php")
    Call<Void> createContact(
            @Field("name") String name,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("contacts/createBulk")
    Call<Void> createContacts(
            @Field("contacts") List<Contact> contacts
    );

    // Add this method for fetching a contact by phone number
    @FormUrlEncoded
    @POST("getContact.php")
    Call<List<Contact>> getContactByPhone(@Field("phone") String phone);

}

