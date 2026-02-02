package com.theralieve.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.theralieve.ui.components.TheraBackgroundDialog
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.TheraSecondaryButton2
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.ui.utils.throttledClickable
import kotlinx.coroutines.delay

@Composable
fun QuestionnaireDialog(
    onDismiss: () -> Unit,
    onSubmit: (isMember: Boolean, memberNumber: String?, employeeNumber: String?) -> Unit,
    isVerifying: Boolean = false,
    verificationError: String? = null,
    memberIdError: String? = null,
    employeeIdError: String? = null,
    isVerifyingMemberId: Boolean = false,
    isVerifyingEmployeeId: Boolean = false,
    onMemberIdChange: (String) -> Unit = {},
    onEmployeeIdChange: (String) -> Unit = {},
    onMemberIdFocus: () -> Unit = {},
    onEmployeeIdFocus: () -> Unit = {},
    locationName: String = "XYZ"
) {
    var selectedOption by remember { mutableStateOf<Boolean?>(null) }
    var memberNumber by remember { mutableStateOf("") }
    var employeeNumber by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = false
        )
    ) {
        TheraBackgroundDialog {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.width(620.dp) // kiosk width
            ) {
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {

                    // ───────────── Title ─────────────
                    Text(
                        text = "Are you a Member/Employee ?",
                        style = MaterialTheme.typography.titleLarge,
                        color = TheraColorTokens.Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ───────────── YES / NO ─────────────
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.throttledClickable {
                                selectedOption = true
                            }) {
                            RadioButton(
                                selected = selectedOption == true,
                                onClick = { selectedOption = true })
                            Text("Yes")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.throttledClickable {
                                selectedOption = false
                            }) {
                            RadioButton(
                                selected = selectedOption == false, onClick = {
                                    onSubmit(false,null,null)
                                    selectedOption = false
                                })
                            Text("No")
                        }
                    }

                    // ───────────── Input Section ─────────────
                    if (selectedOption == true) {

                        Spacer(modifier = Modifier.height(16.dp))

                        // Member ID debounce
                        LaunchedEffect(memberNumber) {
                            if (memberNumber.isNotBlank()) {
                                delay(500)
                                onMemberIdChange(memberNumber)
                            } else {
                                onMemberIdChange("")
                            }
                        }

                        OutlinedTextField(
                            value = memberNumber,
                            onValueChange = { memberNumber = it },
                            label = { Text("Please enter Member No.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .onFocusChanged {
                                    if (it.hasFocus) {
                                        employeeNumber = ""
                                        onEmployeeIdChange("")
                                        onMemberIdFocus()
                                    }
                                },
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            isError = memberIdError != null,
                            trailingIcon = if (isVerifyingMemberId) {
                                {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else null,
                            singleLine = true)

                        Text(
                            text = memberIdError ?: "", color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("OR", modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Employee ID debounce
                        LaunchedEffect(employeeNumber) {
                            if (employeeNumber.isNotBlank()) {
                                delay(500)
                                onEmployeeIdChange(employeeNumber)
                            } else {
                                onEmployeeIdChange("")
                            }
                        }

                        OutlinedTextField(
                            value = employeeNumber,
                            onValueChange = { employeeNumber = it },
                            label = { Text("Please enter Employee No.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .onFocusChanged {
                                    if (it.hasFocus) {
                                        memberNumber = ""
                                        onMemberIdChange("")
                                        onEmployeeIdFocus()
                                    }
                                },
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            isError = employeeIdError != null,
                            trailingIcon = if (isVerifyingEmployeeId) {
                                {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else null,
                            singleLine = true)

                        Text(
                            text = employeeIdError ?: "", color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (verificationError != null && memberIdError == null && employeeIdError == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = verificationError, color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ───────────── Actions ─────────────

                    if (selectedOption == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            TheraSecondaryButton2(
                                label = "Cancel",
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f)
                            )

                            TheraPrimaryButton(
                                label = if (isVerifying) "Verifying..." else "Submit",
                                enabled = !isVerifying && !isVerifyingMemberId && !isVerifyingEmployeeId && when (selectedOption) {
                                    true -> (memberNumber.isNotBlank() && memberIdError == null) || (employeeNumber.isNotBlank() && employeeIdError == null)
                                    false -> true
                                    null -> false
                                },
                                onClick = {
                                    onSubmit(
                                        selectedOption == true, memberNumber, employeeNumber
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewQuestionnaireDialog() {
    TheraJetTabTheme {


        // send outside_member always
//        membership_type
        // If Yes : club_member
        // If No : outside_member

        // only for employee otherwise dont send
        // is_for_employee 1


        QuestionnaireDialog({}, { _, _, _ ->

        })
    }
}