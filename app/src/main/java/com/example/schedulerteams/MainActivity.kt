package com.example.schedulerteams

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.schedulerteams.models.ScheduleModels.Schedule
import com.example.schedulerteams.models.ScheduleModels.ScheduleResponse
import com.example.schedulerteams.models.ScheduleModels.Team
import com.example.schedulerteams.models.ScheduleModels.TeamResponse
import com.example.schedulerteams.ui.theme.SchedulerTeamsTheme
import kotlinx.serialization.json.Json
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonString = loadJsonFromAssets(this, "teams.json")
        val jsonConfig = Json { ignoreUnknownKeys = true }
        val teamResponse: TeamResponse = jsonConfig.decodeFromString(jsonString)
        val teamsMap: Map<String, Team> = teamResponse.data.teams.associateBy { it.teamId }


        val jsonStringSchedule = loadJsonFromAssets(this, "schedule.json")
        val jsonConfigSchedule = Json { ignoreUnknownKeys = true }
        val scheduleResponse: ScheduleResponse = jsonConfigSchedule.decodeFromString(jsonStringSchedule)

        val scheduleList: List<Schedule> = scheduleResponse.data.gameSchedules
        enableEdgeToEdge()
        setContent {
            SchedulerTeamsTheme {
                ScheduleScreen(scheduleList,teamsMap)
            }
        }
    }
}


fun loadJsonFromAssets(context: Context, filename: String): String {
    return try {
        val inputStream = context.assets.open(filename)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(scheduleList: List<Schedule>, teamsMap: Map<String, Team>) {
    LazyColumn {
        scheduleList.groupBy { it.gameTime.substring(0, 7) }
            .forEach { (month, games) ->
                stickyHeader {
                    Text(
                        text = month,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center // Centers the text
                    )
                }
                items(games) { game ->
                    GameRow(game, teamsMap)
                }
            }
    }
}

//@Composable
//fun GameRow(game: Schedule, teamsMap: Map<String, Team>) {
//    val homeTeam = teamsMap[game.homeTeam.teamId]
//    val awayTeam = teamsMap[game.awayTeam.teamId]
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .background(Color(android.graphics.Color.parseColor("#${homeTeam?.primaryColor ?: "FFFFFF"}")))
//    ) {
//        Text(text = awayTeam?.teamName ?: "", modifier = Modifier.weight(1f))
//        Text(text = "vs", fontWeight = FontWeight.Bold)
//        Text(text = homeTeam?.teamName ?: "", modifier = Modifier.weight(1f))
//    }
//}

@Composable
fun GameRow(game: Schedule, teamsMap: Map<String, Team>) {
    val homeTeam = teamsMap[game.homeTeam.teamId]
    val awayTeam = teamsMap[game.awayTeam.teamId]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Visitor Team Logo
        AsyncImage(
            model = awayTeam?.teamLogoUrl,
            contentDescription = "${awayTeam?.teamName} Logo",
            modifier = Modifier.size(50.dp)
        )

        // Game Details
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(text = awayTeam?.teamName ?: "Unknown")
            Text(text = "vs")
            Text(text = homeTeam?.teamName ?: "Unknown")
        }

        // Home Team Logo
        AsyncImage(
            model = homeTeam?.teamLogoUrl,
            contentDescription = "${homeTeam?.teamName} Logo",
            modifier = Modifier.size(50.dp)
        )
    }
}
