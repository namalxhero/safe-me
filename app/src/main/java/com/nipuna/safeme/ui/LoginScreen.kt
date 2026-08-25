package com.nipuna.safeme.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nipuna.safeme.data.FirebaseRepository
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(activity: Activity, onLoggedIn: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var codeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun sendOtp() {
        error = null
        loading = true
        val options = PhoneAuthOptions.newBuilder(FirebaseRepository.auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    loading = false
                    FirebaseRepository.auth.signInWithCredential(credential)
                        .addOnSuccessListener { onLoggedIn() }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    loading = false
                    error = e.message
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    loading = false
                    verificationId = id
                    codeSent = true
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp() {
        val id = verificationId ?: return
        error = null
        loading = true
        val credential = PhoneAuthProvider.getCredential(id, otp)
        FirebaseRepository.auth.signInWithCredential(credential)
            .addOnSuccessListener {
                loading = false
                onLoggedIn()
            }
            .addOnFailureListener {
                loading = false
                error = it.message
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Safe Me", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Private, encrypted chat",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        if (!codeSent) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number (+94...)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { sendOtp() },
                enabled = phone.length > 8 && !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Sending..." else "Send OTP")
            }
        } else {
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { verifyOtp() },
                enabled = otp.length >= 4 && !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Verifying..." else "Verify & Continue")
            }
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }
    }
}
