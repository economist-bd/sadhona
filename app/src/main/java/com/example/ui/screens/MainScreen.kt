package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AdviceEntity
import com.example.ui.components.AddAdviceDialog
import com.example.ui.components.AdviceCard
import com.example.ui.components.CommentsSheet
import com.example.ui.viewmodel.AdviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AdviceViewModel,
    modifier: Modifier = Modifier
) {
    val advices by viewModel.advices.collectAsStateWithLifecycle()
    val categories = viewModel.categories
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val activeAdviceId by viewModel.activeAdviceId.collectAsStateWithLifecycle()
    val comments by viewModel.currentComments.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    // Selected advice for comment sheet
    val activeAdvice = remember(activeAdviceId, advices) {
        advices.find { it.id == activeAdviceId }
    }

    // Filtered advices by search query
    val filteredAdvices = remember(searchQuery, advices) {
        if (searchQuery.isBlank()) {
            advices
        } else {
            advices.filter {
                it.text.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Brand Banner with warm gradient
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "শুভ সকাল/দিন",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "আজকের সাধনা",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "পড়াশোনায় সফলতার চাবিকাঠি",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "মনোযোগ ধরে রাখতে ও লক্ষ্য অর্জনে প্রতিদিনের জন্য সঠিক উপদেশ ও গাইডলাইন",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Search Bar & Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("উপদেশ খুঁজুন (যেমন: মনোযোগ, মোবাইল, রুটিন...)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "খুঁজুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("advice_search_bar"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )

                // Categories Chips Horizontal Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleCategory(category) },
                            label = { Text(text = category, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Advices Lazy List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredAdvices.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔍",
                            fontSize = 54.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো উপদেশ মিলছে না!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "অন্য কোনো শব্দ দিয়ে সার্চ করার চেষ্টা করো অথবা নতুন একটি উপদেশ নিজেই যোগ করে ফেলো।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = filteredAdvices,
                            key = { it.id }
                        ) { advice ->
                            AdviceCard(
                                advice = advice,
                                onLikeClick = { viewModel.toggleLike(advice) },
                                onThumbClick = { viewModel.toggleThumb(advice) },
                                onClapClick = { viewModel.toggleClap(advice) },
                                onMindblownClick = { viewModel.toggleMindblown(advice) },
                                onCommentClick = { viewModel.selectAdviceForComments(advice.id) },
                                onDeleteClick = if (advice.isCustom) {
                                    { viewModel.deleteAdvice(advice) }
                                } else null
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to add advice
        FloatingActionButton(
            onClick = { isAddDialogOpen = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_advice_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "নতুন উপদেশ যোগ করুন",
                modifier = Modifier.size(24.dp)
            )
        }

        // Add Advice Dialog
        if (isAddDialogOpen) {
            AddAdviceDialog(
                categories = categories,
                onAddAdvice = { text, category ->
                    viewModel.addCustomAdvice(text, category)
                    isAddDialogOpen = false
                },
                onDismiss = { isAddDialogOpen = false }
            )
        }

        // Comments Sheet Dialog
        if (activeAdvice != null) {
            CommentsSheet(
                adviceId = activeAdvice!!.id,
                adviceText = activeAdvice!!.text,
                comments = comments,
                onAddComment = { userName, text ->
                    viewModel.addComment(activeAdvice!!.id, userName, text)
                },
                onDeleteComment = { viewModel.deleteComment(it) },
                onDismiss = { viewModel.selectAdviceForComments(null) }
            )
        }
    }
}
