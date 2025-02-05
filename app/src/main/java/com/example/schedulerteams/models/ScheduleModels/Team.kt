package com.example.schedulerteams.models.ScheduleModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    @SerialName("tid") val teamId: String,
    @SerialName("tn") val teamName: String,
    @SerialName("ta") val teamAbbreviation: String,
    @SerialName("tc") val teamCity: String,
    @SerialName("logo") val teamLogoUrl: String,
    @SerialName("color") val primaryColor: String
)