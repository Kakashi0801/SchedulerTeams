package com.example.schedulerteams.models.ScheduleModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    @SerialName("gid") val gameId: String,
    @SerialName("gametime") val gameTime: String,
    @SerialName("arena_name") val arenaName: String,
    @SerialName("st") val gameStatus: Int,  // 1 = Future, 2 = Live, 3 = Past
    @SerialName("h") val homeTeam: TeamDetails,
    @SerialName("v") val awayTeam: TeamDetails
)
