package com.steadymate.app.ui.theme.accessibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steadymate.app.ui.theme.SteadyMateTheme

/**
 * Example composable demonstrating accessibility features in SteadyMateTheme
 */
@Composable
fun AccessibilityExamples(modifier: Modifier = Modifier) {
    val preferences = rememberAccessibilityPreferences()
    var isVisible by remember { mutableStateOf(true) }
    var isToggled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Accessible heading
        Text(
            text = "Accessibility Demo",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.accessibleHeading("Main accessibility demo heading")
        )

        // Current settings display
        Text(
            text = "Current Settings:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.accessibleHeading()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Large Font: ${preferences.isLargeFont}")
            Text("High Contrast: ${preferences.isHighContrast}")
            Text("Reduced Motion: ${preferences.isReducedMotion}")
            Text("Dark Mode: ${preferences.isDarkMode}")
            Text("Dynamic Color: ${preferences.isDynamicColor}")
            Text("Large Touch Targets: ${preferences.largeTouchTargets}")
        }

        // Accessible button with proper touch targets
        Button(
            onClick = { isVisible = !isVisible },
            modifier = Modifier.accessibleButton(
                label = "Toggle visibility of demo content",
                onClick = { isVisible = !isVisible }
            )
        ) {
            Text("Toggle Demo Content")
        }

        // Animated visibility with accessibility considerations
        AnimatedVisibility(
            visible = isVisible,
            enter = AccessibleAnimations.accessibleEnterTransition(preferences),
            exit = AccessibleAnimations.accessibleExitTransition(preferences)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Demo Content",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.accessibleHeading("Demo content section")
                )

                // Accessible toggle switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = isToggled,
                        onCheckedChange = { isToggled = it },
                        modifier = Modifier.accessibleToggle(
                            label = "Demo toggle switch",
                            checked = isToggled,
                            onCheckedChange = { isToggled = it }
                        )
                    )
                    Text("Demo Switch")
                }

                // Button with custom touch target
                Button(
                    onClick = { /* Action */ },
                    modifier = Modifier
                        .largeTouchTarget(enabled = preferences.largeTouchTargets)
                        .accessibleDescription(
                            description = "Perform custom action",
                            role = androidx.compose.ui.semantics.Role.Button
                        )
                ) {
                    Text("Custom Action")
                }

                // Status message with live region
                Text(
                    text = "Status: ${if (isToggled) "Active" else "Inactive"}",
                    modifier = Modifier.accessibleLiveRegion()
                )
            }
        }

        // Error announcement example
        if (preferences.isReducedMotion) {
            AccessibleStatusAnnouncement(
                statusMessage = "Reduced motion is enabled - animations are simplified"
            )
        }

        // High contrast warning
        if (preferences.isHighContrast) {
            AccessibleStatusAnnouncement(
                statusMessage = "High contrast mode is active for better visibility"
            )
        }
    }
}

/**
 * Preview showing normal accessibility
 */
@Preview(
    name = "Normal Accessibility",
    showBackground = true
)
@Composable
private fun AccessibilityExamplesPreview() {
    SteadyMateTheme {
        AccessibilityExamples()
    }
}

/**
 * Preview showing dark theme with high contrast
 */
@Preview(
    name = "Dark High Contrast",
    showBackground = true
)
@Composable
private fun AccessibilityExamplesDarkHighContrastPreview() {
    SteadyMateTheme(
        darkTheme = true,
        highContrast = true
    ) {
        AccessibilityExamples()
    }
}

/**
 * Preview showing large font mode
 */
@Preview(
    name = "Large Font",
    showBackground = true
)
@Composable
private fun AccessibilityExamplesLargeFontPreview() {
    SteadyMateTheme(
        largeFont = true
    ) {
        AccessibilityExamples()
    }
}

/**
 * Preview showing all accessibility features enabled
 */
@Preview(
    name = "All Accessibility Features",
    showBackground = true
)
@Composable
private fun AccessibilityExamplesAllFeaturesPreview() {
    SteadyMateTheme(
        darkTheme = true,
        highContrast = true,
        largeFont = true,
        reducedMotion = true
    ) {
        AccessibilityExamples()
    }
}

/**
 * Example of accessibility-first button component
 */
@Composable
fun AccessibleActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.accessibleButton(
            label = description ?: text,
            enabled = enabled,
            onClick = onClick
        )
    ) {
        Text(text)
    }
}

/**
 * Example of an accessible card component with proper semantics
 */
@Composable
fun AccessibleCard(
    title: String,
    content: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .then(
                if (onClick != null) {
                    Modifier.accessibleClickableOverlay(
                        label = "Card: $title. $content",
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.accessibleHeading(title)
        )
        
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
