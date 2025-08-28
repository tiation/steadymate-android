package com.steadymate.app.ui.theme.accessibility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag

/**
 * Extension functions for enhanced accessibility semantics
 */

/**
 * Adds button role using the new mutable property approach
 */
fun Modifier.buttonRole(): Modifier = semantics { this.role = Role.Button }

/**
 * Adds checkbox role using the new mutable property approach
 */
fun Modifier.checkboxRole(): Modifier = semantics { this.role = Role.Checkbox }

/**
 * Adds radio button role using the new mutable property approach
 */
fun Modifier.radioButtonRole(): Modifier = semantics { this.role = Role.RadioButton }

/**
 * Adds tab role using the new mutable property approach
 */
fun Modifier.tabRole(): Modifier = semantics { this.role = Role.Tab }

/**
 * Adds switch role using the new mutable property approach
 */
fun Modifier.switchRole(): Modifier = semantics { this.role = Role.Switch }

/**
 * Adds image role using the new mutable property approach
 */
fun Modifier.imageRole(): Modifier = semantics { this.role = Role.Image }

/**
 * Adds dropdown list role using the new mutable property approach
 */
fun Modifier.dropdownListRole(): Modifier = semantics { this.role = Role.DropdownList }

/**
 * Safely reads the role property from a SemanticsConfiguration
 * Use this when you need read-only access to the role property
 */
fun SemanticsConfiguration.getRoleSafely(): Role? = this.getOrNull(SemanticsProperties.Role)

/**
 * Helper function to check if a component has a specific role
 */
fun SemanticsConfiguration.hasRole(expectedRole: Role): Boolean = 
    getRoleSafely() == expectedRole

/**
 * Adds accessibility semantics for screen readers with proper content descriptions
 */
fun Modifier.accessibleDescription(
    description: String,
    role: Role? = null,
    testTag: String? = null
): Modifier = this.semantics {
    contentDescription = description
    role?.let { this.role = it }
    testTag?.let { this.testTag = it }
}

/**
 * Marks content as a heading for screen readers
 */
fun Modifier.accessibleHeading(
    description: String? = null
): Modifier = this.semantics {
    heading()
    description?.let { contentDescription = it }
}

/**
 * Creates a live region for dynamic content updates
 */
fun Modifier.accessibleLiveRegion(
    mode: LiveRegionMode = LiveRegionMode.Polite
): Modifier = this.semantics {
    liveRegion = mode
}

/**
 * Adds state description for toggleable elements
 */
fun Modifier.accessibleState(
    stateDescription: String
): Modifier = this.semantics {
    this.stateDescription = stateDescription
}

/**
 * Creates an accessible button with proper semantics and touch targets
 */
@Composable
fun Modifier.accessibleButton(
    label: String,
    enabled: Boolean = true,
    role: Role = Role.Button,
    onClick: () -> Unit
): Modifier {
    val preferences = rememberAccessibilityPreferences()
    
    return this
        .accessibleTouchTarget()
        .clickable(
            enabled = enabled,
            role = role,
            interactionSource = remember { MutableInteractionSource() },
            indication = if (preferences.isReducedMotion) null else rememberRipple(),
            onClick = onClick
        )
        .accessibleDescription(label, role)
}

/**
 * Creates an accessible toggle with proper semantics
 */
@Composable
fun Modifier.accessibleToggle(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
): Modifier {
    val preferences = rememberAccessibilityPreferences()
    val stateText = if (checked) "On" else "Off"
    
    return this
        .accessibleTouchTarget()
        .toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = remember { MutableInteractionSource() },
            indication = if (preferences.isReducedMotion) null else rememberRipple(),
            onValueChange = onCheckedChange
        )
        .accessibleDescription(label, Role.Checkbox)
        .accessibleState("$label, $stateText")
}

/**
 * Creates an accessible selectable item with proper semantics
 */
@Composable
fun Modifier.accessibleSelectable(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val preferences = rememberAccessibilityPreferences()
    val stateText = if (selected) "Selected" else "Not selected"
    
    return this
        .accessibleTouchTarget()
        .selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = remember { MutableInteractionSource() },
            indication = if (preferences.isReducedMotion) null else rememberRipple(),
            onClick = onClick
        )
        .accessibleDescription(label, Role.RadioButton)
        .accessibleState("$label, $stateText")
}

/**
 * Creates an accessible clickable area with invisible overlay for custom components
 */
@Composable
fun Modifier.accessibleClickableOverlay(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    return this.then(
        Modifier.semantics(mergeDescendants = true) {
            contentDescription = label
            role = Role.Button
        }
    ).clickable(
        enabled = enabled,
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    )
}

/**
 * Removes semantics from decorative elements
 */
fun Modifier.decorativeElement(): Modifier = this.clearAndSetSemantics { }

/**
 * Focuses an element for accessibility
 */
fun Modifier.accessibleFocusable(
    enabled: Boolean = true
): Modifier = if (enabled) this.focusable() else this

/**
 * Helper composable for accessible error announcements
 */
@Composable
fun AccessibleErrorAnnouncement(
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    if (errorMessage != null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .accessibleLiveRegion(LiveRegionMode.Assertive)
                .accessibleDescription(
                    description = "Error: $errorMessage",
                    role = Role.Button
                )
        )
    }
}

/**
 * Helper composable for accessible status announcements
 */
@Composable
fun AccessibleStatusAnnouncement(
    statusMessage: String?,
    modifier: Modifier = Modifier
) {
    if (statusMessage != null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .accessibleLiveRegion(LiveRegionMode.Polite)
                .accessibleDescription(
                    description = statusMessage,
                    role = Role.Button
                )
        )
    }
}
