package com.nipuna.safeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nipuna.safeme.data.FirebaseRepository
import com.nipuna.safeme.ui.ChatListScreen
import com.nipuna.safeme.ui.ChatScreen
import com.nipuna.safeme.ui.LoginScreen
import com.nipuna.safeme.ui.theme.SafeMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeMeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SafeMeApp()
                }
            }
        }
    }

    @Composable
    fun SafeMeApp() {
        val navController = rememberNavController()
        val startDestination = if (FirebaseRepository.currentUid != null) "chats" else "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(activity = this@MainActivity, onLoggedIn = {
                    navController.navigate("chats") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            composable("chats") {
                ChatListScreen(onOpenChat = { chatId, title ->
                    navController.navigate("chat/$chatId/$title")
                })
            }
            composable("chat/{chatId}/{title}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                ChatScreen(chatId = chatId, title = title, onBack = { navController.popBackStack() })
            }
        }
    }
}
