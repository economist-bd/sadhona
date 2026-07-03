package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AdviceEntity

@Composable
fun AdviceCard(
    advice: AdviceEntity,
    onLikeClick: () -> Unit,
    onThumbClick: () -> Unit,
    onClapClick: () -> Unit,
    onMindblownClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Category colors
    val (catBg, catText) = getCategoryColors(advice.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("advice_card_${advice.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Category & Author / Delete (for custom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(catBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = advice.category,
                        color = catText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "— ${advice.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    if (advice.isCustom && onDeleteClick != null) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_custom_advice_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "উপদেশ ডিলিট করুন",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Quote
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = "Quote",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Top)
                )

                Text(
                    text = advice.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: Reactions & Comments/Share
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reactions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Heart ❤️
                    ReactionPill(
                        emoji = "❤️",
                        count = advice.likesCount,
                        isActive = advice.liked,
                        onClick = onLikeClick,
                        modifier = Modifier.testTag("reaction_heart_${advice.id}")
                    )

                    // Thumbs Up 👍
                    ReactionPill(
                        emoji = "👍",
                        count = null,
                        isActive = advice.thumbed,
                        onClick = onThumbClick,
                        modifier = Modifier.testTag("reaction_thumb_${advice.id}")
                    )

                    // Clap 👏
                    ReactionPill(
                        emoji = "👏",
                        count = null,
                        isActive = advice.clapped,
                        onClick = onClapClick,
                        modifier = Modifier.testTag("reaction_clap_${advice.id}")
                    )

                    // Mind-blown 🤯
                    ReactionPill(
                        emoji = "🤯",
                        count = null,
                        isActive = advice.mindblown,
                        onClick = onMindblownClick,
                        modifier = Modifier.testTag("reaction_mindblown_${advice.id}")
                    )
                }

                // Comment & Share Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Comments button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onCommentClick)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "মন্তব্য",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "মন্তব্য করুন / দেখুন",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = { shareAdvice(context, advice.text, advice.category) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_advice_btn_${advice.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionPill(
    emoji: String,
    count: Int?,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        animationSpec = spring(),
        label = "pill_color"
    )

    val borderStroke = if (isActive) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
    } else {
        BorderStroke(1.dp, Color.Transparent)
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp)),
        color = containerColor,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 14.sp)
            if (count != null) {
                Text(
                    text = count.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun shareAdvice(context: Context, text: String, category: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "সাধনা - ছাত্র উপদেশ")
        putExtra(
            Intent.EXTRA_TEXT,
            """
            📖 সাধনা - পড়াশোনায় মনোযোগ বৃদ্ধির উপদেশ 📖
            
            বিভাগ: $category
            
            "$text"
            
            — সংগৃহীত (সাদনা অ্যাপ থেকে)
            """.trimIndent()
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "উপদেশটি বন্ধুদের সাথে শেয়ার করুন"))
}

@Composable
fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category) {
        "মনোযোগ ও ফোকাস" -> Pair(Color(0xFFE8EBE2), Color(0xFF2D3C30)) // Soft Sage & Dark Olive
        "সময় ও রুটিন" -> Pair(Color(0xFFE1ECE6), Color(0xFF1E3529)) // Muted Pine & Dark Green
        "পরীক্ষার প্রস্তুতি" -> Pair(Color(0xFFF3EFE0), Color(0xFF6B582F)) // Cream & Ochre
        "ডিজিটাল আসক্তি" -> Pair(Color(0xFFEFECE5), Color(0xFF5A584F)) // Warm Slate & Muted Charcoal
        "ব্যর্থতা ও অনুপ্রেরণা" -> Pair(Color(0xFFEDE9DE), Color(0xFF635643)) // Muted Clay & Dark Warm Bronze
        "স্বাস্থ্য ও মানসিকতা" -> Pair(Color(0xFFE1EBE2), Color(0xFF324B35)) // Light Moss & Dark Sage
        else -> Pair(Color(0xFFF4F5F1), Color(0xFF4F524A)) // Soft Linen & Charcoal-Grey
    }
}
