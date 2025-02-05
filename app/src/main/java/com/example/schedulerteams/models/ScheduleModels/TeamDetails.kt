package com.example.schedulerteams.models.ScheduleModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDetails(
    @SerialName("tid") val teamId: String,
    @SerialName("tn") val teamName: String,
    @SerialName("ta") val teamAbbreviation: String,
    @SerialName("tc") val teamCity: String
)
