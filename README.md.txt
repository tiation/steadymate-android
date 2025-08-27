

# Build an Android App: Men’s Mental Health (Native Kotlin, Material 3, Compose)

## Role

You are a senior Android engineer + product designer. Generate **production-ready** Android code using **Kotlin + Jetpack Compose + Material 3**, following **Android Architecture Guidelines (MVVM)** with **Hilt**, **Room**, **DataStore**, **WorkManager**, and **Navigation Compose**. The output should compile and run.

## App Name (working)

**SteadyMate** — an interactive, stigma-aware men’s mental health companion.

## Goals

* Reduce friction for help-seeking.
* Nudge daily self-check-ins and simple actions.
* Offer private tools that work **fully offline** with **optional cloud sync** (user must opt-in).
* Follow **Material Design 3** and **accessibility** best practices.

## Core Features (MVP)

1. **Daily Check-In**

   * 30-second mood slider (0–10), tags (e.g., work, sleep, family), one-line note.
   * Streaks + gentle encouragement (no shame language).
   * Visual trend chart (7/30/90 days).

2. **Guided Breathing & Grounding**

   * 1–5 minute breathing timer (box, 4-7-8, custom), haptics + animation.
   * Quick “Reset” card on home screen.

3. **CBT-Lite Tools (on-device)**

   * Thought reframing: Situation → Thought → Evidence For/Against → Balanced thought.
   * Worry timer + “park it” notes.
   * Micro-wins tracker (3 good things).

4. **Crisis & Support Plan**

   * One-tap **SOS** screen with user-defined trusted contacts.
   * Safety plan (warning signs, coping strategies, reasons for living).
   * Region-agnostic: allow user to set local helplines (no hardcoded advice).

5. **Habits & Micro-Actions**

   * Small, binary habits (walk 5 min, water, message a mate).
   * Reminders with notification channels; skip without guilt.

6. **Insights**

   * Private analytics: mood vs. sleep, exercise, alcohol (toggle fields).
   * All charts **on-device**; no PII leaves device unless user opts-in.

## Non-Goals (MVP)

* No social feed or therapy claims.
* No always-on tracking; everything explicit and user-controlled.

## Tech & Architecture

* **Language**: Kotlin
* **UI**: Jetpack Compose + Material 3 (dynamic color, dark mode by default)
* **Navigation**: Navigation Compose (type-safe routes)
* **DI**: Hilt
* **Persistence**: Room (journals, habits, events), DataStore (prefs, flags)
* **Background**: WorkManager (reminders, periodic summaries)
* **Charts**: Compose-friendly chart lib or custom Canvas charts
* **Testing**: JUnit5, Turbine (flows), MockK, Espresso/Compose UI tests
* **Lint/QA**: ktlint, Detekt, baseline profiles for startup

## Privacy, Safety, Consent

* Local-first, **no analytics by default**.
* Clear **consent screens** for any optional sync/backup.
* **Panic hide** option: disguise app icon/name (optional stretch), quick-close gesture.
* Positive, non-pathologizing language; content warnings where relevant.
* Disclaimer screen (not a medical device; emergency instructions).

## Information Architecture / Screens

* **Onboarding (3–5 screens)**

  * Welcome → Consent → Personalize (goals, reminders) → Add supports (contacts/helplines) → Done
* **Home**

  * “How are you today?” card → Check-in button
  * Quick tools row: Breathe, Ground, Reframe, Micro-win
  * Streak + gentle nudge chip
* **Check-In**

  * Mood slider, tags (chips), optional note; Save → brief confetti micro-animation
* **Tools**

  * **Breathe** (animated), **Reframe** (wizard), **Worry Timer**, **3 Good Things**
* **Habits**

  * List (toggle complete), add/edit habit, reminder time, weekdays
* **Insights**

  * Charts: mood over time, correlations (toggleable fields)
* **Crisis Plan**

  * Safety plan editor, one-tap call/message trusted contacts
* **Settings**

  * Theme (follow system, dark, light), reminders, privacy/export, data reset, About

## UX & Accessibility

* Material 3 **TopAppBar**, **NavigationBar** (Home, Tools, Habits, Insights, Settings)
* Large touch targets (48dp), minimum 14sp body, high contrast, TalkBack labels
* Haptics subtle; animations **respect Reduce Motion**
* Copy tone: mate-to-mate, encouraging, shame-free

## Data Model (initial)

```kotlin
@Entity data class MoodEntry(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val timestamp: Long,
  val score: Int, // 0..10
  val tags: List<String>, // use TypeConverter
  val note: String?
)

@Entity data class Habit(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val schedule: String, // e.g., "MTWTF--"
  val reminderTime: String?, // "07:30"
  val enabled: Boolean = true
)

@Entity(primaryKeys=["habitId","date"]) data class HabitTick(
  val habitId: String,
  val date: String, // yyyy-MM-dd
  val done: Boolean
)

@Entity data class ReframeEntry(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val timestamp: Long,
  val situation: String,
  val thought: String,
  val evidenceFor: String,
  val evidenceAgainst: String,
  val balancedThought: String
)

data class SafetyPlan(
  val warningSigns: List<String>,
  val copingStrategies: List<String>,
  val supports: List<SupportContact>
)

data class SupportContact(
  val name: String,
  val phone: String,
  val relationship: String
)
```

## Repositories & Use Cases

* `MoodRepository`: add/get range, stats (moving avg)
* `HabitRepository`: CRUD habits, ticks, schedule helpers
* `ReframeRepository`: CRUD reframes
* UseCases: `SubmitDailyCheckIn`, `CompleteHabit`, `GenerateInsights`

## Navigation Routes (example)

```
home, checkin, tools, tools/breathe, tools/reframe, habits, habits/edit/{id?},
insights, crisis, settings, onboarding/{step}
```

## Dependencies (Gradle snippets)

```kotlin
plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
}

dependencies {
  implementation(platform("androidx.compose:compose-bom:2025.01.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.navigation:navigation-compose:2.8.0")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
  implementation("com.google.dagger:hilt-android:2.51")
  kapt("com.google.dagger:hilt-compiler:2.51")
  implementation("androidx.room:room-runtime:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  implementation("androidx.datastore:datastore-preferences:1.1.1")
  implementation("androidx.work:work-runtime-ktx:2.9.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
}
```

## Sample UI Briefs (Compose)

* **Check-In Card**

  * `Slider` (0–10) with tick marks; chips for tags (“work”, “sleep”, “gym”, “mates”).
  * CTA “Save today’s check-in”.
* **Breathe Screen**

  * Circular animated guide with in/hold/out timings; `Vibrator` for cues.
* **Insights**

  * Line chart for mood; bar chart for habits completed; toggle chips for overlays.

## Local-First Export/Import

* JSON export to user-selected location (SAF).
* Optional encrypted backup to user’s cloud (Drive/Dropbox) only if enabled.

## Notifications

* Channels: **Check-In**, **Habits**, **Encouragement** (low-key).
* Respect Do Not Disturb; allow “Snooze a week.”

## Acceptance Criteria

* App installs and launches to **Onboarding** if first run; otherwise **Home**.
* I can complete a check-in in **< 30 seconds**.
* All data (journal, habits, reframes) persists across restarts (Room).
* Works offline; no internet required after install.
* Dark mode, dynamic color, TalkBack readable controls.
* Unit tests for repositories; UI tests for check-in flow and habit toggle.
* No network calls unless user enables backup; no third-party trackers.

## Nice-to-Have (stretch)

* **Widget**: 1-tap check-in.
* **Wear OS** mini breathing session.
* **Baseline Profiles** for startup perf.
* **App icon masking** (privacy mode).

## Deliverables

* A complete Android Studio project with:

  * Modularized packages: `ui/*`, `domain/*`, `data/*`, `di/*`
  * Hilt setup, Room schema, DataStore prefs
  * Compose screens for all features above
  * Unit + UI tests
  * README with build/run steps

---

