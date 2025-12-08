package com.example.appointmentbookingapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.appointmentbookingapp.R

@Composable
fun RatingDialog(
    doctorName: String,
    existingRating: Double = 0.0,
    existingComment: String = "",
    onDismiss: () -> Unit,
    onSubmit: (rating: Double, comment: String) -> Unit
) {
    var selectedRating by remember { mutableDoubleStateOf(existingRating) }
    var comment by remember { mutableStateOf(existingComment) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (existingRating > 0) "Update Your Review" else "Rate Doctor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = doctorName,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Star Rating
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= selectedRating) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedRating = i.toDouble() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (Optional)") },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedRating > 0) {
                                onSubmit(selectedRating, comment.trim())
                            }
                        },
                        enabled = selectedRating > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.colorPrimary),
                            disabledContainerColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Submit", color = Color.White)
                    }
                }
            }
        }
    }
}
