package com.nipuna.safeme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nipuna.safeme.data.FirebaseRepository
import com.nipuna.safeme.data.Message
import com.nipuna.safeme.ui.theme.BubbleMe
import com.nipuna.safeme.ui.theme.BubbleOther
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String, title: String, onBack: () -> Unit) {
    val uid = FirebaseRepository.currentUid ?: return
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        FirebaseRepository.listenMessages(chatId).collect { messages = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title.ifBlank { "Chat" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("<") }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        input = ""
                        scope.launch {
                            FirebaseRepository.sendMessage(chatId, uid, text)
                        }
                    }
                }) { Text("Send") }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == uid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isMe) BubbleMe else BubbleOther)
                            .padding(10.dp)
                    ) {
                        Text(msg.text)
                    }
                }
            }
        }
    }
}
