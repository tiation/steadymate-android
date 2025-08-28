package com.steadymate.app.ui.screens.cbt

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * CBT Thought Reframing Screen - Guide users through challenging negative thoughts
 * Uses evidence-based CBT techniques to help reframe distorted thinking patterns
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CBTReframeScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CBTReframeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Thought Reframing") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (uiState.canReset) {
                    IconButton(onClick = viewModel::resetSession) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset"
                        )
                    }
                }
            }
        )
        
        // Content
        when (uiState.currentStep) {
            CBTReframeStep.INTRODUCTION -> {
                IntroductionStep(onContinue = viewModel::nextStep)
            }
            CBTReframeStep.THOUGHT_IDENTIFICATION -> {
                ThoughtIdentificationStep(
                    thoughtText = uiState.automaticThought,
                    onThoughtChange = viewModel::updateAutomaticThought,
                    onContinue = viewModel::nextStep,
                    isValid = uiState.automaticThought.isNotBlank()
                )
            }
            CBTReframeStep.EMOTION_RATING -> {
                EmotionRatingStep(
                    emotions = uiState.emotions,
                    onEmotionUpdate = viewModel::updateEmotion,
                    onContinue = viewModel::nextStep,
                    isValid = uiState.emotions.isNotEmpty()
                )
            }
            CBTReframeStep.EVIDENCE_FOR -> {
                EvidenceStep(
                    title = "Evidence FOR the thought",
                    subtitle = "What evidence supports this negative thought?",
                    evidenceList = uiState.evidenceFor,
                    onAddEvidence = viewModel::addEvidenceFor,
                    onRemoveEvidence = viewModel::removeEvidenceFor,
                    onContinue = viewModel::nextStep
                )
            }
            CBTReframeStep.EVIDENCE_AGAINST -> {
                EvidenceStep(
                    title = "Evidence AGAINST the thought",
                    subtitle = "What evidence contradicts this negative thought?",
                    evidenceList = uiState.evidenceAgainst,
                    onAddEvidence = viewModel::addEvidenceAgainst,
                    onRemoveEvidence = viewModel::removeEvidenceAgainst,
                    onContinue = viewModel::nextStep
                )
            }
            CBTReframeStep.BALANCED_THINKING -> {
                BalancedThinkingStep(
                    balancedThought = uiState.balancedThought,
                    onBalancedThoughtChange = viewModel::updateBalancedThought,
                    onContinue = viewModel::nextStep,
                    isValid = uiState.balancedThought.isNotBlank()
                )
            }
            CBTReframeStep.FINAL_RATING -> {
                FinalRatingStep(
                    emotions = uiState.emotions,
                    onEmotionUpdate = viewModel::updateEmotion,
                    onComplete = {
                        viewModel.saveSession()
                        onNavigateBack()
                    }
                )
            }
        }
        
        // Progress indicator
        LinearProgressIndicator(
            progress = uiState.progress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun IntroductionStep(
    onContinue: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Thought Reframing",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Challenge negative thinking patterns with evidence-based CBT techniques",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        item {
            Text(
                text = "How it works:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            StepCard(
                stepNumber = 1,
                title = "Identify the thought",
                description = "Write down the negative or distressing thought you're experiencing"
            )
        }
        
        item {
            StepCard(
                stepNumber = 2,
                title = "Rate your emotions",
                description = "Identify what emotions this thought triggers and rate their intensity"
            )
        }
        
        item {
            StepCard(
                stepNumber = 3,
                title = "Examine evidence",
                description = "Look for evidence both for and against this thought being true"
            )
        }
        
        item {
            StepCard(
                stepNumber = 4,
                title = "Create balanced thought",
                description = "Develop a more balanced, realistic perspective based on the evidence"
            )
        }
        
        item {
            StepCard(
                stepNumber = 5,
                title = "Re-rate emotions",
                description = "See how your emotional intensity changes with the new perspective"
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start Reframing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ThoughtIdentificationStep(
    thoughtText: String,
    onThoughtChange: (String) -> Unit,
    onContinue: () -> Unit,
    isValid: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "What's the negative thought?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Write down the exact thought that's bothering you. Be as specific as possible.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Examples:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val examples = listOf(
                        "I'm going to fail this presentation",
                        "Nobody likes me",
                        "I always mess things up",
                        "I'm not good enough for this job"
                    )
                    
                    examples.forEach { example ->
                        Text(
                            text = "• $example",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        item {
            OutlinedTextField(
                value = thoughtText,
                onValueChange = onThoughtChange,
                label = { Text("Your negative thought") },
                placeholder = { Text("I'm going to fail at...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onContinue,
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Emotion data class
data class CBTEmotion(
    val name: String,
    val icon: ImageVector,
    val beforeIntensity: Int = 0,
    val afterIntensity: Int = 0
)

@Composable 
private fun EmotionRatingStep(
    emotions: List<CBTEmotion>,
    onEmotionUpdate: (String, Int, Boolean) -> Unit,
    onContinue: () -> Unit,
    isValid: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Rate the intensity of emotions this thought creates (1-10)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        items(emotions.size) { index ->
            val emotion = emotions[index]
            EmotionSlider(
                emotion = emotion,
                onIntensityChange = { intensity ->
                    onEmotionUpdate(emotion.name, intensity, true)
                }
            )
        }
        
        item {
            Button(
                onClick = onContinue,
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmotionSlider(
    emotion: CBTEmotion,
    onIntensityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = emotion.icon,
                    contentDescription = emotion.name,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = emotion.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = emotion.beforeIntensity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Slider(
                value = emotion.beforeIntensity.toFloat(),
                onValueChange = { onIntensityChange(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Not at all",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Extremely intense",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EvidenceStep(
    title: String,
    subtitle: String,
    evidenceList: List<String>,
    onAddEvidence: (String) -> Unit,
    onRemoveEvidence: (Int) -> Unit,
    onContinue: () -> Unit
) {
    var newEvidenceText by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = newEvidenceText,
                        onValueChange = { newEvidenceText = it },
                        label = { Text("Add evidence") },
                        placeholder = { Text("Enter supporting evidence...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        trailingIcon = {
                            if (newEvidenceText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        onAddEvidence(newEvidenceText.trim())
                                        newEvidenceText = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add evidence"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
        
        if (evidenceList.isNotEmpty()) {
            item {
                Text(
                    text = "Evidence collected:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(evidenceList.size) { index ->
                EvidenceCard(
                    evidence = evidenceList[index],
                    index = index + 1,
                    onRemove = { onRemoveEvidence(index) }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EvidenceCard(
    evidence: String,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = evidence,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove evidence",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun BalancedThinkingStep(
    balancedThought: String,
    onBalancedThoughtChange: (String) -> Unit,
    onContinue: () -> Unit,
    isValid: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Create a Balanced Thought",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Based on the evidence you've collected, write a more balanced, realistic perspective.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Examples of balanced thoughts:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val examples = listOf(
                        "While I'm nervous, I've prepared well for this presentation",
                        "Some people may not connect with me, but I have meaningful relationships",
                        "I sometimes make mistakes, but I also succeed and learn from errors",
                        "I'm still learning and growing in this role"
                    )
                    
                    examples.forEach { example ->
                        Text(
                            text = "• $example",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        item {
            OutlinedTextField(
                value = balancedThought,
                onValueChange = onBalancedThoughtChange,
                label = { Text("Your balanced thought") },
                placeholder = { Text("While this situation is challenging...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )
        }
        
        item {
            Button(
                onClick = onContinue,
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FinalRatingStep(
    emotions: List<CBTEmotion>,
    onEmotionUpdate: (String, Int, Boolean) -> Unit,
    onComplete: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "How do you feel now?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "After reframing your thought, rate how intense these emotions feel now.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        items(emotions.size) { index ->
            val emotion = emotions[index]
            FinalEmotionSlider(
                emotion = emotion,
                onIntensityChange = { intensity ->
                    onEmotionUpdate(emotion.name, intensity, false)
                }
            )
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Great work!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "You've successfully worked through challenging this negative thought. Remember to practice this technique regularly.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        item {
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Complete Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FinalEmotionSlider(
    emotion: CBTEmotion,
    onIntensityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = emotion.icon,
                    contentDescription = emotion.name,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = emotion.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Before: ${emotion.beforeIntensity} → Now: ${emotion.afterIntensity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = emotion.afterIntensity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Slider(
                value = emotion.afterIntensity.toFloat(),
                onValueChange = { onIntensityChange(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Enums and data classes
enum class CBTReframeStep {
    INTRODUCTION,
    THOUGHT_IDENTIFICATION,
    EMOTION_RATING,
    EVIDENCE_FOR,
    EVIDENCE_AGAINST,
    BALANCED_THINKING,
    FINAL_RATING
}

@HiltViewModel
class CBTReframeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = mutableStateOf(
        CBTReframeUiState(
            currentStep = CBTReframeStep.INTRODUCTION,
            automaticThought = "",
            balancedThought = "",
            evidenceFor = emptyList(),
            evidenceAgainst = emptyList(),
            emotions = getDefaultEmotions(),
            progress = 1f/7f,
            canReset = false
        )
    )
    val uiState: State<CBTReframeUiState> = _uiState
    
    fun nextStep() {
        val current = _uiState.value
        val nextStep = when (current.currentStep) {
            CBTReframeStep.INTRODUCTION -> CBTReframeStep.THOUGHT_IDENTIFICATION
            CBTReframeStep.THOUGHT_IDENTIFICATION -> CBTReframeStep.EMOTION_RATING
            CBTReframeStep.EMOTION_RATING -> CBTReframeStep.EVIDENCE_FOR
            CBTReframeStep.EVIDENCE_FOR -> CBTReframeStep.EVIDENCE_AGAINST
            CBTReframeStep.EVIDENCE_AGAINST -> CBTReframeStep.BALANCED_THINKING
            CBTReframeStep.BALANCED_THINKING -> CBTReframeStep.FINAL_RATING
            CBTReframeStep.FINAL_RATING -> return // Final step
        }
        
        val stepIndex = nextStep.ordinal
        val totalSteps = CBTReframeStep.values().size
        val progress = (stepIndex + 1).toFloat() / totalSteps.toFloat()
        
        _uiState.value = current.copy(
            currentStep = nextStep,
            progress = progress,
            canReset = true
        )
    }
    
    fun updateAutomaticThought(thought: String) {
        _uiState.value = _uiState.value.copy(automaticThought = thought)
    }
    
    fun updateEmotion(name: String, intensity: Int, isBefore: Boolean) {
        val current = _uiState.value
        val updatedEmotions = current.emotions.map { emotion ->
            if (emotion.name == name) {
                if (isBefore) {
                    emotion.copy(beforeIntensity = intensity)
                } else {
                    emotion.copy(afterIntensity = intensity)
                }
            } else {
                emotion
            }
        }
        _uiState.value = current.copy(emotions = updatedEmotions)
    }
    
    fun addEvidenceFor(evidence: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            evidenceFor = current.evidenceFor + evidence
        )
    }
    
    fun removeEvidenceFor(index: Int) {
        val current = _uiState.value
        if (index in current.evidenceFor.indices) {
            _uiState.value = current.copy(
                evidenceFor = current.evidenceFor - current.evidenceFor[index]
            )
        }
    }
    
    fun addEvidenceAgainst(evidence: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            evidenceAgainst = current.evidenceAgainst + evidence
        )
    }
    
    fun removeEvidenceAgainst(index: Int) {
        val current = _uiState.value
        if (index in current.evidenceAgainst.indices) {
            _uiState.value = current.copy(
                evidenceAgainst = current.evidenceAgainst - current.evidenceAgainst[index]
            )
        }
    }
    
    fun updateBalancedThought(thought: String) {
        _uiState.value = _uiState.value.copy(balancedThought = thought)
    }
    
    fun resetSession() {
        _uiState.value = CBTReframeUiState(
            currentStep = CBTReframeStep.INTRODUCTION,
            automaticThought = "",
            balancedThought = "",
            evidenceFor = emptyList(),
            evidenceAgainst = emptyList(),
            emotions = getDefaultEmotions(),
            progress = 1f/7f,
            canReset = false
        )
    }
    
    fun saveSession() {
        // TODO: Implement session persistence to database
        // For now, just mark as completed
        val current = _uiState.value
        
        // Log the session data (you'd save to database here)
        println("CBT Reframe Session Completed:")
        println("Original thought: ${current.automaticThought}")
        println("Balanced thought: ${current.balancedThought}")
        println("Evidence for: ${current.evidenceFor}")
        println("Evidence against: ${current.evidenceAgainst}")
        println("Emotions before/after: ${current.emotions.map { "${it.name}: ${it.beforeIntensity}→${it.afterIntensity}" }}")
    }
    
    private fun getDefaultEmotions(): List<CBTEmotion> {
        return listOf(
            CBTEmotion("Anxious", Icons.Default.Warning),
            CBTEmotion("Sad", Icons.Default.Face),
            CBTEmotion("Angry", Icons.Default.Star),
            CBTEmotion("Worried", Icons.Default.Settings),
            CBTEmotion("Overwhelmed", Icons.Default.Home)
        )
    }
}

data class CBTReframeUiState(
    val currentStep: CBTReframeStep,
    val automaticThought: String,
    val balancedThought: String,
    val evidenceFor: List<String>,
    val evidenceAgainst: List<String>,
    val emotions: List<CBTEmotion>,
    val progress: Float,
    val canReset: Boolean
)
