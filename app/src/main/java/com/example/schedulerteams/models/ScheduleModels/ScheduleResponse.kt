package com.example.schedulerteams.models.ScheduleModels

@kotlinx.serialization.Serializable
data class ScheduleResponse( // Wraps the JSON root-level "data"
    val data: ScheduleList
)