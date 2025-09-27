package com.project.yabawigame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.project.yabawigame.ui.theme.YabawiGameTheme
import kotlin.random.Random

data class CupState(
    val hasBall: Boolean = false,
    var revealed: Boolean = false,
    var removed: Boolean = false
)

@Composable
fun StartScreen(navController: NavController) {
    var playerCount by remember { mutableStateOf(2) }
    var cupCount by remember { mutableStateOf(6) }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(71.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(androidx.compose.ui.graphics.Color.Black)
                    .clickable {
                        navController.navigate("game/$playerCount/$cupCount")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "시작하기",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 40.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 참가 인원
            Text("참가 인원", fontSize = 40.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (playerCount > 1) playerCount-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(100.dp))
                }
                Text("$playerCount", fontSize = 64.sp)
                IconButton(onClick = { playerCount++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(100.dp))
                }
            }

            Spacer(Modifier.height(30.dp))

            // 컵 개수
            Text("컵 개수", fontSize = 40.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (cupCount > 1) cupCount-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(100.dp))
                }
                Text("$cupCount", fontSize = 64.sp)
                IconButton(onClick = { cupCount++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}

@Composable
fun GameScreen(playerCount: Int, cupCount: Int, navController: NavController) {
    val ballIndex = remember { Random.nextInt(cupCount) }
    var cups by remember { mutableStateOf(List(cupCount) { i -> CupState(hasBall = i == ballIndex) }) }
    var currentPlayer by remember { mutableStateOf(1) }
    var winner by remember { mutableStateOf("") }

    // 플레이어 색 정의
    val playerColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan)

    // 피라미드 형태 계산
    fun getPyramidRows(cups: List<CupState>): List<List<Pair<Int, CupState>>> {
        val rows = mutableListOf<List<Pair<Int, CupState>>>()
        var index = 0
        var rowSize = 1
        while (index < cups.size) {
            val row = cups.subList(index, kotlin.math.min(index + rowSize, cups.size))
                .mapIndexed { i, cup -> Pair(i, cup) }
            rows.add(row)
            index += rowSize
            rowSize++
        }
        return rows
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (playerCount == 1) "혼자서 도전!" else "플레이어 $currentPlayer 차례",
            fontSize = 30.sp,
            color = if (playerCount > 1) playerColors[(currentPlayer - 1) % playerColors.size] else Color.Black
        )
        Spacer(Modifier.height(30.dp))

        val rows = getPyramidRows(cups)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { (i, cup) ->
                        if (!cup.removed) {
                            Image(
                                painter = if (cup.revealed) painterResource(R.drawable.ball)
                                else painterResource(R.drawable.cup),
                                contentDescription = "Cup",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable(enabled = winner.isEmpty()) {
                                        if (cup.hasBall) {
                                            cups = cups.toMutableList().also {
                                                it[cups.indexOf(cup)] =
                                                    it[cups.indexOf(cup)].copy(revealed = true)
                                            }
                                            winner = if (playerCount == 1) "공을 찾았습니다!"
                                            else "플레이어 $currentPlayer 승리!"
                                        } else {
                                            cups = cups.toMutableList().also {
                                                it[cups.indexOf(cup)] =
                                                    it[cups.indexOf(cup)].copy(removed = true)
                                            }
                                            if (playerCount > 1) currentPlayer = (currentPlayer % playerCount) + 1
                                        }
                                    }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(30.dp))
        if (winner.isNotEmpty()) {
            Text(winner, fontSize = 36.sp, color = Color.Red)
            Spacer(Modifier.height(20.dp))
            // 다시 시작 버튼 (StartScreen과 동일 디자인)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(71.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .clickable { navController.navigate("start") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "다시 시작",
                    color = Color.White,
                    fontSize = 40.sp
                )
            }
        }
    }
}


@Composable
fun YabawiGameApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("game/{playerCount}/{cupCount}") { backStackEntry ->
            val playerCount = backStackEntry.arguments?.getString("playerCount")?.toInt() ?: 2
            val cupCount = backStackEntry.arguments?.getString("cupCount")?.toInt() ?: 6
            GameScreen(playerCount, cupCount, navController)
        }
    }
}