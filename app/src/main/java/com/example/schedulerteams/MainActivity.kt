package com.example.schedulerteams

import GamesViewModel
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val HOME_TEAM_ID = "1610612748"
class MainActivity : ComponentActivity() {
    private val gamesViewModel: GamesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gamesViewModel.loadData()
        enableEdgeToEdge()
        setContent {
            SchedulerTeamsTheme {
                ScheduleScreen(gamesViewModel)
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
fun ScheduleScreen(viewModel: GamesViewModel) {
    val scheduleList by viewModel.scheduleList.observeAsState(emptyList())
    val teamsMap by viewModel.teamsMap.observeAsState(emptyMap())

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
                items(games.size) { game ->
                    GameRow(games[game], teamsMap , games[game]?.gameStatus === 1)
                }
            }
    }
}

@Composable
fun GameRow(game: Schedule, teamsMap: Map<String, Team>,showBuyTicketsButton: Boolean) {
    val homeTeam = teamsMap[game.homeTeam.teamId]
    val awayTeam = teamsMap[game.awayTeam.teamId]

    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("EEE MMM dd", Locale.US)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
    // Format gameTime
    val formattedDate = formatDate(game.gameTime)
    val gameStatusText = if (game.homeTeam.teamId.equals(HOME_TEAM_ID)) "Vs" else "@"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Dark background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with date and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "AWAY", // Adjust text as needed
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = formattedDate, // Example date
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "HOME",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Away Team Logo and Name
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = awayTeam?.teamLogoUrl,
                        contentDescription = "${awayTeam?.teamName} Logo",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = awayTeam?.teamAbbreviation ?: "UNK",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Scores and Separator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if(showBuyTicketsButton)  " " else "100", // Away team score
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Text(
                        text = gameStatusText,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if(showBuyTicketsButton)  " " else "150", // Home team score
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                // Home Team Logo and Name
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = homeTeam?.teamLogoUrl,
                        contentDescription = "${homeTeam?.teamName} Logo",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = homeTeam?.teamAbbreviation ?: "UNK",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }

            if (showBuyTicketsButton) {
                Button(
                    onClick = { /* Add navigation to Ticketmaster here */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "BUY TICKETS ON ",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ticketmaster",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}