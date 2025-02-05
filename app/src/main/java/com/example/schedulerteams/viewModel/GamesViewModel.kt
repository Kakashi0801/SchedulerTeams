import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.schedulerteams.models.ScheduleModels.Schedule
import com.example.schedulerteams.models.ScheduleModels.ScheduleResponse
import com.example.schedulerteams.models.ScheduleModels.Team
import com.example.schedulerteams.models.ScheduleModels.TeamResponse
import kotlinx.serialization.json.Json
import java.io.IOException

class GamesViewModel(application: Application) : AndroidViewModel(application) {

    val scheduleList = MutableLiveData<List<Schedule>>()
    val teamsMap = MutableLiveData<Map<String, Team>>()
    val homeTeamId = "1610612748"


    private suspend fun loadJsonFromAssets(filename: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = getApplication<Application>().assets.open(filename)
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
    }

    fun loadData() {
        viewModelScope.launch {
            val jsonStringTeams = loadJsonFromAssets("teams.json")
            val jsonConfigTeams = Json { ignoreUnknownKeys = true }
            val teamResponse: TeamResponse = jsonConfigTeams.decodeFromString(jsonStringTeams)
            val teamsMapData: Map<String, Team> = teamResponse.data.teams.associateBy { it.teamId }
            teamsMap.postValue(teamsMapData)


            val jsonStringSchedule = loadJsonFromAssets("schedule.json")
            val jsonConfigSchedule = Json { ignoreUnknownKeys = true }
            val scheduleResponse: ScheduleResponse = jsonConfigSchedule.decodeFromString(jsonStringSchedule)
            val scheduleListData: List<Schedule> = scheduleResponse.data.gameSchedules
            scheduleList.postValue(scheduleListData)
        }
    }
}
