package com.nipuna.safeme.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nipuna.safeme.data.ChatSummary
import com.nipuna.safeme.data.FirebaseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onOpenChat: (String, String) -> Unit) {
    val uid = FirebaseRepository.currentUid ?: return
    var chats by remember { mutableStateOf<List<ChatSummary>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uid) {
        FirebaseRepository.listenChats(uid).collect { chats = it }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Safe Me") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    val id = FirebaseRepository.createChat("New chat", listOf(uid))
                    onOpenChat(id, "New chat")
                }
            }) { Text("+") }
        }
    ) { padding ->
        if (chats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No chats yet. Tap + to start one.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(chats) { chat ->
                    ListItem(
                        headlineContent = { Text(chat.title.ifBlank { "Chat" }) },
                        supportingContent = { Text(chat.lastMessage) },
                        modifier = Modifier.clickable { onOpenChat(chat.chatId, chat.title) }
                    )
                    Divider()
                }
            }
        }
    }
}
