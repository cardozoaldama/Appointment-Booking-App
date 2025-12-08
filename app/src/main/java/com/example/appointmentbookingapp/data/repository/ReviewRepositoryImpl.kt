package com.example.appointmentbookingapp.data.repository

import com.example.appointmentbookingapp.data.remorte.ReviewRemoteDataSource
import com.example.appointmentbookingapp.domain.model.Review
import com.example.appointmentbookingapp.domain.repository.ReviewRepository
import com.example.appointmentbookingapp.util.Resource
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val remote: ReviewRemoteDataSource
) : ReviewRepository {

    override suspend fun submitReview(review: Review): Resource<Unit> {
        return try {
            remote.submitReview(review)
            // Update doctor's rating after submitting review
            remote.updateDoctorRating(review.doctorId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getDoctorReviews(doctorId: String): Resource<List<Review>> {
        return try {
            val reviews = remote.getDoctorReviews(doctorId)
            Resource.Success(reviews)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getPatientReview(doctorId: String, patientId: String): Resource<Review?> {
        return try {
            val review = remote.getPatientReview(doctorId, patientId)
            Resource.Success(review)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun updateDoctorRating(doctorId: String): Resource<Unit> {
        return try {
            remote.updateDoctorRating(doctorId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}
