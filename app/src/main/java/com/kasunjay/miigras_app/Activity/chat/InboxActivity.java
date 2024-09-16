package com.kasunjay.miigras_app.Activity.chat;

import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_CONVERSIONS;
import static com.kasunjay.miigras_app.util.Constants.KEY_LAST_MESSAGE;
import static com.kasunjay.miigras_app.util.Constants.KEY_RECEIVER_ID;
import static com.kasunjay.miigras_app.util.Constants.KEY_RECEIVER_NAME;
import static com.kasunjay.miigras_app.util.Constants.KEY_SENDER_ID;
import static com.kasunjay.miigras_app.util.Constants.KEY_SENDER_NAME;
import static com.kasunjay.miigras_app.util.Constants.KEY_TIMESTAMP;
import static com.kasunjay.miigras_app.util.Constants.KEY_USER;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kasunjay.miigras_app.Activity.HomeActivity;
import com.kasunjay.miigras_app.Activity.ProfileActivity;
import com.kasunjay.miigras_app.Adapter.RecentConversionAdapter;
import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ActivityInboxBinding;
import com.kasunjay.miigras_app.listeners.ConversionalListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxActivity extends AppCompatActivity implements ConversionalListener {

    private ActivityInboxBinding binding;
    private static final String TAG = "InboxActivity";
    private List<ChatMessage> conversationList;
    private RecentConversionAdapter recentConversionAdapter;
    private FirebaseFirestore firestore;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        binding.inboxBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(InboxActivity.this, HomeActivity.class));
            finish();
        });

        binding.addFriendBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(InboxActivity.this, FindFriendActivity.class));
            finish();
        });

        init();
        listenConversations();

    }

    private void init(){
        conversationList = new ArrayList<>();
        recentConversionAdapter = new RecentConversionAdapter(conversationList, this);
        binding.conversionalRecyclerView.setAdapter(recentConversionAdapter);
        firestore = FirebaseFirestore.getInstance();
    }

    private void listenConversations(){
        firestore.collection(KEY_COLLECTION_CONVERSIONS)
                .whereEqualTo(KEY_SENDER_ID, sharedPref.getLong("userId", 0))
                .addSnapshotListener(eventListener);
        firestore.collection(KEY_COLLECTION_CONVERSIONS)
                .whereEqualTo(KEY_RECEIVER_ID, sharedPref.getLong("userId", 0))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        for(DocumentChange dc : value.getDocumentChanges()){
            long senderId = dc.getDocument().getLong(KEY_SENDER_ID);
            long receiverId = dc.getDocument().getLong(KEY_RECEIVER_ID);
            switch (dc.getType()){
                case ADDED:
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(String.valueOf(senderId));
                    chatMessage.setReceiverId(String.valueOf(receiverId));

                    if(sharedPref.getLong("userId", 0) == senderId) {
                        chatMessage.setConversionId(String.valueOf(receiverId));
                        chatMessage.setConversionName(dc.getDocument().getString(KEY_RECEIVER_NAME));
                    }else {
                        chatMessage.setConversionId(String.valueOf(senderId));
                        chatMessage.setConversionName(dc.getDocument().getString(KEY_SENDER_NAME));
                    }
                    chatMessage.setMessage(dc.getDocument().getString(KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(dc.getDocument().getDate(KEY_TIMESTAMP));
                    conversationList.add(chatMessage);
                    break;
                case MODIFIED:
                    for(int i = 0; i < conversationList.size(); i++){
                        if (conversationList.get(i).getSenderId().equals(String.valueOf(senderId)) && conversationList.get(i).getReceiverId().equals(String.valueOf(receiverId))){
                            conversationList.get(i).setMessage(dc.getDocument().getString(KEY_LAST_MESSAGE));
                            conversationList.get(i).setDateObject(dc.getDocument().getDate(KEY_TIMESTAMP));
                            break;
                        }
                    }
                    break;
                case REMOVED:
                    break;
            }
        }
        Collections.sort(conversationList, (o1, o2) -> o2.getDateObject().compareTo(o1.getDateObject()));
        recentConversionAdapter.notifyDataSetChanged();
        binding.conversionalRecyclerView.smoothScrollToPosition(0);
        binding.conversionalRecyclerView.setVisibility(android.view.View.VISIBLE);
        binding.progressBar.setVisibility(android.view.View.GONE);
    };


    @Override
    public void onConversionalClick(ChatUser chatUser) {
        android.content.Intent intent = new android.content.Intent(InboxActivity.this, ChatActivity.class);
        intent.putExtra(KEY_USER, chatUser);
        startActivity(intent);
        finish();
    }
}