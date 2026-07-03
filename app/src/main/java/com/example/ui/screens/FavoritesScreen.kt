package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdviceCard
import com.example.ui.components.CommentsSheet
import com.example.ui.viewmodel.AdviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: AdviceViewModel,
    modifier: Modifier = Modifier
) {
    val reactedAdvices by viewModel.reactedAdvices.collectAsStateWithLifecycle()
    val activeAdviceId by viewModel.activeAdviceId.collectAsStateWithLifecycle()
    val comments by viewModel.currentComments.collectAsStateWithLifecycle()

    val activeAdvice = remember(activeAdviceId, reactedAdvices) {
        reactedAdvices.find { it.id == activeAdviceId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        // Favorites Screen Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "পছন্দের লোগো",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = "আমার পছন্দের তালিকা",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "যে উপদেশগুলো তোমার পড়াশোনায় অনুপ্রেরণা যুগিয়েছে",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (reactedAdvices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "💖",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "তালিকাটি এখন ফাঁকা!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "উপদেশ পড়ার সময় ভালো লাগলে লাইক বা অন্যান্য রিঅ্যাকশন দিয়ে রাখো। সেগুলো এখানে স্বয়ংক্রিয়ভাবে জমা হবে, যাতে তুমি যেকোনো সময় রিভিশন করতে পারো।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = reactedAdvices,
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

        // Active Comments Dialog
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
