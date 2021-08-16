package com.example.incidenttracker

data class IncidentReport(val incidentType: String = "", val incidentDescription: String="",
val incidentLat: Double=0.0, val incidentLng: Double=0.0, val incidentLocation: String = "")