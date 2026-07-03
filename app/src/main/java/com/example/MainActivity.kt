package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CoachScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdviceViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: AdviceViewModel = viewModel()
        var selectedTab by remember { mutableIntStateOf(0) }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            NavigationBar(
              modifier = Modifier.testTag("bottom_nav_bar"),
              tonalElevation = 8.dp
            ) {
              NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("উপদেশমালা", fontSize = 11.sp) },
                icon = {
                  Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = "উপদেশমালা"
                  )
                },
                modifier = Modifier.testTag("nav_tab_advices")
              )

              NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("এআই কোচ", fontSize = 11.sp) },
                icon = {
                  Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "এআই কোচ"
                  )
                },
                modifier = Modifier.testTag("nav_tab_coach")
              )

              NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("পছন্দসমূহ", fontSize = 11.sp) },
                icon = {
                  Icon(
                    imageVector = if (selectedTab == 2) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "পছন্দসমূহ"
                  )
                },
                modifier = Modifier.testTag("nav_tab_favorites")
              )
            }
          }
        ) { innerPadding ->
          Surface(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (selectedTab) {
              0 -> MainScreen(viewModel = viewModel)
              1 -> CoachScreen(viewModel = viewModel)
              2 -> FavoritesScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}
