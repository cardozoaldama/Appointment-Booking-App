package com.example.appointmentbookingapp.domain.repository

import com.example.appointmentbookingapp.domain.model.Review
import com.example.appointmentbookingapp.util.Resource

/**
 * Repository interface for managing doctor reviews
 */
interface ReviewRepository {

    /**
     * Submit or update a review for a doctor
     * @param review The review to submit
     * @return Resource indicating success or failure
     */
    suspend fun submitReview(review: Review): Resource<Unit>

    /**
     * Get all reviews for a specific doctor
     * @param doctorId The doctor's ID
     * @return Resource containing list of reviews
     */
    suspend fun getDoctorReviews(doctorId: String): Resource<List<Review>>

    /**
     * Get a patient's existing review for a doctor (if any)
     * @param doctorId The doctor's ID
     * @param patientId The patient's ID
     * @return Resource containing the review or null
     */
    suspend fun getPatientReview(doctorId: String, patientId: String): Resource<Review?>

    /**
     * Update doctor's average rating and review count
     * @param doctorId The doctor's ID
     * @return Resource indicating success or failure
     */
    suspend fun updateDoctorRating(doctorId: String): Resource<Unit>
}
