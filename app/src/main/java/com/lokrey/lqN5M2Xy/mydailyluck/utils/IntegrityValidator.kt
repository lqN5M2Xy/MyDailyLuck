package com.lokrey.lqN5M2Xy.mydailyluck.utils

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import java.util.UUID

class IntegrityValidator(private val context: Context) {

    fun checkIntegrity(

        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit

    ){
        val integrityManager = IntegrityManagerFactory.create(context)
        val nonce = UUID.randomUUID().toString()  // Generiere einen eindeutigen Nonce
        val integrityTokenResponse = integrityManager.requestIntegrityToken(
            IntegrityTokenRequest.builder()
                .setNonce(nonce)  // Füge den Nonce hinzu
                .setCloudProjectNumber(111895167276)  // Google Cloud Projektnummer einfügen
                .build()
        )

        integrityTokenResponse.addOnSuccessListener { response ->
            val integrityToken = response.token()
            Log.d("IntegrityCheck", "Integrity token received: $integrityToken")
            // Hier kannst du das Token an deinen Server senden oder lokal verarbeiten
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("IntegrityCheck", "Integrity check error: ${exception.message}")
            onFailure(exception)
        }
    }
    fun destroy() {
        // Keine Aufräumarbeiten notwendig
    }
}