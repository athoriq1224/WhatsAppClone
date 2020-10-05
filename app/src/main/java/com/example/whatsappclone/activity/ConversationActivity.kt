package com.example.whatsappclone.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.ConversationAdapter
import com.example.whatsappclone.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val conversationAdapter = ConversationAdapter(arrayListOf(), userId.toString())
    private val firebaseDb = FirebaseFirestore.getInstance()
    private var chatId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null
    private var chatName: String? = null
    private var phone: String? = null

    companion object{
        private val PARAM_CHAT_ID = "Chat_id"
        private val PARAM_IMAGE_URL = "Image_url"
        private val PARAM_CHAT_NAME = "Chat_name"
        private val PARAM_OTHER_USER_ID = "Other_user_id"

        fun newIntent(
            context: Context?,
            chatId: String?,
            imageUrl: String?,
            otherUserId: String?,
            chatName: String?

        ): Intent{
            val  intent = Intent(context,ConversationActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_IMAGE_URL, imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            intent.putExtra(PARAM_CHAT_NAME, chatName)
            return intent
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        setSupportActionBar(conversation_toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        conversation_toolbar.setNavigationOnClickListener{onBackPressed()}

        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        chatName= intent.extras?.getString(PARAM_CHAT_NAME)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)

        if (chatId.isNullOrEmpty() || userId.isNullOrEmpty()){
            Toast.makeText(this, "Chat Room Error", Toast.LENGTH_SHORT).show()
            finish()
        }
        populateImage(this,imageUrl, img_coversation, R.drawable.ic_user)
        txt_toolbar_conversation.text = chatName

        rv_message.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }
        firebaseDb.collection(DATA_CHATS)
            .document(chatId!!)
            .collection(DATA_CHAT_MESSAGE)
            .orderBy(DAtA_CHAT_MESSAGE_TIME)
            .addSnapshotListener { querySnapShot, firebaseStoreExeception ->
                if (firebaseStoreExeception !=null){
                    firebaseStoreExeception.printStackTrace()
                    return@addSnapshotListener
                }else{
                    if (querySnapShot != null){
                        for(change in querySnapShot.documentChanges){
                            when(change.type){
                                DocumentChange.Type.ADDED -> {
                                    val message = change.document.toObject(Message::class.java)

                                    if (message != null){
                                        conversationAdapter.addMassage(message)
                                        rv_message.post{
                                            rv_message.smoothScrollToPosition(conversationAdapter.itemCount - 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        imbtn_send.setOnClickListener {
            if (!edt_message.text.isNullOrEmpty()){
                val message = Message(
                    userId, edt_message.text.toString(),
                    System.currentTimeMillis()
                )

                firebaseDb.collection(DATA_CHATS)
                    .document(chatId!!)
                    .collection(DATA_CHAT_MESSAGE)
                    .document()
                    .set(message)
                edt_message.setText("", TextView.BufferType.EDITABLE)
            }
        }

        firebaseDb.collection(DATA_USERS).document(otherUserId!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                phone = user?.phone
            }
            .addOnFailureListener {
                it.printStackTrace()
                finish()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conversation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_profile ->{
                val intent = Intent(this,ProfileActivity::class.java)
                intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
                startActivity(intent)
            }
            R.id.action_call -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }
}