package com.nipuna.safeme.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Central place for talking to Firebase Auth + Firestore.
 * Kept simple for Phase 3 (plaintext messages). Phase 4 will
 * wrap text/image payloads with encryption before they reach
 * sendMessage(), so this class doesn't need to change much.
 */
object FirebaseRepository {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUid: String?
        get() = auth.currentUser?.uid

    suspend fun saveProfile(profile: UserProfile) {
        db.collection("users").document(profile.uid).set(profile).await()
    }

    suspend fun getProfile(uid: String): UserProfile? {
        val snap = db.collection("users").document(uid).get().await()
        return snap.toObject(UserProfile::class.java)
    }

    fun listenChats(uid: String): Flow<List<ChatSummary>> = callbackFlow {
        val registration = db.collection("chats")
            .whereArrayContains("members", uid)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val chats = snapshot?.documents?.map { doc ->
                    ChatSummary(
                        chatId = doc.id,
                        title = doc.getString("title") ?: "",
                        lastMessage = doc.getString("lastMessage") ?: "",
                        lastTimestamp = doc.getLong("lastTimestamp") ?: 0L
                    )
                } ?: emptyList()
                trySend(chats)
            }
        awaitClose { registration.remove() }
    }

    fun listenMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val registration = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.map { doc ->
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        text = doc.getString("text") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { registration.remove() }
    }

    suspend fun sendMessage(chatId: String, senderId: String, text: String) {
        val now = System.currentTimeMillis()
        val message = hashMapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to now
        )
        db.collection("chats").document(chatId)
            .collection("messages").add(message).await()

        db.collection("chats").document(chatId).update(
            mapOf(
                "lastMessage" to text,
                "lastTimestamp" to now
            )
        ).await()
    }

    suspend fun createChat(title: String, members: List<String>): String {
        val chat = hashMapOf(
            "title" to title,
            "members" to members,
            "lastMessage" to "",
            "lastTimestamp" to System.currentTimeMillis()
        )
        val ref = db.collection("chats").add(chat).await()
        return ref.id
    }
}
