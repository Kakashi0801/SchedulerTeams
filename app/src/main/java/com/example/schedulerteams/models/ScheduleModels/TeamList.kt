package com.example.schedulerteams.models.ScheduleModels

import kotlinx.serialization.Serializable

@Serializable
data class TeamsList(
    val teams: List<Team>
)

