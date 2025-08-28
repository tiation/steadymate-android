# SteadyMate Insights System

## Architecture Overview

The insights system provides comprehensive mood analytics through a clean MVVM architecture with reactive data flows.

### 🎯 Features Implemented

✅ **Real-time Data Integration**: Room-backed repository with reactive flows  
✅ **Beautiful Visualizations**: Vico charts with accessibility support  
✅ **Smart Analytics**: Emotion analysis, mood trends, activity correlations  
✅ **Intelligent Insights**: AI-generated recommendations and pattern detection  
✅ **Time Range Filtering**: Week, Month, 3M, 6M, Year views  

### 📱 Components

#### UI Layer
- `InsightsScreen.kt` - Main screen with responsive Material 3 design
- `InsightsViewModel.kt` - Reactive ViewModel with StateFlow architecture
- `MoodLineChart.kt` - Beautiful line chart for mood trends
- `EmotionBarChart.kt` - Colorful bar chart for emotion analysis

#### Data Layer
- `InsightsRepositoryRoom.kt` - Room-backed real data implementation
- `InsightsRepositoryImpl.kt` - Mock data for development/previews
- `MoodEntryDao.kt` - SQL aggregation queries for analytics

#### Domain Layer
- `GetMoodTrendUseCase.kt` - Business logic for mood trend calculations
- `GetEmotionAnalysisUseCase.kt` - Emotion pattern analysis
- `GetMoodStatisticsUseCase.kt` - Statistical calculations and streaks

### 🔄 Data Flow

```
UI State ← ViewModel ← Use Cases ← Repository ← Room ← Database
    ↑                                ↓
StateFlow            Repository Impl (Mock/Real)
```

### 🎨 Design System

- **Material 3** color scheme with semantic colors
- **Accessibility-first** with content descriptions and proper contrast
- **Responsive** layouts that adapt to screen sizes
- **Smooth animations** with proper motion design

### 🔧 Technical Details

#### Reactive Architecture
- `StateFlow` for UI state management
- `combine()` for merging multiple data sources
- Proper error handling with retry mechanisms
- Offline-first with local Room database

#### Smart Analytics
- **Mood Trends**: Statistical analysis with improvement percentages
- **Emotion Patterns**: Frequency analysis with mood correlations  
- **Activity Impact**: Keyword-based activity correlation from notes
- **Streaks**: Current and longest streak calculations
- **Insights Generation**: Rule-based recommendations

#### Chart Implementation
- **Vico Compose** for professional chart rendering
- **Accessibility**: Screen reader support with detailed descriptions
- **Responsive**: Adapts to light/dark themes
- **Interactive**: Touch-friendly with proper minimum target sizes

### 🚀 Usage

```kotlin
@Composable
fun MyScreen() {
    // ViewModel is automatically injected with Hilt
    val viewModel: InsightsViewModel = hiltViewModel()
    
    InsightsScreen(
        modifier = Modifier.fillMaxSize()
    )
}
```

### 📊 Data Sources

1. **Mood Entries**: Primary source from user check-ins
2. **Emotion Tags**: Analyzed for patterns and frequencies
3. **Notes Content**: Parsed for activity keywords
4. **Timestamps**: Used for streak and trend calculations

### 🎯 Future Enhancements

- [ ] ML-powered pattern recognition
- [ ] Export functionality (CSV, PDF reports)
- [ ] Push notification insights
- [ ] Comparative analysis (week-over-week)
- [ ] Custom insight rules
- [ ] Data visualization themes

### 🧪 Testing Strategy

- **Unit Tests**: Repository and ViewModel logic
- **UI Tests**: Chart rendering and state changes  
- **Integration Tests**: End-to-end data flow
- **Accessibility Tests**: Screen reader compatibility

### 🔒 Privacy & Security

- **Local-first**: All data stays on device
- **No external APIs**: Complete offline functionality
- **Encrypted storage**: Room database with encryption
- **No tracking**: Zero analytics or telemetry

---

*Built with ❤️ using Jetpack Compose, Room, Hilt, and Vico Charts*
