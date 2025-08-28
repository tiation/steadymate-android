package com.steadymate.app.ui.screens.crisis

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CrisisScreen(
    modifier: Modifier = Modifier,
    viewModel: CrisisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emergency Header
        item {
            EmergencyHeader()
        }
        
        // Immediate Crisis Resources
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Immediate Crisis Resources",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    uiState.crisisResources.forEach { resource ->
                        EmergencyContactCard(
                            resource = resource,
                            onCall = { phoneNumber ->
                                makePhoneCall(context, phoneNumber)
                            },
                            onText = { textNumber ->
                                sendSMS(context, textNumber, "HOME")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
        
        // Safety Plan Section
        item {
            SafetyPlanSection(
                safetyPlan = uiState.safetyPlan,
                onUpdatePlan = viewModel::updateSafetyPlan
            )
        }
        
        // Trusted Contacts
        item {
            TrustedContactsSection(
                contacts = uiState.trustedContacts,
                onAddContact = viewModel::addTrustedContact,
                onRemoveContact = viewModel::removeTrustedContact,
                onCallContact = { contact ->
                    makePhoneCall(context, contact.phoneNumber)
                }
            )
        }
        
        // Self-Care Resources
        item {
            SelfCareResourcesSection()
        }
    }
}

@Composable
private fun EmergencyHeader() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Crisis Support",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Crisis Support & Safety Plan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "You're not alone. Help is available 24/7.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmergencyContactCard(
    resource: CrisisResource,
    onCall: (String) -> Unit,
    onText: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                resource.phoneNumber?.let { phoneNumber ->
                    Button(
                        onClick = { onCall(phoneNumber) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call $phoneNumber")
                    }
                }
                
                resource.textNumber?.let { textNumber ->
                    OutlinedButton(
                        onClick = { onText(textNumber) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Text",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Text $textNumber")
                    }
                }
            }
        }
    }
}

@Composable
private fun SafetyPlanSection(
    safetyPlan: SafetyPlan,
    onUpdatePlan: (SafetyPlan) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star, // TODO: Fix Security icon
                        contentDescription = "Safety Plan",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "My Safety Plan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                SafetyPlanEditor(
                    safetyPlan = safetyPlan,
                    onUpdatePlan = onUpdatePlan
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to view or edit your personal safety plan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SafetyPlanEditor(
    safetyPlan: SafetyPlan,
    onUpdatePlan: (SafetyPlan) -> Unit
) {
    var warningSignsText by remember { mutableStateOf(safetyPlan.warningSigns.joinToString("\n")) }
    var copingStrategiesText by remember { mutableStateOf(safetyPlan.copingStrategies.joinToString("\n")) }
    var safeEnvironmentText by remember { mutableStateOf(safetyPlan.safeEnvironmentSteps.joinToString("\n")) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Signs
        Column {
            Text(
                text = "Warning Signs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Signs that might indicate I'm in crisis",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = warningSignsText,
                onValueChange = { 
                    warningSignsText = it
                    onUpdatePlan(
                        safetyPlan.copy(
                            warningSigns = it.split("\n").filter { line -> line.isNotBlank() }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Feeling overwhelmed\nIsolating from friends\nNegative thoughts") },
                minLines = 3
            )
        }
        
        // Coping Strategies
        Column {
            Text(
                text = "Coping Strategies",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Things I can do to help myself feel better",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = copingStrategiesText,
                onValueChange = { 
                    copingStrategiesText = it
                    onUpdatePlan(
                        safetyPlan.copy(
                            copingStrategies = it.split("\n").filter { line -> line.isNotBlank() }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Deep breathing\nListening to music\nCalling a friend") },
                minLines = 3
            )
        }
        
        // Safe Environment
        Column {
            Text(
                text = "Making Environment Safe",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Steps to make my environment safer",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = safeEnvironmentText,
                onValueChange = { 
                    safeEnvironmentText = it
                    onUpdatePlan(
                        safetyPlan.copy(
                            safeEnvironmentSteps = it.split("\n").filter { line -> line.isNotBlank() }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Remove harmful items\nGo to a public place\nStay with someone") },
                minLines = 3
            )
        }
    }
}

@Composable
private fun TrustedContactsSection(
    contacts: List<TrustedContact>,
    onAddContact: (TrustedContact) -> Unit,
    onRemoveContact: (TrustedContact) -> Unit,
    onCallContact: (TrustedContact) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    Card {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Trusted Contacts",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Trusted Contacts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Contact"
                    )
                }
            }
            
            if (contacts.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Add trusted people you can reach out to in times of need",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                
                contacts.forEach { contact ->
                    TrustedContactCard(
                        contact = contact,
                        onCall = { onCallContact(contact) },
                        onRemove = { onRemoveContact(contact) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddContactDialog(
            onAddContact = { contact ->
                onAddContact(contact)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TrustedContactCard(
    contact: TrustedContact,
    onCall: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (contact.relationship.isNotBlank()) {
                    Text(
                        text = contact.relationship,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Row {
                IconButton(
                    onClick = onCall
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call ${contact.name}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onRemove
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove ${contact.name}",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onAddContact: (TrustedContact) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Trusted Contact") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (optional)") },
                    placeholder = { Text("e.g. Friend, Family, Therapist") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && phoneNumber.isNotBlank()) {
                        onAddContact(
                            TrustedContact(
                                name = name,
                                phoneNumber = phoneNumber,
                                relationship = relationship
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && phoneNumber.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SelfCareResourcesSection() {
    Card {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Self Care",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Self-Care Resources",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val resources = listOf(
                "Take deep breaths and ground yourself",
                "Reach out to someone you trust",
                "Practice mindfulness or meditation",
                "Engage in physical activity",
                "Listen to calming music",
                "Write in a journal",
                "Practice gratitude",
                "Get professional help when needed"
            )
            
            resources.forEach { resource ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                    )
                    Text(
                        text = resource,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Utility functions for phone and SMS intents
private fun makePhoneCall(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to dial intent if CALL permission is not granted
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    }
}

private fun sendSMS(context: Context, phoneNumber: String, message: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error
    }
}
