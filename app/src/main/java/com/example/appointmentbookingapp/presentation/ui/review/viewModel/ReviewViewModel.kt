package com.example.appointmentbookingapp.presentation.ui.review.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appointmentbookingapp.domain.model.Review
import com.example.appointmentbookingapp.domain.repository.ReviewRepository
import com.example.appointmentbookingapp.presentation.state.UiState
import com.example.appointmentbookingapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<Unit>>(UiState.Initial)
    val submitState: StateFlow<UiState<Unit>> = _submitState

    private val _existingReview = MutableStateFlow<Review?>(null)
    val existingReview: StateFlow<Review?> = _existingReview

    private val _doctorReviews = MutableStateFlow<UiState<List<Review>>>(UiState.Initial)
    val doctorReviews: StateFlow<UiState<List<Review>>> = _doctorReviews

    /**
     * Submit or update a review
     */
    fun submitReview(
        doctorId: String,
        doctorName: String,
        rating: Double,
        comment: String
    ) {
        viewModelScope.launch {
            _submitState.value = UiState.Loading

            val currentUserId = firebaseAuth.currentUser?.uid
            val currentUserName = firebaseAuth.currentUser?.displayName ?: "Anonymous"

            if (currentUserId == null) {
                _submitState.value = UiState.Error("User not authenticated")
                return@launch
            }

            // Check if user has existing review
            val existingReviewId = _existingReview.value?.reviewId ?: ""

            val review = Review(
                reviewId = existingReviewId,
                doctorId = doctorId,
                patientId = currentUserId,
                patientName = currentUserName,
                rating = rating,
                comment = comment
            )

            when (val result = reviewRepository.submitReview(review)) {
                is Resource.Success -> {
                    _submitState.value = UiState.Success(Unit)
                    Log.d("ReviewViewModel", "Review submitted successfully")
                    // Reload existing review
                    loadExistingReview(doctorId)
                }
                is Resource.Error -> {
                    _submitState.value = UiState.Error(result.message)
                    Log.e("ReviewViewModel", "Review submission failed: ${result.message}")
                }
                is Resource.Loading -> {
                    _submitState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * Load patient's existing review for a doctor
     */
    fun loadExistingReview(doctorId: String) {
        viewModelScope.launch {
            val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch

            when (val result = reviewRepository.getPatientReview(doctorId, currentUserId)) {
                is Resource.Success -> {
                    _existingReview.value = result.data
                    Log.d("ReviewViewModel", "Existing review loaded: ${result.data?.rating}")
                }
                is Resource.Error -> {
                    _existingReview.value = null
                    Log.e("ReviewViewModel", "Failed to load existing review: ${result.message}")
                }
                is Resource.Loading -> {
                    // No action needed
                }
            }
        }
    }

    /**
     * Load all reviews for a doctor
     */
    fun loadDoctorReviews(doctorId: String) {
        viewModelScope.launch {
            _doctorReviews.value = UiState.Loading

            when (val result = reviewRepository.getDoctorReviews(doctorId)) {
                is Resource.Success -> {
                    _doctorReviews.value = UiState.Success(result.data)
                    Log.d("ReviewViewModel", "Doctor reviews loaded: ${result.data.size} reviews")
                }
                is Resource.Error -> {
                    _doctorReviews.value = UiState.Error(result.message)
                    Log.e("ReviewViewModel", "Failed to load doctor reviews: ${result.message}")
                }
                is Resource.Loading -> {
                    _doctorReviews.value = UiState.Loading
                }
            }
        }
    }

    /**
     * Reset submit state
     */
    fun resetSubmitState() {
        _submitState.value = UiState.Initial
    }
}
