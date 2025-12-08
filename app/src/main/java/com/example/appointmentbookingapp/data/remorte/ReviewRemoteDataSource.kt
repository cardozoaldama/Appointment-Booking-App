package com.example.appointmentbookingapp.data.remorte

import android.util.Log
import com.example.appointmentbookingapp.domain.model.Review
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val logTag = "ReviewRemoteDataSource"

    /**
     * Submit a new review or update existing one
     */
    suspend fun submitReview(review: Review) {
        try {
            val reviewId = if (review.reviewId.isEmpty()) {
                // Create new review
                firestore.collection("reviews").document().id
            } else {
                // Update existing review
                review.reviewId
            }

            val reviewData = review.copy(
                reviewId = reviewId,
                timestamp = Timestamp.now()
            )

            firestore.collection("reviews")
                .document(reviewId)
                .set(reviewData)
                .await()

            Log.d(logTag, "Review submitted successfully: $reviewId")
        } catch (e: Exception) {
            Log.e(logTag, "submitReview: ${e.message}", e)
            throw e
        }
    }

    /**
     * Get all reviews for a specific doctor
     */
    suspend fun getDoctorReviews(doctorId: String): List<Review> {
        return try {
            val snapshot = firestore.collection("reviews")
                .whereEqualTo("doctorId", doctorId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            Log.e(logTag, "getDoctorReviews: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Get a patient's review for a specific doctor
     */
    suspend fun getPatientReview(doctorId: String, patientId: String): Review? {
        return try {
            val snapshot = firestore.collection("reviews")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("patientId", patientId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(Review::class.java)
        } catch (e: Exception) {
            Log.e(logTag, "getPatientReview: ${e.message}", e)
            null
        }
    }

    /**
     * Update doctor's rating and review count based on all reviews
     */
    suspend fun updateDoctorRating(doctorId: String) {
        try {
            val reviews = getDoctorReviews(doctorId)

            if (reviews.isEmpty()) {
                // No reviews, set to default values
                firestore.collection("doctors")
                    .document(doctorId)
                    .update(
                        mapOf(
                            "rating" to "0.0",
                            "reviewsCount" to 0
                        )
                    )
                    .await()
            } else {
                // Calculate average rating
                val averageRating = reviews.map { it.rating }.average()
                val formattedRating = String.format("%.1f", averageRating)

                firestore.collection("doctors")
                    .document(doctorId)
                    .update(
                        mapOf(
                            "rating" to formattedRating,
                            "reviewsCount" to reviews.size
                        )
                    )
                    .await()

                Log.d(logTag, "Doctor $doctorId rating updated: $formattedRating (${reviews.size} reviews)")
            }
        } catch (e: Exception) {
            Log.e(logTag, "updateDoctorRating: ${e.message}", e)
            throw e
        }
    }
}
