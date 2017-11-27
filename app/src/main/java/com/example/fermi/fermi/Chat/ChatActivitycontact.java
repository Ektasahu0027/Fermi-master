package com.example.fermi.fermi.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.R;
import com.example.fermi.fermi.adapter.ChatMessage;
import com.example.fermi.fermi.adapter.ChatModel;
import com.example.fermi.fermi.adapter.ContactModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by znt on 9/12/17.
 */

public class ChatActivitycontact extends AppCompatActivity {
     private FirebaseListAdapter<ChatMessage> adapter;
    DatabaseReference mDatabase;
    Timer myTimer,myTimer1;
    TimerTask doThis,doThis1;
    ListingAdapter message_adapter;
    ArrayList<ChatMessage> messageview = new ArrayList<>();
    String loginperson_name,login_email,login_udid,login_profile;
    String username,uid,profile,email;
    ImageButton sendbutton;
    EditText message;
    ListView messagelist;
    Button wave_btn,wave_btn_accept;
    RelativeLayout requrest_lay,send_lay,main_request_lay,requrest_lay2;
    TextView waiting_text,send_text,typing_text;
    String lastmessa="Send";
    CircleImageView chatprofile;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    String avaliblemeg="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        initComponents();

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        uid = intent.getStringExtra("Uid");
        profile = intent.getStringExtra("image");
        email = intent.getStringExtra("Email");

        send_text.setText("Invitation sent to "+username);
        waiting_text.setText("Wating for "+username+"...");

        message_adapter = new ListingAdapter(getApplicationContext(), messageview);
        messagelist.setAdapter(message_adapter);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(username);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  mDatabase.child("users").child(uid).child("Person").child(loginperson_name).child("typing_alert").setValue("no");
                mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
             //   myTimer.cancel();
                myTimer1.cancel();
                ChatActivitycontact.this.finish();



            }
        });

        Glide.with(getApplicationContext())
                .load(profile)
                .placeholder(R.drawable.profile)
                .into(chatprofile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loginperson_name=  FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            login_email=  FirebaseAuth.getInstance().getCurrentUser().getEmail();
            login_udid=  FirebaseAuth.getInstance().getCurrentUser().getUid();
            login_profile=  FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
            getDataFromServer();
           // displayChatMessage();
            invitaionchaeck();
        }

        mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).child("chatmegalert").setValue("No");

        initListeners();

      /*  myTimer = new Timer();
        int delay = 0;   // delay for 30 sec.
        int period = 3000;  // repeat every 60 sec.
        doThis = new TimerTask() {
            public void run() {
                if (avaliblemeg .equals("")){
                }
                else {
                    mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).setValue(new ChatModel(username,profile, uid, email,"No",avaliblemeg,"0",new Date().getTime()));
                    mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).setValue(new ChatModel(loginperson_name,login_profile,login_udid,login_email,"yes",avaliblemeg,"1",new Date().getTime()));
                    mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"0"));
                    mDatabase.child("users").child(uid).child("Chat").child(login_udid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"1"));
                    avaliblemeg="";
                }
            }
        };

        myTimer.scheduleAtFixedRate(doThis, delay, period);*/

        myTimer1 = new Timer();
        int delay1 = 0;   // delay for 30 sec.
        int period1 = 1000;  // repeat every 60 sec.
        doThis1 = new TimerTask() {
            public void run() {

                Runnable runnable = new Runnable() {
                    public void run() {

                        mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String  typing = (String) dataSnapshot.child("typing_alert").getValue();

                                // Toast.makeText(getApplication(),""+typing,Toast.LENGTH_SHORT).show();
                                //prints "Do you have data? You'll love Firebase."

                                if (typing==null) {
                                    //Log.d("ekta1", "" + typing);
                                    typing_text.setVisibility(View.GONE);
                                }
                                else if (typing.equals("yes")) {
                                    //  Log.d("ekta12", "" + typing);
                                    typing_text.setVisibility(View.VISIBLE);
                                }
                                else {
                                    //  Log.d("ekta123", "" + typing);
                                    typing_text.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

            }
        };

        myTimer1.scheduleAtFixedRate(doThis1, delay1, period1);

      /*  new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {



            }
        }, 0, 3000);*/

    }

    private void initComponents() {

        sendbutton=(ImageButton)findViewById(R.id.iv_sendMessage);
        message=(EditText)findViewById(R.id.edit_new_text);
        chatprofile = (CircleImageView)findViewById(R.id.tv_close);
        requrest_lay=(RelativeLayout)findViewById(R.id.request_layout1);
        requrest_lay2=(RelativeLayout)findViewById(R.id.request_layout2);
        send_lay=(RelativeLayout)findViewById(R.id.send_layout1);
        send_text=(TextView)findViewById(R.id.text_invitation1);
        typing_text=(TextView)findViewById(R.id.typing_text);
        waiting_text=(TextView)findViewById(R.id.waiting_text);
        main_request_lay=(RelativeLayout)findViewById(R.id.main_request_layout);
        wave_btn=(Button)findViewById(R.id.wave_btn1);
        wave_btn_accept = (Button) findViewById(R.id.wave_btn2);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_send_message);
        messagelist=(ListView)findViewById(R.id.lv_list);
        alertDialog = new AlertDialog.Builder(
                ChatActivitycontact.this).create();

    }

    private void initListeners() {

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    sendbutton.setEnabled(false);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.ic_send_24dp));

                    Runnable runnable = new Runnable() {
                        public void run() {

                       mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
                     }
               };

                  Thread mythread = new Thread(runnable);
                  mythread.start();


                  } else {
                    sendbutton.setEnabled(true);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.send_message));

                    Runnable runnable = new Runnable() {
                        public void run() {

                            mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("yes");
                        }
                    };
                    Thread mythread = new Thread(runnable);
                    mythread.start();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        wave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendTask().execute();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                avaliblemeg = message.getText().toString();
                // ChatMessage name=  new ChatMessage(avaliblemeg,"0");


                ChatMessage film = new ChatMessage();
                // Here we set the film name and star name attribute for a film in one loop
                film.setMessageText(avaliblemeg);
                film.setMsgDirection("0");

                // Pass the Film object to the array of Film objects
                messageview.add(film);
                message_adapter.notifyDataSetChanged();
                message.setText("");
                buttonClick();
            }
        });

        wave_btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendAcceptTask().execute();
            }
        });
    }
    public void buttonClick()
    {
        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).setValue(new ChatModel(username,profile, uid, email,"No",avaliblemeg,"0",new Date().getTime()));
                mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).setValue(new ChatModel(loginperson_name,login_profile,login_udid,login_email,"yes",avaliblemeg,"1",new Date().getTime()));
                mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"0"));
                mDatabase.child("users").child(uid).child("Chat").child(login_udid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"1"));
            }
        }).start();
       /* Runnable runnable = new Runnable() {
            public void run() {

                mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).setValue(new ChatModel(username,profile, uid, email,"No",avaliblemeg,"0",new Date().getTime()));
                mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).setValue(new ChatModel(loginperson_name,login_profile,login_udid,login_email,"yes",avaliblemeg,"1",new Date().getTime()));
                mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"0"));
                mDatabase.child("users").child(uid).child("Chat").child(login_udid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"1"));
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();*/
    }
    public void getDataFromServer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            messageview.clear();

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                    ChatMessage user = postSnapShot.getValue(ChatMessage.class);

                                    messageview.add(user);
                                    message_adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();

    }

    private void displayChatMessage() {

        messagelist=(ListView)findViewById(R.id.lv_list);
        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.item_chat_adapter,mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView tvmessage,tvmessage1,time;
                LinearLayout recevier,sender;
                CircleImageView senderimage;

                recevier = (LinearLayout) v.findViewById(R.id.receiver);
                sender = (LinearLayout) v.findViewById(R.id.sender);
                senderimage = (CircleImageView) v.findViewById(R.id.profile_view);

                tvmessage=(TextView)v.findViewById(R.id.messageTextView);
                tvmessage1=(TextView)v.findViewById(R.id.messageTextView1);
                time=(TextView)v.findViewById(R.id.txt_msg_time);

                //message.setText(model.getMessageText());
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvmessage.getLayoutParams();

                if(model.getMsgDirection().equals("1")){ //direction=0  should be displayed as messages sent from the mobile app.
                    lp.gravity = Gravity.LEFT;
                    recevier.setVisibility(View.GONE);
                    sender.setVisibility(View.VISIBLE);
                    sender.setGravity(Gravity.LEFT);
                    Glide.with(getApplicationContext())
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(senderimage);
                    sender.setLayoutParams(lp);
                    tvmessage.setText(model.getMessageText());
                    tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                    tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                }else if(model.getMsgDirection().equals("0")){ //direction=1  should be displayed as messages received at the mobile app
                    lp.gravity = Gravity.RIGHT;
                    sender.setVisibility(View.GONE);
                    recevier.setVisibility(View.VISIBLE);
                    recevier.setGravity(Gravity.RIGHT);
                    recevier.setLayoutParams(lp);
                    tvmessage1.setText(model.getMessageText());
                    tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                    tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
                }
            }
        };

        messagelist.setAdapter(adapter);

    }
    private void invitaionchaeck(){

        mDatabase.child("users").child(login_udid).child("Person").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lastmessa = (String) dataSnapshot.child("status").getValue();
                //   Toast.makeText(getApplicationContext(),"message "+lastmessa,Toast.LENGTH_SHORT).show();
                Log.d("last", "" + lastmessa);
                try {

                    if (lastmessa == null) {
                        requrest_lay.setVisibility(View.VISIBLE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last3", "" + lastmessa);
                    } else if (lastmessa.equals("Request")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.VISIBLE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Invite")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.VISIBLE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Friend")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last2", "" + lastmessa);
                    }

                } catch (Exception e) {
                    Log.d("last4", "" + lastmessa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class WaveSendTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
            requrest_lay.setVisibility(View.GONE);
            send_lay.setVisibility(View.VISIBLE);
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");
        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Invite");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Request");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);
            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }
    private class WaveSendAcceptTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            requrest_lay2.setVisibility(View.GONE);
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");

        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Friend");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Friend");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);

            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }

    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ChatMessage> users;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = null;
        ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<ChatMessage> users) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_chat_adapter, null, false);
                holder = new ViewHolder();

                holder.recevier = (LinearLayout) convertView.findViewById(R.id.receiver);
                holder.sender = (LinearLayout) convertView.findViewById(R.id.sender);
                holder.senderimage = (CircleImageView) convertView.findViewById(R.id.profile_view);

                holder. tvmessage=(TextView)convertView.findViewById(R.id.messageTextView);
                holder. tvmessage1=(TextView)convertView.findViewById(R.id.messageTextView1);
                holder.time=(TextView)convertView.findViewById(R.id.txt_msg_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                ChatMessage user = users.get(position);

                holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", user.getMessageTime()));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvmessage.getLayoutParams();

                if (user.getMsgDirection().equals("1")) { //direction=0  should be displayed as messages sent from the mobile app.
                    lp.gravity = Gravity.LEFT;
                    holder.recevier.setVisibility(View.GONE);
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.sender.setGravity(Gravity.LEFT);
                    Glide.with(getApplicationContext())
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(holder.senderimage);
                    holder.sender.setLayoutParams(lp);
                    holder.tvmessage.setText(user.getMessageText());
                    holder.tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                    holder.tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                    //    holder.txt_message_time.setTextColor(mContext.getResources().getColor(R.color.error_text));
                } else if (user.getMsgDirection().equals("0")) { //direction=1  should be displayed as messages received at the mobile app
                    lp.gravity = Gravity.RIGHT;
                    holder.sender.setVisibility(View.GONE);
                    holder.recevier.setVisibility(View.VISIBLE);
                    holder.recevier.setGravity(Gravity.RIGHT);
                    holder.recevier.setLayoutParams(lp);
                    holder.tvmessage1.setText(user.getMessageText());
                    holder.tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                    holder.tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
                }
            }catch (Exception e){

            }

            return convertView;
        }

        public class ViewHolder {

            TextView tvmessage,tvmessage1,time;
            LinearLayout recevier,sender;
            CircleImageView senderimage;

        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  mDatabase.child("users").child(uid).child("Person").child(login_udid).child("typing_alert").setValue("no");
        mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
        //myTimer.cancel();
        myTimer1.cancel();
        finish();
    }
}
