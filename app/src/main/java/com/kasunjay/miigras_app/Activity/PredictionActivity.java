package com.kasunjay.miigras_app.Activity;

import static com.kasunjay.miigras_app.util.Constants.KEY_ACCESS_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_CHAT;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_EMERGENCY;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_USERS;
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
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kasunjay.miigras_app.Activity.chat.ChatActivity;
import com.kasunjay.miigras_app.Activity.chat.InboxActivity;
import com.kasunjay.miigras_app.Adapter.ChatAdapter;
import com.kasunjay.miigras_app.Adapter.ChatUserAdapter;
import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ActivityPredictionBinding;
import com.kasunjay.miigras_app.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PredictionActivity extends AppCompatActivity {

    private static final String TAG = "PredictionActivity";
    private static final String getPredictURL = Constants.BASE_URL + "/api/v1/mobile/predict";
    private ActivityPredictionBinding binding;
    private ChatUser receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore firestore;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPredictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

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
        firestore.collection(KEY_COLLECTION_EMERGENCY)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    sendPredictionMessage(binding.inputMessage.getText().toString());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PredictionActivity.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void listenMessages(){
        firestore.collection(KEY_COLLECTION_EMERGENCY)
                .whereEqualTo(KEY_SENDER_ID, sharedPref.getLong("userId", 0))
                .whereEqualTo(KEY_RECEIVER_ID, receiverUser.getUserId())
                .addSnapshotListener(eventListener);
        firestore.collection(KEY_COLLECTION_EMERGENCY)
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
    };

    private void setListeners() {
        binding.chatBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, HomeActivity.class));
            finish();
        });
        binding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });
    }

    private void loadReceiverUser() {
        receiverUser = (ChatUser) getIntent().getSerializableExtra(KEY_USER);
    }

    private String getReadableDate(Date date){
        return new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(date);
    }

    private void sendPredictionMessage(String message) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("employeeId", sharedPref.getLong("userId", 0));
            payload.put("message", message);
            payload.put("fcmToken", sharedPref.getString(Constants.KEY_FCM_TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, getPredictURL, payload,
                response -> {
                    try {
                        if(response.getString("success").equals("SUCCESS")){
                            binding.inputMessage.setText("");
                            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Message failed to send", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e) {
                        Toast.makeText(this, "Message failed to send", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle Volley error
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "";
                    int statusCode = 0;

                    if (networkResponse != null) {
                        String result = new String(networkResponse.data);
                        statusCode = networkResponse.statusCode;

                        Log.e(TAG, "Status Code: " + statusCode);
                        Log.e(TAG, "Response Data: " + result);

                        try {
                            JSONObject response = new JSONObject(result);
                            String errorResponse = response.optString("error", "No error details");
                            errorMessage = "Error: " + errorResponse;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMessage = "JSON parsing error in error response: " + e.getMessage();
                        }
                    } else {
                        // Handle case where network response is null
                        Log.e(TAG, "Network response is null, error: " + error.getMessage());
                        errorMessage = "No employee details found!";
                    }
                    Log.e(TAG, "onErrorResponse: " + errorMessage);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Access-Token", "Bearer " + sharedPref.getString(KEY_ACCESS_TOKEN, ""));
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}