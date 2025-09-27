package com.project.yabawigame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.times

data class CupState(
    val hasBall: Boolean = false,
    var revealed: Boolean = false,
    var isOpen: Boolean = false,
    var showMiss: Boolean = false // 빗나감 표시
)

data class CupPosition(
    val x: Float = 0f,
    val y: Float = 0f
)

enum class GamePhase {
    SHOWING_BALL,    // 공 위치 보여주기
    SHUFFLING,       // 섞는 중
    PLAYING,         // 플레이 중
    GAME_OVER        // 게임 종료
}

@Composable
fun StartScreen(navController: NavController) {
    var playerCount by remember { mutableStateOf(2) }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(71.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .clickable {
                        navController.navigate("game/$playerCount")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("시작하기", color = Color.White, fontSize = 40.sp)
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
        }
    }
}

@Composable
fun GameScreen(playerCount: Int, navController: NavController) {
    val cupCount = 3
    var cups by remember { mutableStateOf(List(cupCount) { CupState() }) }
    var currentPlayer by remember { mutableStateOf(1) }
    var winner by remember { mutableStateOf("") }
    var gamePhase by remember { mutableStateOf(GamePhase.SHOWING_BALL) }
    val scope = rememberCoroutineScope()
    val spacing = 140.dp

    // 플레이어별 색상 정의
    val playerColors = listOf(
        Color(0xFF2196F3), // 파랑
        Color(0xFFE91E63), // 핑크
        Color(0xFF4CAF50), // 초록
        Color(0xFFFF9800), // 주황
        Color(0xFF9C27B0), // 보라
        Color(0xFFFF5722), // 빨강-주황
        Color(0xFF00BCD4), // 청록
        Color(0xFF8BC34A)  // 연두
    )

    fun getPlayerColor(player: Int): Color {
        return playerColors[(player - 1) % playerColors.size]
    }

    // 각 컵의 실제 위치를 추적
    var cupPositions by remember { mutableStateOf(listOf(
        CupPosition(0f, 0f),
        CupPosition(1f, 0f),
        CupPosition(2f, 0f)
    )) }

    // 애니메이션된 위치들
    val animatedPositions = cupPositions.mapIndexed { index, pos ->
        val animatedX by animateFloatAsState(
            targetValue = pos.x,
            animationSpec = tween(
                durationMillis = 250, // 섞는 시간 더 단축
                easing = FastOutSlowInEasing
            ),
            label = "cup_x_$index"
        )
        val animatedY by animateFloatAsState(
            targetValue = pos.y,
            animationSpec = tween(
                durationMillis = 250, // 섞는 시간 더 단축
                easing = FastOutSlowInEasing
            ),
            label = "cup_y_$index"
        )
        CupPosition(animatedX, animatedY)
    }

    fun resetCups(showBall: Boolean = false) {
        val ballIndex = Random.nextInt(cupCount)
        cups = List(cupCount) { i ->
            CupState(hasBall = i == ballIndex, revealed = showBall) // 처음에만 공 위치 보여주기
        }
        cupPositions = listOf(
            CupPosition(0f, 0f),
            CupPosition(1f, 0f),
            CupPosition(2f, 0f)
        )
        if (showBall) {
            gamePhase = GamePhase.SHOWING_BALL
        }
    }

    fun shuffleCups() {
        scope.launch {
            gamePhase = GamePhase.SHUFFLING

            // 컵 덮기
            cups = cups.map { it.copy(revealed = false, isOpen = false, showMiss = false) }
            delay(300) // 덮기 시간 단축

            // 여러 번의 섞기 동작 (횟수와 시간 더 단축)
            repeat(4) { shuffleRound ->
                // 두 개의 컵을 랜덤하게 선택해서 교체
                val cup1 = Random.nextInt(3)
                var cup2 = Random.nextInt(3)
                while (cup2 == cup1) {
                    cup2 = Random.nextInt(3)
                }

                val currentPositions = cupPositions.toMutableList()

                // 첫 번째 컵을 위로 올리기
                currentPositions[cup1] = currentPositions[cup1].copy(y = -0.5f)
                cupPositions = currentPositions
                delay(60) // 시간 더 단축

                // 두 번째 컵도 위로 올리기
                currentPositions[cup2] = currentPositions[cup2].copy(y = -0.5f)
                cupPositions = currentPositions
                delay(60) // 시간 더 단축

                // 위치 교체 (공중에서)
                val temp = currentPositions[cup1].x
                currentPositions[cup1] = currentPositions[cup1].copy(x = currentPositions[cup2].x)
                currentPositions[cup2] = currentPositions[cup2].copy(x = temp)
                cupPositions = currentPositions
                delay(80) // 시간 더 단축

                // 두 컵 모두 내리기
                currentPositions[cup1] = currentPositions[cup1].copy(y = 0f)
                currentPositions[cup2] = currentPositions[cup2].copy(y = 0f)
                cupPositions = currentPositions
                delay(120) // 시간 더 단축

                // 컵 상태도 함께 교체
                val tempCup = cups[cup1]
                val newCups = cups.toMutableList()
                newCups[cup1] = cups[cup2]
                newCups[cup2] = tempCup
                cups = newCups
            }

            gamePhase = GamePhase.PLAYING
        }
    }

    fun startInitialShuffle() {
        scope.launch {
            delay(1500) // 1.5초 동안 공 위치 보여주기 (시간 단축)
            shuffleCups()
        }
    }

    LaunchedEffect(Unit) {
        resetCups(showBall = true) // 처음 시작할 때만 공 위치 보여주기
        startInitialShuffle()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (winner.isNotEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ball),
                    contentDescription = "Ball",
                    modifier = Modifier.size(200.dp)
                )

                // 승리 메시지 색상 적용
                val winnerColor = if (playerCount == 1) Color.Blue else {
                    val playerNumber = winner.filter { it.isDigit() }.firstOrNull()?.toString()?.toIntOrNull() ?: currentPlayer
                    getPlayerColor(playerNumber)
                }

                Text(
                    text = winner,
                    fontSize = 36.sp,
                    color = winnerColor
                )
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .height(71.dp)
                        .width(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black)
                        .clickable { navController.navigate("start") { popUpTo("start") { inclusive = true } } },
                    contentAlignment = Alignment.Center
                ) {
                    Text("다시 시작", color = Color.White, fontSize = 28.sp)
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 게임 상태에 따른 메시지
                when (gamePhase) {
                    GamePhase.SHOWING_BALL -> Text("공의 위치를 확인하세요!", fontSize = 24.sp, color = Color.Blue)
                    GamePhase.SHUFFLING -> Text("컵을 섞는 중...", fontSize = 24.sp, color = Color.Gray)
                    GamePhase.PLAYING -> {
                        val currentPlayerColor = getPlayerColor(currentPlayer)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(currentPlayerColor, androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("플레이어 $currentPlayer 차례", fontSize = 28.sp, color = currentPlayerColor)
                        }
                    }
                    GamePhase.GAME_OVER -> Text("게임 종료", fontSize = 28.sp)
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .width(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    animatedPositions.forEachIndexed { index, pos ->
                        val cup = cups[index]
                        val offsetX = (pos.x - 1f) * spacing.value
                        val offsetY = pos.y * 80

                        Box(
                            modifier = Modifier
                                .offset(x = offsetX.dp, y = offsetY.dp)
                                .size(80.dp)
                                .clickable(enabled = gamePhase == GamePhase.PLAYING && !cup.revealed) {
                                    val newCups = cups.toMutableList()
                                    newCups[index] = cup.copy(revealed = true, isOpen = true)
                                    cups = newCups

                                    scope.launch {
                                        delay(800) // 결과 보여주는 시간 단축
                                        if (cup.hasBall) {
                                            val winnerColor = getPlayerColor(currentPlayer)
                                            winner = if (playerCount == 1) "공을 찾았습니다!"
                                            else "플레이어 $currentPlayer 승리!"
                                            gamePhase = GamePhase.GAME_OVER
                                        } else {
                                            // 못 찾았을 때 시각적 피드백
                                            newCups[index] = cup.copy(showMiss = true, isOpen = true, revealed = true)
                                            cups = newCups
                                            delay(1200) // 빗나감 표시 시간

                                            // 모든 컵 초기화
                                            cups = cups.map { it.copy(revealed = false, isOpen = false, showMiss = false) }
                                            delay(500)

                                            // 다음 라운드를 위해 리셋 (공 위치는 보여주지 않음)
                                            resetCups(showBall = false)

                                            // 바로 섞기 시작
                                            scope.launch {
                                                delay(300) // 대기 시간 단축
                                                shuffleCups()
                                            }

                                            if (playerCount > 1) {
                                                currentPlayer = (currentPlayer % playerCount) + 1
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val imageRes = when {
                                cup.hasBall && cup.revealed -> R.drawable.ball
                                cup.isOpen -> R.drawable.open_cup
                                cup.showMiss -> R.drawable.open_cup // 빗나감도 열린 컵으로 표시
                                else -> R.drawable.cup
                            }
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = "Cup",
                                modifier = Modifier.fillMaxSize()
                            )

                            // 빗나감 표시
                            if (cup.showMiss) {
                                Text(
                                    "X",
                                    fontSize = 40.sp,
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                // 못 찾았을 때 메시지
                if (cups.any { it.showMiss }) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                Color.Red.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            "못 찾았습니다! 😔",
                            fontSize = 24.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YabawiGameApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("game/{playerCount}") { backStackEntry ->
            val playerCount = backStackEntry.arguments?.getString("playerCount")?.toInt() ?: 2
            GameScreen(playerCount, navController)
        }
    }
}