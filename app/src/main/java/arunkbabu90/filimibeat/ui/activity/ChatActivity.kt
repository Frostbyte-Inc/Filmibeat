package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.data.model.Message
import arunkbabu90.filimibeat.databinding.ActivityChatBinding
import arunkbabu90.filimibeat.runPullDownAnimation
import arunkbabu90.filimibeat.ui.adapter.ChatAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity(), View.OnClickListener, ChildEventListener {
    private lateinit var binding: ActivityChatBinding

    private lateinit var roomRoot: DatabaseReference
    private lateinit var roomQuery: Query
    private var adapter: ChatAdapter? = null

    private val messages = ArrayList<Message>()
    private var senderName = ""
    private var senderId = ""
    private var movieId = ""

    private var isFirstLaunch = true

    companion object {
        const val MOVIE_ID_EXTRA_KEY = "key_chat_receiver_id_extra"
        const val SENDER_NAME_EXTRA_KEY = "key_chat_sender_name_extra"
        const val SENDER_ID_EXTRA_KEY = "key_chat_sender_id_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getStringExtra(MOVIE_ID_EXTRA_KEY) ?: ""
        senderId = intent.getStringExtra(SENDER_ID_EXTRA_KEY) ?: ""
        senderName = intent.getStringExtra(SENDER_NAME_EXTRA_KEY) ?: ""

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        roomRoot = Firebase.database.reference
            .child(Constants.ROOT_MOVIE_ROOMS)
            .child(movieId)
        loadMessages()

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lm.stackFromEnd = true
        adapter = ChatAdapter(messages, userId = senderId)
        binding.rvMessages.layoutManager = lm
        binding.rvMessages.adapter = adapter

        binding.rvMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, prevBottom ->
            // Scroll to the end of list when keyboard pop
            if (bottom < prevBottom)
                binding.rvMessages.smoothScrollToPosition(messages.size)
        }

        binding.toolbarBackBtn.setOnClickListener(this)
        binding.toolbarName.setOnClickListener(this)
        binding.fabSendMessage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.toolbarBackBtn.id -> {
                finish()
            }
            binding.fabSendMessage.id -> {
                val message: String = binding.etTypeMessage.text.toString()
                val newMsgRoot = roomRoot.push()

                if (message.isNotBlank()) {
                    val msgMap = hashMapOf(
                        Constants.FIELD_MESSAGE to message,
                        Constants.FIELD_SENDER_ID to senderId,
                        Constants.FIELD_RECEIVER_ID to movieId,
                        Constants.FIELD_MSG_TIMESTAMP to ServerValue.TIMESTAMP
                    )
                    newMsgRoot.updateChildren(msgMap)

                    binding.etTypeMessage.setText("")
                }
            }
        }
    }

    /**
     * Loads the messages
     */
    private fun loadMessages() {
        roomQuery = roomRoot.orderByChild(Constants.FIELD_MSG_TIMESTAMP)
        roomQuery.addChildEventListener(this)
    }

    /**
     * Loads the message from database to recycler view
     */
    private fun updateDataItems(snapshot: DataSnapshot) {
        val message = snapshot.getValue(Message::class.java) ?: Message()
        message.key = snapshot.key ?: ""
        messages.add(message)

        if (isFirstLaunch) {
            runPullDownAnimation(this, binding.rvMessages)
            isFirstLaunch = false
        }

        binding.rvMessages.smoothScrollToPosition(messages.size)
        adapter?.notifyDataSetChanged()
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        updateDataItems(snapshot)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
    override fun onChildRemoved(snapshot: DataSnapshot) { }
    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {  }
    override fun onCancelled(error: DatabaseError) { }
}