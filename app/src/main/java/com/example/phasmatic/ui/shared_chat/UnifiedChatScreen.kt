package com.example.phasmatic.ui.shared_chat

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun UnifiedChatScreen(
    title: String,
    subtitle: String,
    userFullName: String?,
    profileImageUrl: String?,
    profileBitmap: Bitmap?,
    inputText: String,
    messages: List<String>,
    isSending: Boolean,
    placeholder: String = "How can I help you?",
    onInputChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onSaveClick: () -> Unit,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onConferenceClick: () -> Unit,
    onNotesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val animatedFinishedMessages = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        if (inputText.isNotBlank() && messages.isEmpty()) {
            onSendClick()
        }
    }

    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty() || isSending) {
            listState.animateScrollToItem(if (isSending) messages.size else messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureWhite,
        topBar = {
            PremiumTopBar(
                title = title,
                subtitle = subtitle,
                profileImageUrl = profileImageUrl,
                profileBitmap = profileBitmap,
                onBackClick = onBackClick,
                onAvatarClick = { menuExpanded = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedMeshBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
                ) {
                    itemsIndexed(
                        items = messages,
                        key = { index, message -> "$index-${message.hashCode()}" }
                    ) { index, message ->
                        val isAssistant = message.startsWith("Assistant:")
                        val isError = message.startsWith("Error:")
                        val cleanText = when {
                            isAssistant -> message.substringAfter("Assistant:\n")
                            isError -> message.substringAfter("Error: ")
                            else -> message.substringAfter("You: ")
                        }

                        ChatBubble(
                            message = cleanText,
                            isUser = !isAssistant && !isError,
                            isError = isError,
                            isLatestAssistant = index == messages.size - 1 && isAssistant,
                            alreadyAnimated = animatedFinishedMessages.contains(cleanText),
                            onAnimationFinished = {
                                if (!animatedFinishedMessages.contains(cleanText)) {
                                    animatedFinishedMessages.add(cleanText)
                                }
                            },
                            onCharTyped = {
                                scope.launch {
                                    listState.scrollToItem(messages.size - 1)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (isSending) {
                        item(key = "loading-indicator") {
                            ChatBubble(
                                message = "",
                                isUser = false,
                                isError = false,
                                isLatestAssistant = true,
                                alreadyAnimated = false,
                                onAnimationFinished = {},
                                onCharTyped = {}
                            )
                        }
                    }
                }

                ChatInputArea(
                    inputText = inputText,
                    isSending = isSending,
                    onInputChange = onInputChange,
                    onSendClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSendClick()
                    },
                    onVoiceClick = onVoiceClick,
                    onSaveClick = onSaveClick,
                    placeholder = placeholder
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 8.dp)
            ) {
                ProfileMenuDropdown(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onAccountClick = onProfileClick,

                    onChatClick = onChatClick,
                    onConferenceClick = onConferenceClick,
                    onNotesClick = onNotesClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    isError: Boolean,
    isLatestAssistant: Boolean,
    alreadyAnimated: Boolean,
    onAnimationFinished: () -> Unit,
    onCharTyped: () -> Unit
) {
    var skipAnimation by remember(message, alreadyAnimated) {
        mutableStateOf(alreadyAnimated || !isLatestAssistant)
    }

    val haptic = LocalHapticFeedback.current

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val textColor = when {
        isUser -> Color.White
        isError -> Color(0xFFB91C1C)
        else -> InkDeep
    }

    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    val bubbleBrush = when {
        isUser -> Brush.linearGradient(listOf(InkDeep, HeroIndigoEnd))
        isError -> Brush.linearGradient(listOf(Color(0xFFFFE4E6), Color(0xFFFFF1F2)))
        else -> Brush.linearGradient(listOf(AssistantBubbleBg, Color.White))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser && !isError) {
                NeuralPrismAura(isSpeaking = isLatestAssistant && !skipAnimation)
                Spacer(Modifier.width(10.dp))
            }

            Surface(
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .clip(shape)
                    .clickable {
                        if (isLatestAssistant && !skipAnimation && message.isNotEmpty()) {
                            skipAnimation = true
                            onAnimationFinished()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                color = Color.Transparent,
                border = if (!isUser && !isError) BorderStroke(1.dp, Color(0xFFF1E8F7)) else null,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(bubbleBrush)
                        .padding(14.dp)
                ) {
                    Column {
                        if (isLatestAssistant && !skipAnimation && !isError) {
                            if (message.isEmpty()) {
                                ThinkingShimmerText()
                            } else {
                                TypewriterText(
                                    text = message,
                                    color = textColor,
                                    onCharTyped = onCharTyped,
                                    onFinish = {
                                        skipAnimation = true
                                        onAnimationFinished()
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                )
                                Spacer(Modifier.height(10.dp))
                                FastForwardBadge()
                            }
                        } else {
                            Text(
                                text = message,
                                color = textColor,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}