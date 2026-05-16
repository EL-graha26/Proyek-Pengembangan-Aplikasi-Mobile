package com.example.pantaujompo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    // 5 Tab Utama di Bottom Navigation
    @Serializable data object Beranda : Route
    @Serializable data object Olahraga : Route
    @Serializable data object Pemindai : Route
    @Serializable data object History : Route
    @Serializable data object Profil : Route

    // Rute tambahan untuk CRUD (Ini yang tadi ketinggalan bray!)
    @Serializable data class AddEditActivity(val id: Long? = null) : Route
    @Serializable data class DetailRiwayat(val id: Long) : Route
    @Serializable data object GpsTracker
    @Serializable data object IndoorWorkout
    @Serializable data object MealResult
    @Serializable data object CameraScanner

}