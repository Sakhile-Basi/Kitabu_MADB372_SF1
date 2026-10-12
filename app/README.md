# Kitabu — Library Book Rental & Reservation System

Kitabu is a native Android application developed for the MADB372 SF1 project. It allows a library user to browse a local book catalogue, search and filter titles, reserve available books, manage the booking lifecycle, and view active rental information. The app works fully offline using Room as its local database.

## Module Details

- **Module:** Mobile Application Development B (MADB372)
- **Project:** SF1 — Library Book Rental & Reservation System
- **Student:** Sakhile Basi
- **Package:** `com.stadio.kitabu`
- **Required repository name:** `Kitabu_MADB372_SF1`

## Core Features

- Two-column book catalogue using Jetpack Compose
- Availability badges for Available and Borrowed books
- Search by book title or author using SQL `LIKE`
- Availability filter: All books, Available books, Borrowed books
- Full-screen book detail page
- Reservation bottom sheet with 7, 14, and 21 day rental options
- Calculated return deadline before confirming a reservation
- Pending reservation state with Cancel and Pick Up actions
- Active rental state with Renew and Return actions
- 14-day renewal extension
- Reactive availability updates after reservation, cancellation, and return
- Profile page with live active-booking count from Room
- Three-tab navigation: Home, Library, Profile
- Local offline persistence with a pre-populated Room database

## Technology Stack

- Kotlin
- Jetpack Compose (Material 3)
- Room Database
- MVVM architecture
- Repository pattern
- Kotlin Coroutines
- Kotlin Flow / StateFlow
- Navigation Compose
- KSP for Room annotation processing
- Manual dependency injection using `AppContainer`

## Architecture

Kitabu follows MVVM with clear separation of responsibilities:

```text
UI Layer (Compose Screens / Components)
        ↓
ViewModels (StateFlow + viewModelScope)
        ↓
LibraryRepository (business logic / transactions)
        ↓
Room DAOs
        ↓
BookEntity + BookingEntity
```

`KitabuApp` creates the application-level dependency container. `DefaultAppContainer` builds the singleton Room database and the shared `LibraryRepository` instance used by the ViewModels.

## Main Package Structure

```text
com.stadio.kitabu/
├── KitabuApp.kt
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── KitabuDatabase.kt
│   │   ├── converter/Converters.kt
│   │   ├── dao/
│   │   │   ├── BookDao.kt
│   │   │   └── BookingDao.kt
│   │   ├── entity/
│   │   │   ├── BookEntity.kt
│   │   │   └── BookingEntity.kt
│   │   └── relation/BookingWithBook.kt
│   └── repository/LibraryRepository.kt
├── di/AppContainer.kt
├── domain/model/BookingStatus.kt
└── ui/
    ├── bookdetail/
    ├── catalog/
    ├── components/
    ├── dashboard/
    ├── navigation/
    ├── profile/
    └── theme/
```

## Database Design

### BookEntity

- `bookId` — auto-generated primary key
- `title`
- `author`
- `category`
- `isAvailable`

### BookingEntity

- `bookingId` — auto-generated primary key
- `bookOwnerId` — foreign key to `BookEntity.bookId`
- `userName`
- `bookingDate`
- `returnDeadline`
- `status` — `PENDING`, `ACTIVE`, or `RETURNED`

The foreign key uses cascade delete. `BookingStatus` is stored using a Room `TypeConverter`.

## Booking Lifecycle

```text
AVAILABLE
   │
   │ Reserve
   ▼
PENDING
   ├── Cancel ─────────────► booking deleted + book available
   │
   └── Pick Up
         ▼
       ACTIVE
         ├── Renew ─────────► deadline extended by 14 days
         │
         └── Return ────────► RETURNED + book available
```

Cross-table operations such as reserve, cancel, and return are wrapped in `RoomDatabase.withTransaction` so booking status and book availability remain consistent.

## CRUD Mapping

| CRUD | Kitabu implementation |
|---|---|
| Create | Insert books and insert a new `BookingEntity` when a reservation is confirmed |
| Read | Observe catalogue and bookings with Room `Flow`, filter by availability, SQL `LIKE` search, joined booking/book dashboard data |
| Update | Pick Up changes `PENDING` to `ACTIVE`, Renew extends the deadline, Return changes status to `RETURNED`, availability updates reactively |
| Delete | Cancel deletes only a `PENDING` booking and restores book availability |

## Reactive State

Room query methods expose `Flow`. ViewModels convert those streams into `StateFlow` using `stateIn`, and Compose screens observe them using `collectAsStateWithLifecycle()`.

Database writes are called from `viewModelScope.launch` using suspend DAO functions. No Room operation is intentionally executed synchronously on the main thread.

## Validation

- Search text is sanitised with `trim()` before querying Room.
- Reservations only accept approved rental durations: 7, 14, or 21 days.
- The calculated return deadline must be later than the booking date.
- Unavailable books cannot be reserved.
- Renew and Return DAO queries operate only on `ACTIVE` bookings.
- Cancellation deletes only `PENDING` bookings.

## Pre-populated Catalogue

The Room database is populated on first creation with 10 sample books, including titles from Software Engineering, Computer Science, Fiction, Self-Help, and History categories.

## Screenshots

### Catalogue

![Catalogue showing availability badges](screenshots/01_catalog_all_books.png)

### Borrowed filter

![Borrowed books filter](screenshots/02_catalog_borrowed_filter.png)

### Search

![Search result for data](screenshots/03_catalog_search.png)

### Active rental dashboard

![Active booking with Renew and Return](screenshots/04_dashboard_active_booking.png)

### Profile

![Profile showing live active booking count](screenshots/05_profile_active_count.png)

### Book detail

![Available book detail with Reserve action](screenshots/06_book_detail_available.png)

### Reservation duration sheet

![Reservation bottom sheet with 7, 14, and 21 day rental choices](screenshots/07_reservation_duration_sheet.png)

### Pending reservation

![Pending reservation with Pick Up and Cancel actions](screenshots/08_dashboard_pending_booking.png)

### Pending reservation does not count as active

![Profile showing zero active bookings while a reservation is still pending](screenshots/09_profile_pending_not_active.png)

## How to Run

1. Clone or download the repository.
2. Open `Kitabu_MADB372_SF1` in Android Studio.
3. Ensure the project uses JDK 17 or newer.
4. Allow Gradle to sync and download dependencies.
5. Run the `app` configuration on an Android emulator or device.
6. On first launch, Room creates the local database and inserts the sample catalogue.

## Project Scope

The project intentionally does not include Firebase, REST APIs, cloud databases, authentication, multi-user accounts, payment processing, or downloadable e-books. Data persistence and offline availability are provided locally by Room.

## Submission Notes

The final assessment submission should include the Android Studio project, this README, application screenshots, the required Word document, design/planning evidence, references, and AI-use evidence where applicable.
