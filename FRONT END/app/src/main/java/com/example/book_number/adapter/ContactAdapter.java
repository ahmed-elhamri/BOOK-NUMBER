package com.example.book_number.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_number.R;
import com.example.book_number.models.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;

        public ContactViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            phone = itemView.findViewById(R.id.contactPhone);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Contact contact = contactList.get(position);
                    showOptionsDialog(contact);
                }
            });
        }

        private void showOptionsDialog(Contact contact) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Choose Action")
                    .setItems(new CharSequence[]{"Call", "Send SMS"}, (dialog, which) -> {
                        switch (which) {
                            case 0: // Call
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                                if (ActivityCompat.checkSelfPermission(itemView.getContext(), Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(itemView.getContext(), "Call permission not granted", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                itemView.getContext().startActivity(callIntent);
                                break;

                            case 1: // SMS
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                smsIntent.setData(Uri.parse("smsto:" + contact.getPhone()));
                                smsIntent.putExtra("sms_body", "Hello " + contact.getName());
                                itemView.getContext().startActivity(smsIntent);
                                break;
                        }
                    })
                    .show();
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        // Ensure position is correctly handled
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());
    }



    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateData(List<Contact> newContacts) {
        this.contactList = newContacts;
        notifyDataSetChanged();
    }
}

