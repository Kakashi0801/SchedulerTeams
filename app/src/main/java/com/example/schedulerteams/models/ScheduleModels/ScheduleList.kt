package com.example.schedulerteams.models.ScheduleModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleList(
    @SerialName("schedules") val gameSchedules: List<Schedule>
)
