package com.example.schedulerteams.models.ScheduleModels

@kotlinx.serialization.Serializable
data class TeamResponse( // Wraps the JSON root-level "data"
    val data: TeamsList
)