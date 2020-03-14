package com.roomies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Intent menuIntent;
    private FirebaseAuth mAuth;
    private String code;
    private String apartmentID = "apartmentID";
    private DatabaseReference mDatabase;
    private ListView chatList;
    private EditText chatInput;
    private Button sendButton;
    private ArrayList<String> listConversation;
    private ArrayAdapter arrayAdapter;
    private String user_msg_key;
    private Toolbar chatToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.Chat));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        findAll();
        menuIntent = getIntent();
        if (!apartmentID.equals(null)) {
            code = menuIntent.getExtras().getString(apartmentID);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.Apartments)).child(code).child(getResources().getString(R.string.chat));
        sendButton.setOnClickListener(view -> {
            Map<String, Object> map = new HashMap<>();
            user_msg_key = mDatabase.push().getKey();
            mDatabase.updateChildren(map);

            DatabaseReference dbr2 = mDatabase.child(user_msg_key);
            Map<String, Object> map2 = new HashMap<>();
            map2.put(getResources().getString(R.string.msg), chatInput.getText().toString());
            map2.put(getResources().getString(R.string.user), mAuth.getCurrentUser().getDisplayName());
            dbr2.updateChildren(map2);
            chatInput.setText("");
        });

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateConversation(DataSnapshot dataSnapshot) {
        String msg, user, conversation;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            msg = (String) ((DataSnapshot) i.next()).getValue();
            user = (String) ((DataSnapshot) i.next()).getValue();
            conversation = user + ": " + msg;
            arrayAdapter.insert(conversation, arrayAdapter.getCount());
            arrayAdapter.notifyDataSetChanged();
            chatList.setSelection(chatList.getCount() - 1);

        }
    }

    private void findAll() {
        chatList = findViewById(R.id.chatListView);
        chatList.setSelection(chatList.getCount() - 1);
        chatInput = findViewById(R.id.chatInput);
        sendButton = findViewById(R.id.sendButton);
        listConversation = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listConversation);
        chatList.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

