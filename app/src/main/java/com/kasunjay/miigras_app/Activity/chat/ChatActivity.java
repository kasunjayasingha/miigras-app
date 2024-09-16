package com.kasunjay.miigras_app.Activity.chat;

import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_CHAT;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_CONVERSIONS;
import static com.kasunjay.miigras_app.util.Constants.KEY_LAST_MESSAGE;
import static com.kasunjay.miigras_app.util.Constants.KEY_MESSAGE;
import static com.kasunjay.miigras_app.util.Constants.KEY_RECEIVER_ID;
import static com.kasunjay.miigras_app.util.Constants.KEY_RECEIVER_NAME;
import static com.kasunjay.miigras_app.util.Constants.KEY_SENDER_ID;
import static com.kasunjay.miigras_app.util.Constants.KEY_SENDER_NAME;
import static com.kasunjay.miigras_app.util.Constants.KEY_TIMESTAMP;
import static com.kasunjay.miigras_app.util.Constants.KEY_USER;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_EMPLOYEE_DETAILS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kasunjay.miigras_app.Adapter.ChatAdapter;
import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private ChatUser receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore firestore;
    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;
    private String conversationId = null;
    JSONObject employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);
        try {
            employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        setListeners();
        loadReceiverUser();
        init();
        listenMessages();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,
                String.valueOf(sharedPref.getLong("userId", 0))
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        firestore = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(KEY_SENDER_ID, sharedPref.getLong("userId", 0));
        message.put(KEY_RECEIVER_ID, receiverUser.getUserId());
        message.put(KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(KEY_TIMESTAMP, new Date());
        firestore.collection(KEY_COLLECTION_CHAT)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    if(conversationId != null){
                        updateConversion(binding.inputMessage.getText().toString());
                    } else {
                        HashMap<String, Object> conversion = new HashMap<>();
                        conversion.put(KEY_SENDER_ID, sharedPref.getLong("userId", 0));
                        try {
                            conversion.put(KEY_SENDER_NAME, employee.getJSONObject("person").getString("firstName") + " " + employee.getJSONObject("person").getString("lastName"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        conversion.put(KEY_RECEIVER_ID, receiverUser.getUserId());
                        conversion.put(KEY_RECEIVER_NAME, receiverUser.getName());
                        conversion.put(KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
                        conversion.put(KEY_TIMESTAMP, new Date());
                        addConversion(conversion);
                    }
                    binding.inputMessage.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void listenMessages(){
        firestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, sharedPref.getLong("userId", 0))
                .whereEqualTo(KEY_RECEIVER_ID, receiverUser.getUserId())
                .addSnapshotListener(eventListener);
        firestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, receiverUser.getUserId())
                .whereEqualTo(KEY_RECEIVER_ID, sharedPref.getLong("userId", 0))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            error.printStackTrace();
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(documentChange.getDocument().getString(KEY_MESSAGE));
                    chatMessage.setSenderId(String.valueOf(documentChange.getDocument().getLong(KEY_SENDER_ID)));
                    chatMessage.setReceiverId(String.valueOf(documentChange.getDocument().getLong(KEY_RECEIVER_ID)));
                    chatMessage.setDateTime(getReadableDate(documentChange.getDocument().getDate(KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }
//            Collections.sort(chatMessages, (o1, o2) -> o1.getDateObject().compareTo(o2.getDateObject()));
            chatMessages.sort(Comparator.comparing(ChatMessage::getDateObject));
            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(android.view.View.VISIBLE);
        }
        binding.progressBar.setVisibility(android.view.View.GONE);

        if(conversationId == null){
            checkForConversion();
        }
    };

    private void loadReceiverUser() {
        receiverUser = (ChatUser) getIntent().getSerializableExtra(KEY_USER);
        binding.textName.setText(receiverUser.getName());
    }

    private void setListeners() {
        binding.chatBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, InboxActivity.class));
            finish();
        });
        binding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });
    }

    private String getReadableDate(Date date){
        return new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        firestore.collection(KEY_COLLECTION_CONVERSIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> {
                    conversationId = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void updateConversion(String message){
        DocumentReference documentReference =
                firestore.collection(KEY_COLLECTION_CONVERSIONS).document(conversationId);
        documentReference.update(KEY_LAST_MESSAGE, message,
                KEY_TIMESTAMP, new Date());

    }

    private void checkForConversion(){
        if (chatMessages.size() != 0){
            checkForConversionRemotely(
                    sharedPref.getLong("userId", 0),
                   receiverUser.getUserId()
            );
            checkForConversionRemotely(
                    receiverUser.getUserId(),
                    sharedPref.getLong("userId", 0)
            );
        }
    }

    private void checkForConversionRemotely(long senderId, long receiverId){
        firestore.collection(KEY_COLLECTION_CONVERSIONS)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };

}