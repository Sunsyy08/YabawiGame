package com.project.yabawigame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.yabawigame.ui.theme.YabawiGameTheme

@Composable
fun StartScreen() {
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
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "시작하기",
                    color = Color.White,
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
            Text(
                text = "참가 인원",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(85.dp, 82.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "2",
                    fontSize = 64.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .size(85.dp, 82.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "컵 개수",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(85.dp, 82.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "6",
                    fontSize = 64.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .size(85.dp, 82.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview1() {
    YabawiGameTheme {
        StartScreen()
    }
}