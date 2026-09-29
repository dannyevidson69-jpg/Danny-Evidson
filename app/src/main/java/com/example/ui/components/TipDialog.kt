package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TipDialog(
    creatorName: String,
    onDismiss: () -> Unit,
    onSendTip: (Double, String) -> Unit
) {
    val presets = listOf(2.0, 5.0, 10.0, 25.0, 50.0)
    var selectedAmount by remember { mutableDoubleStateOf(5.0) }
    var fanMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Column {
                Text(
                    text = "Tip @$creatorName",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
                Text(
                    text = "Send a Super Chat & support this creator directly",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Tip Amount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presets.forEach { amount ->
                        val isSelected = selectedAmount == amount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoalGreen else DarkSurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) GoalGreen else DarkSurfaceBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedAmount = amount }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$${amount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DarkBg else TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fanMessage,
                    onValueChange = { fanMessage = it },
                    placeholder = { Text("Highlight message (optional)", color = TextSecondary, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = GoalGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSendTip(selectedAmount, fanMessage)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoalGreen),
                modifier = Modifier.testTag("send_tip_confirm_button")
            ) {
                Text(
                    text = "Send $$selectedAmount Tip",
                    color = DarkBg,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
