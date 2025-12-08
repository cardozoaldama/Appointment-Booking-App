package com.example.appointmentbookingapp.domain.model

import com.google.firebase.Timestamp

/**
 * Domain model representing a patient's review for a doctor
 */
data class Review(
    val reviewId: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
