# CLAUDE.md - AI Assistant Guide for Appointment Booking App

**Last Updated:** 2025-12-07
**App Version:** 1.0
**Target SDK:** 35 (Android 15)

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Codebase Structure](#codebase-structure)
4. [Tech Stack & Dependencies](#tech-stack--dependencies)
5. [Development Workflows](#development-workflows)
6. [Coding Conventions](#coding-conventions)
7. [Common Tasks & Patterns](#common-tasks--patterns)
8. [Firebase Structure](#firebase-structure)
9. [Testing Guidelines](#testing-guidelines)
10. [Important Gotchas](#important-gotchas)

---

## Project Overview

### What This App Does

A **Doctor Appointment Booking** Android application with role-based access for **Patients** and **Doctors**. Key features include:

- User authentication with role selection (Patient/Doctor)
- Doctor browsing by specialization
- Appointment booking and management
- Real-time chat between patients and doctors
- Video calling using PeerJS (WebRTC)
- Favorite doctors management
- Appointment cancellation functionality

### User Roles

**Patient:**
- Browse doctors by category
- Book/cancel appointments
- Chat with doctors
- Video consultations
- Manage favorite doctors

**Doctor:**
- View scheduled appointments
- Chat with patients
- Conduct video consultations
- Manage availability through appointments

---

## Architecture & Design Patterns

### Clean Architecture + MVVM

The app follows a **three-layer Clean Architecture** with **MVVM**:

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                      │
│  - UI (Jetpack Compose)                                 │
│  - ViewModels (StateFlow-based state management)        │
│  - Navigation (Jetpack Navigation Compose)              │
└───────────────────────────┬─────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                          │
│  - Repository Interfaces (contracts)                     │
│  - Domain Models (pure Kotlin data classes)             │
│  - Business logic utilities                              │
└───────────────────────────┬─────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                     DATA LAYER                           │
│  - Repository Implementations                            │
│  - Remote Data Sources (Firebase)                        │
│  - Resource wrapper (Success/Error/Loading)             │
└─────────────────────────────────────────────────────────┘
```

### Key Principles

1. **Separation of Concerns**: Each layer has distinct responsibilities
2. **Dependency Rule**: Dependencies flow inward (Presentation → Domain ← Data)
3. **Single Source of Truth**: Repository pattern provides unified data access
4. **Reactive Programming**: StateFlow for state management
5. **Dependency Injection**: Hilt manages all dependencies

---

## Codebase Structure

### Directory Tree

```
app/src/main/java/com/example/appointmentbookingapp/
├── data/
│   ├── remorte/              # Firebase data sources (note: typo in original)
│   │   ├── AppointmentRemoteDataSource.kt
│   │   ├── AuthRemoteDataSource.kt
│   │   ├── CallRemoteDataSource.kt
│   │   ├── ChatRemoteDataSource.kt
│   │   ├── FavoritesRemoteDataSource.kt
│   │   ├── HomeRemoteDataSource.kt
│   │   └── ProfileRemoteDataSource.kt
│   └── repository/           # Repository implementations
│       ├── AppointmentRepositoryImpl.kt
│       ├── AuthRepositoryImpl.kt
│       ├── CallRepositoryImpl.kt
│       ├── ChatRepositoryImpl.kt
│       ├── FavoriteRepositoryImpl.kt
│       ├── HomeRepositoryImpl.kt
│       └── ProfileRepositoryImp.kt
│
├── di/                       # Dependency injection
│   ├── AppointmentApplication.kt
│   └── appModule.kt
│
├── domain/
│   ├── model/               # Domain models
│   │   ├── Appointment.kt
│   │   ├── AppointmentWithDoctor.kt
│   │   ├── AppointmentWithPatient.kt
│   │   ├── BannerItem.kt
│   │   ├── ChatListItem.kt
│   │   ├── ConversationItem.kt
│   │   ├── DoctorCategory.kt
│   │   ├── DoctorExtras.kt
│   │   ├── DoctorItem.kt
│   │   ├── Message.kt
│   │   └── User.kt
│   ├── repository/          # Repository interfaces
│   │   ├── AppointmentRepository.kt
│   │   ├── AuthRepository.kt
│   │   ├── CallRepository.kt
│   │   ├── ChatRepository.kt
│   │   ├── FavoriteRepository.kt
│   │   ├── HomeRepository.kt
│   │   └── ProfileRepository.kt
│   └── util/
│       └── AppointmentStatusString.kt
│
├── presentation/
│   ├── MainActivity.kt
│   ├── MainApp.kt           # Navigation graph
│   ├── state/
│   │   └── UiState.kt
│   └── ui/                  # Feature modules
│       ├── allCategories/
│       ├── allDoctors/
│       ├── appointment/
│       ├── auth/
│       ├── call/
│       ├── chat/
│       ├── components/      # Shared UI components
│       ├── doctorDetail/
│       ├── doctorHome/
│       ├── favorite/
│       ├── home/
│       ├── navigation/
│       ├── profile/
│       ├── roleselection/
│       ├── sharedviewmodel/
│       └── splash/
│
├── ui/theme/                # Material 3 theme
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
└── util/                    # App utilities
    ├── ChatUtils.kt
    ├── DateUtils.kt
    ├── Resource.kt
    ├── UserRole.kt
    └── getRandomColor.kt
```

### File Organization

- **92 Kotlin files** total
- **Feature-based organization** within `presentation/ui/`
- **Shared components** in `presentation/ui/components/`
- **Shared ViewModels** in `presentation/ui/sharedviewmodel/`

---

## Tech Stack & Dependencies

### Core Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Kotlin | 2.2.10 | Primary language |
| Jetpack Compose | BOM 2025.04.00 | Declarative UI |
| Material 3 | Latest | Design system |
| Hilt | 2.57 | Dependency injection |
| Firebase Auth | 23.2.0 | Authentication |
| Firebase Firestore | 25.1.3 | NoSQL database |
| Navigation Compose | 2.8.9 | Screen navigation |
| Coil | 3.1.0 | Image loading |

### Build Configuration

- **Compile SDK:** 35 (Android 15)
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 35
- **Java Version:** 11
- **AGP:** 8.12.1
- **KSP:** 2.2.10-2.0.2

### Key Dependencies

```kotlin
// Compose
androidx.compose.bom:2025.04.00
androidx.compose.material3
androidx.compose.material:material-icons-extended

// Navigation
androidx.navigation:navigation-compose:2.8.9

// Hilt
com.google.dagger:hilt-android:2.57
androidx.hilt:hilt-navigation-compose:1.2.0

// Firebase
com.google.firebase:firebase-auth:23.2.0
com.google.firebase:firebase-firestore:25.1.3

// Image Loading
io.coil-kt.coil3:coil-compose:3.1.0
io.coil-kt.coil3:coil-network-okhttp:3.1.0
```

---

## Development Workflows

### Adding a New Feature

#### 1. Define Domain Model (if needed)

**Location:** `domain/model/`

```kotlin
data class YourModel(
    val id: String = "",
    val name: String = "",
    // ... other fields
)
```

#### 2. Create Repository Interface

**Location:** `domain/repository/`

```kotlin
interface YourRepository {
    suspend fun getData(): Resource<List<YourModel>>
    suspend fun saveData(model: YourModel): Resource<Unit>
}
```

#### 3. Create Remote Data Source

**Location:** `data/remorte/` (note the typo in directory name)

```kotlin
class YourRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getData(): List<YourModel> {
        return firestore.collection("your_collection")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(YourModel::class.java) }
    }
}
```

#### 4. Implement Repository

**Location:** `data/repository/`

```kotlin
class YourRepositoryImpl @Inject constructor(
    private val remote: YourRemoteDataSource
) : YourRepository {
    override suspend fun getData(): Resource<List<YourModel>> {
        return try {
            val data = remote.getData()
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}
```

#### 5. Register in Hilt Module

**Location:** `di/appModule.kt`

```kotlin
@Provides
@Singleton
fun provideYourRepository(remote: YourRemoteDataSource): YourRepository {
    return YourRepositoryImpl(remote)
}
```

#### 6. Create ViewModel

**Location:** `presentation/ui/yourfeature/YourViewModel.kt`

```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val repository: YourRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<YourModel>>>(UiState.Initial)
    val state: StateFlow<UiState<List<YourModel>>> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _state.value = UiState.Loading
        when (val result = repository.getData()) {
            is Resource.Success -> _state.value = UiState.Success(result.data)
            is Resource.Error -> _state.value = UiState.Error(result.message)
            is Resource.Loading -> _state.value = UiState.Loading
        }
    }
}
```

#### 7. Create UI Screen

**Location:** `presentation/ui/yourfeature/YourScreen.kt`

```kotlin
@Composable
fun YourScreen(
    navController: NavHostController,
    viewModel: YourViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold { padding ->
        when (state) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val data = (state as UiState.Success<List<YourModel>>).data
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(data) { item ->
                        // Render item
                    }
                }
            }
            is UiState.Error -> {
                val message = (state as UiState.Error).message
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
```

#### 8. Add to Navigation

**Location:** `presentation/MainApp.kt`

```kotlin
composable("YourScreen") {
    YourScreen(navController = navController)
}
```

### Modifying Existing Features

1. **Read the relevant files first** using the Read tool
2. **Understand the existing pattern** before making changes
3. **Follow the established architecture** (don't mix patterns)
4. **Update all three layers** if the change affects data flow
5. **Test navigation flows** after UI changes

### Adding UI Components

#### Reusable Components

**Location:** `presentation/ui/components/`

```kotlin
@Composable
fun YourComponent(
    data: YourModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Component implementation
}
```

#### Feature-Specific Components

Place in the feature's directory (e.g., `presentation/ui/home/components/`)

---

## Coding Conventions

### Kotlin Style

- **Use data classes** for models
- **Default parameters** in data classes for Firebase deserialization
- **Named parameters** for improved readability
- **Trailing commas** in multi-line declarations
- **Explicit types** for public APIs

### Compose Conventions

```kotlin
// ✅ Good: Clear parameter naming
@Composable
fun DoctorCard(
    doctor: DoctorItem,
    isFavorite: Boolean,
    onDoctorClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
)

// ✅ Good: Modifier as last parameter with default
// ✅ Good: Lambda parameters at the end
// ✅ Good: State hoisting (parent controls state)
```

### ViewModel Conventions

```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val repository: YourRepository
) : ViewModel() {

    // Private mutable state
    private val _state = MutableStateFlow<UiState<Data>>(UiState.Initial)

    // Public immutable state
    val state: StateFlow<UiState<Data>> = _state.asStateFlow()

    // Use viewModelScope for coroutines
    fun loadData() = viewModelScope.launch {
        // Implementation
    }
}
```

### Resource Wrapper Pattern

**Always use** `Resource<T>` for repository return types:

```kotlin
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
```

### UiState Pattern

**Always use** `UiState<T>` for ViewModel state:

```kotlin
sealed class UiState<out T> {
    data object Initial : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Navigation Patterns

```kotlin
// ✅ Good: Use NavController for navigation
navController.navigate("ScreenName") {
    popUpTo("HomeScreen") { inclusive = false }
}

// ✅ Good: Pass data via shared ViewModels
sharedViewModel.selectDoctor(doctor)
navController.navigate("DoctorDetail")

// In destination screen
val doctor by sharedViewModel.selectedDoctor.collectAsState()
```

### Firebase Patterns

```kotlin
// ✅ Good: Use suspend functions with await()
suspend fun getData(): List<Model> {
    return firestore.collection("collection")
        .get()
        .await()
        .documents
        .mapNotNull { it.toObject(Model::class.java) }
}

// ✅ Good: Use ListenerRegistration for real-time updates
fun listenToData(callback: (List<Model>) -> Unit): ListenerRegistration {
    return firestore.collection("collection")
        .addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val data = snapshot?.documents?.mapNotNull {
                it.toObject(Model::class.java)
            } ?: emptyList()
            callback(data)
        }
}
```

---

## Common Tasks & Patterns

### Task 1: Add a New Screen

1. Create screen composable in `presentation/ui/featurename/`
2. Create ViewModel if needed (use `@HiltViewModel`)
3. Add route to `MainApp.kt` navigation graph
4. Add navigation calls from other screens

### Task 2: Add Role-Specific Feature

```kotlin
// Check user role
val userRole by userRoleViewModel.userRole.collectAsState()

when (userRole) {
    UserRole.PATIENT -> {
        // Patient-specific UI
    }
    UserRole.DOCTOR -> {
        // Doctor-specific UI
    }
}
```

### Task 3: Implement Real-Time Updates

```kotlin
// In ViewModel
private var messagesListener: ListenerRegistration? = null

fun startListening(chatId: String) {
    messagesListener = repository.listenToMessages(chatId) { messages ->
        _messages.value = messages
    }
}

override fun onCleared() {
    super.onCleared()
    messagesListener?.remove()
}
```

### Task 4: Handle Loading States

```kotlin
// In Composable
when (val currentState = state) {
    is UiState.Loading -> {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    is UiState.Success -> {
        SuccessContent(currentState.data)
    }
    is UiState.Error -> {
        ErrorMessage(currentState.message)
    }
    UiState.Initial -> {
        // Initial state
    }
}
```

### Task 5: Create Dialogs

**Pattern from existing dialogs:**

```kotlin
@Composable
fun YourDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
```

### Task 6: Add to Bottom Navigation

**Location:** `presentation/ui/navigation/BottomNavigationBar.kt`

Update the navigation items based on role requirements.

### Task 7: Theme Customization

**Colors:** `ui/theme/Color.kt`
**Typography:** `ui/theme/Type.kt`
**Theme:** `ui/theme/Theme.kt`

The app supports light/dark mode automatically via Material 3.

---

## Firebase Structure

### Collections Schema

```
Firestore
├── users/{userId}
│   ├── id: String
│   ├── name: String
│   ├── email: String
│   ├── password: String (hashed)
│   ├── profileUrl: String
│   ├── role: String ("patient" | "doctor")
│   └── appointments/{appointmentId}
│       └── appointmentId: String (reference)
│
├── doctors/{doctorId}
│   ├── id: String
│   ├── name: String
│   ├── email: String
│   ├── aboutDoctor: String
│   ├── imageUrl: String
│   ├── rating: String
│   ├── docCategory: String
│   ├── experienceYears: Int
│   ├── consultationFee: String
│   ├── languagesSpoken: List<String>
│   ├── gender: String
│   ├── reviewsCount: Int
│   ├── role: String ("doctor")
│   └── appointments/{appointmentId}
│       └── appointmentId: String (reference)
│
├── appointments/{appointmentId}
│   ├── appointmentId: String
│   ├── patientId: String
│   ├── doctorId: String
│   ├── appointmentDate: Timestamp
│   ├── timeSlot: String
│   ├── status: String ("pending" | "confirmed" | "cancelled" | "completed")
│   ├── peerId: String (for video calls)
│   └── callState: String
│
├── banner/{bannerId}
│   ├── id: String
│   └── imageUrl: String
│
├── doctorCategories/{categoryId}
│   ├── label: String
│   ├── backgroundColor: String (hex color)
│   └── categoryIcon: String (icon name)
│
├── messages/{messageId}
│   ├── messageId: String
│   ├── chatId: String (format: "patientId_doctorId")
│   ├── senderId: String
│   ├── receiverId: String
│   ├── patientId: String
│   ├── doctorId: String
│   ├── content: String
│   └── timestamp: Long
│
└── favorites/{userId}
    └── {doctorId}: Boolean (or document with doctor data)
```

### Firebase Security Rules (Recommended)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Doctors collection readable by all authenticated users
    match /doctors/{doctorId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == doctorId;
    }

    // Appointments readable by involved parties
    match /appointments/{appointmentId} {
      allow read: if request.auth != null &&
        (resource.data.patientId == request.auth.uid ||
         resource.data.doctorId == request.auth.uid);
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
        (resource.data.patientId == request.auth.uid ||
         resource.data.doctorId == request.auth.uid);
    }

    // Messages readable by sender/receiver
    match /messages/{messageId} {
      allow read: if request.auth != null &&
        (resource.data.senderId == request.auth.uid ||
         resource.data.receiverId == request.auth.uid);
      allow create: if request.auth != null;
      allow delete: if request.auth != null &&
        resource.data.senderId == request.auth.uid;
    }

    // Public collections
    match /banner/{bannerId} {
      allow read: if request.auth != null;
    }

    match /doctorCategories/{categoryId} {
      allow read: if request.auth != null;
    }

    // Favorites per user
    match /favorites/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## Testing Guidelines

### Unit Testing (Recommended Setup)

**Test ViewModels:**

```kotlin
@ExperimentalCoroutinesApi
class YourViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: YourViewModel
    private lateinit var repository: YourRepository

    @Before
    fun setup() {
        repository = mockk()
        viewModel = YourViewModel(repository)
    }

    @Test
    fun `loadData emits success state when repository returns data`() = runTest {
        // Given
        val expectedData = listOf(/* test data */)
        coEvery { repository.getData() } returns Resource.Success(expectedData)

        // When
        viewModel.loadData()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UiState.Success)
        assertEquals(expectedData, (state as UiState.Success).data)
    }
}
```

### Integration Testing

Test navigation flows, user interactions, and Firebase integration in `androidTest/`.

### Manual Testing Checklist

- [ ] Patient signup and login
- [ ] Doctor signup and login
- [ ] Browse doctors by category
- [ ] Book appointment
- [ ] Cancel appointment
- [ ] Chat functionality
- [ ] Video call functionality
- [ ] Favorite doctors
- [ ] Profile viewing/editing
- [ ] Dark mode switching
- [ ] Role-based navigation

---

## Important Gotchas

### 1. Directory Naming Typo

⚠️ **The remote data sources directory is named `remorte` (missing the 'e')**

**Location:** `data/remorte/`

When adding new data sources, maintain consistency with existing naming.

### 2. User Role Constants

Always use the constants from `UserRole.kt`:

```kotlin
object UserRole {
    const val PATIENT = "patient"
    const val DOCTOR = "doctor"
    const val NONE = ""
}

// ✅ Good
if (role == UserRole.PATIENT) { }

// ❌ Bad
if (role == "patient") { }
```

### 3. Firebase Timestamp Handling

Use `DateUtils.kt` for consistent date formatting across the app.

### 4. Appointment Status

Always use constants from `AppointmentStatusString.kt`:

```kotlin
object AppointmentStatusString {
    const val PENDING = "pending"
    const val CONFIRMED = "confirmed"
    const val CANCELLED = "cancelled"
    const val COMPLETED = "completed"
}
```

### 5. Chat ID Format

Chat IDs follow a specific pattern: `"${patientId}_${doctorId}"`

**Always maintain this format** to ensure chat uniqueness and proper querying.

### 6. State Management

- **Never expose mutable state** from ViewModels
- **Always use** `StateFlow<T>` with `asStateFlow()`
- **Collect state** in Composables using `collectAsState()`

### 7. Navigation

- **Use SharedViewModels** to pass complex data between screens
- **Avoid passing large objects** in navigation arguments
- **Clear back stack** appropriately to prevent memory leaks

### 8. Firebase Listeners

**Always remove listeners** in `ViewModel.onCleared()`:

```kotlin
override fun onCleared() {
    super.onCleared()
    listenerRegistration?.remove()
}
```

### 9. Permissions

The app requires these runtime permissions (already declared):

- `INTERNET` (always granted)
- `CAMERA` (for video calls)
- `RECORD_AUDIO` (for video calls)
- `POST_NOTIFICATIONS` (for future notification feature)

**Request CAMERA and RECORD_AUDIO** before starting video calls.

### 10. Image Loading

Use **Coil** for all image loading:

```kotlin
AsyncImage(
    model = imageUrl,
    contentDescription = "Description",
    modifier = Modifier.size(120.dp),
    contentScale = ContentScale.Crop
)
```

### 11. Material 3 Theme

The app uses **Material 3** with dynamic color support. When adding colors:

- Define in `Color.kt`
- Add to both light and dark color schemes in `Theme.kt`
- Test in both light and dark modes

### 12. Hilt Dependency Graph

All repositories and data sources are **singletons**. Don't create multiple instances manually.

### 13. Video Calling (PeerJS)

- Peer IDs are stored in Firestore under appointments
- Call state management uses Firebase for synchronization
- Ensure proper cleanup when call ends

### 14. Appointment Booking Logic

- **Always check slot availability** before booking
- Use **Firebase server timestamp** for consistency
- Store appointment in **3 locations** (appointments, user, doctor subcollections)

### 15. Search Functionality

Doctor search is **case-sensitive** in current implementation. Consider adding `.lowercase()` for better UX.

---

## Quick Reference

### Common File Locations

| Task | File Location |
|------|--------------|
| Add new screen | `presentation/ui/featurename/ScreenName.kt` |
| Add ViewModel | `presentation/ui/featurename/ViewModelName.kt` |
| Add repository interface | `domain/repository/RepositoryName.kt` |
| Add repository impl | `data/repository/RepositoryNameImpl.kt` |
| Add data source | `data/remorte/DataSourceName.kt` |
| Add domain model | `domain/model/ModelName.kt` |
| Add to DI | `di/appModule.kt` |
| Add to navigation | `presentation/MainApp.kt` |
| Add shared component | `presentation/ui/components/ComponentName.kt` |
| Add theme color | `ui/theme/Color.kt` |

### Navigation Routes Reference

```kotlin
// Auth Flow
"RoleSelectionScreen"
"SignUp"
"SignIn"
"CompleteProfileScreen"

// Patient Flow
"HomeScreen"
"DoctorDetail"
"BookAppointment"
"DoctorScreen"
"AllDoctorCategories"
"FavoriteScreen"

// Doctor Flow
"DoctorHomeScreenn"

// Shared
"ChatListScreen"
"ChatScreen"
"MyAppointmentsScreen"
"ProfileScreen"
"CallScreen"
"WaitingRoomScreen"
```

### ViewModel Template

```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: FeatureRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<DataType>>(UiState.Initial)
    val state: StateFlow<UiState<DataType>> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _state.value = UiState.Loading
        when (val result = repository.getData()) {
            is Resource.Success -> _state.value = UiState.Success(result.data)
            is Resource.Error -> _state.value = UiState.Error(result.message)
            is Resource.Loading -> _state.value = UiState.Loading
        }
    }

    fun onEvent(event: FeatureEvent) {
        when (event) {
            is FeatureEvent.Action -> handleAction()
        }
    }
}
```

---

## Future Enhancements (TODOs from README)

- [ ] Push Notifications for Appointment Reminders
- [ ] Doctor Availability Calendar Integration
- [ ] Ratings & Reviews for Doctors
- [x] Video Calling (Telehealth Support using PeerJS) ✅
- [ ] Payment Gateway Integration (for paid consultations)

When implementing these features, follow the architecture patterns established in this document.

---

## Questions or Issues?

When encountering issues:

1. **Check this document** for conventions and patterns
2. **Read related files** to understand existing implementations
3. **Follow established patterns** rather than creating new ones
4. **Test thoroughly** in both Patient and Doctor roles
5. **Verify Firebase data structure** matches schema

---

**End of CLAUDE.md**
