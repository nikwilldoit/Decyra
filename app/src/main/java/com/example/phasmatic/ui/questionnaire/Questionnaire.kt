package com.example.phasmatic.ui.questionnaire

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.phasmatic.R
import com.example.phasmatic.ui.Forum.forum.AnimatedShimmerTitle
import com.example.phasmatic.ui.modeSelection.OrchidPrimary
import kotlin.math.sin

// --- PREMIUM PRODUCTION COLOR PALETTE ---
val InkBlack = Color(0xFF000000)
val InkDeep = Color(0xFF1E1B4B)
val HeroIndigoEnd = Color(0xFF312E81)
val OrchidPrimary = Color(0xFFD946EF)
val OrchidLight = Color(0xFFFDF4FF)
val SoftPinkGlow = Color(0xFFFFE4FF)
val PureWhite = Color(0xFFFFFFFF)

// Keep your original color variables for logic compatibility
val QuizPrimary = OrchidPrimary
val QuizSecondary = Color(0xFF8B5CF6)
val QuizAccent = Color(0xFFEC4899)
val QuizBg = Color(0xFFF8FAFC)
val QuizCardBg = Color(0xFFFFFFFF)
val QuizCardSelected = OrchidLight
val QuizTextDark = Color(0xFF1E293B)
val QuizTextLight = Color(0xFF64748B)
val QuizBorder = Color(0xFFE2E8F0)

@Composable
fun QuestionnaireScreen(
    modeType: String,
    currentIndex: Int,
    questions: List<QuestionnaireComposeActivity.QuestionItem>,
    answers: List<String>,
    currentAnswerOptions: List<String>,
    itFieldNames: List<String>,
    selectedFieldId: Int?,
    selectedAnswerIndex: Int?,
    textAnswer: String,
    isLoading: Boolean,
    profileImageUrl: String?,
    profileBitmap: Bitmap?,
    menuExpanded: Boolean,
    onBackClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onStepClick: (Int) -> Unit,
    onItFieldSelected: (Int) -> Unit,
    onAnswerSelected: (Int) -> Unit,
    onTextAnswerChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onAccountClick: () -> Unit,
    onChatClick: () -> Unit,
    onConferenceClick: () -> Unit,
    onNotesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureWhite, // Updated to Premium White
        topBar = {
            QuestionnaireTopBar(
                modeType = modeType,
                profileImageUrl = profileImageUrl,
                profileBitmap = profileBitmap,
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                menuExpanded = menuExpanded,
                onMenuDismiss = onMenuDismiss,
                onAccountClick = onAccountClick,
                onChatClick = onChatClick,
                onConferenceClick = onConferenceClick,
                onNotesClick = onNotesClick,
                onLogoutClick = onLogoutClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. NEURAL MESH BACKGROUND (The "Billion Dollar" Layer)
            AnimatedMeshBackground()

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = QuizPrimary)
                }
            } else if (questions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No questions available", color = QuizTextLight)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Progress header
                    ProgressHeader(
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        onStepClick = onStepClick
                    )

                    // Question content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 2. THE RADIANT AURA CORE (Neural Guidance)
                        NeuralPrismAura()

                        Spacer(modifier = Modifier.height(16.dp))

                        QuestionCard(
                            questionText = questions[currentIndex].text,
                            questionNumber = currentIndex + 1,
                            totalQuestions = questions.size
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Answer area
                        when {
                            modeType == "career" && currentIndex == 0 -> {
                                // IT field selection
                                AnswerGrid(
                                    options = itFieldNames,
                                    selectedIndex = itFieldNames.indexOfFirst {
                                        itFieldNames.indexOf(it) == itFieldNames.indexOf(
                                            itFieldNames.find { name ->
                                                selectedFieldId?.let { id ->
                                                    itFieldNames.indexOf(name) < itFieldNames.size &&
                                                            itFieldNames.indexOf(name) >= 0
                                                } ?: false
                                            }
                                        )
                                    },
                                    onOptionSelected = { index ->
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onItFieldSelected(index)
                                    }
                                )
                            }
                            shouldUseTextInput(modeType, questions[currentIndex].questionId) -> {
                                // Text input
                                TextAnswerField(
                                    value = textAnswer,
                                    onValueChange = onTextAnswerChange,
                                    onVoiceClick = onVoiceClick
                                )
                            }
                            currentAnswerOptions.isNotEmpty() -> {
                                // Multiple choice
                                AnswerGrid(
                                    options = currentAnswerOptions,
                                    selectedIndex = selectedAnswerIndex,
                                    onOptionSelected = { index ->
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onAnswerSelected(index)
                                    }
                                )
                            }
                        }
                    }

                    // Navigation buttons
                    NavigationButtons(
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        onPrevClick = onPrevClick,
                        onNextClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNextClick()
                        }
                    )
                }
            }
        }
    }
}

// --- BILLION DOLLAR VISUAL COMPONENTS ---

// --- BILLION DOLLAR VISUAL COMPONENTS ---

@Composable
fun NeuralPrismAura() {
    val infiniteTransition = rememberInfiniteTransition(label = "prism")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)), label = "rot"
    )
    Box(
        modifier = Modifier.size(85.dp).rotate(rotation)
            .drawBehind {
                drawCircle(brush = Brush.sweepGradient(listOf(OrchidPrimary, Color.Transparent, OrchidPrimary)), style = Stroke(width = 6f, cap = StrokeCap.Round))
                drawCircle(brush = Brush.radialGradient(listOf(OrchidPrimary.copy(0.3f), Color.Transparent)), radius = size.width / 1.5f)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Layers, null, tint = OrchidPrimary, modifier = Modifier.size(30.dp))
    }
}


@Composable
fun AnimatedMeshBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    OrchidPrimary.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(
                    size.width * (0.8f + (0.1f * sin(wave * 6.28f))),
                    size.height * 0.15f
                ),
                radius = 1100f
            )
        )
    }
}

@Composable
fun QuestionnaireTopBar(
    modeType: String,
    profileImageUrl: String?,
    profileBitmap: Bitmap?,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    menuExpanded: Boolean,
    onMenuDismiss: () -> Unit,
    onAccountClick: () -> Unit,
    onChatClick: () -> Unit,
    onConferenceClick: () -> Unit,
    onNotesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(color = Color.White.copy(alpha = 0.9f), shadowElevation = 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, null, tint = QuizTextDark)
                }
                Spacer(Modifier.width(8.dp))
                AnimatedShimmerTitle(
                    text = when (modeType) {
                        "erasmus" -> "ERASMUS"
                        "master" -> "MASTER"
                        else -> "CAREER"
                    } + " QUEST"
                )
            }

            Box {
                ProfileAvatar(
                    imageUrl = profileImageUrl,
                    profileBitmap = profileBitmap,
                    onClick = onProfileClick
                )

                ProfileMenuDropdown(
                    expanded = menuExpanded,
                    onDismiss = onMenuDismiss,
                    onAccountClick = onAccountClick,
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
fun ProgressHeader(
    currentIndex: Int,
    totalQuestions: Int,
    onStepClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Step ${currentIndex + 1} of $totalQuestions",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = OrchidPrimary
            )
            Text(
                "${((currentIndex + 1) * 100 / totalQuestions)}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = QuizTextDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = OrchidPrimary,
            trackColor = QuizBorder
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            repeat(minOf(totalQuestions, 5)) { index ->
                StepDot(
                    isActive = index == currentIndex,
                    isCompleted = index < currentIndex,
                    onClick = { onStepClick(index) }
                )
            }
        }
    }
}

@Composable
fun StepDot(
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "stepScale"
    )

    val color = when {
        isActive -> OrchidPrimary
        isCompleted -> HeroIndigoEnd
        else -> QuizBorder
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    )
}

@Composable
fun QuestionCard(
    questionText: String,
    questionNumber: Int,
    totalQuestions: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OrchidLight),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(OrchidLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$questionNumber", color = OrchidPrimary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "QUESTION DATA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuizTextLight,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = questionText,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkDeep,
                lineHeight = 28.sp
            )
        }
    }
}

@Composable
fun AnswerGrid(
    options: List<String>,
    selectedIndex: Int?,
    onOptionSelected: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1), // Changed to 1 column for more premium readability
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp),
        userScrollEnabled = false
    ) {
        itemsIndexed(options) { index, option ->
            AnswerOptionCard(
                text = option,
                isSelected = index == selectedIndex,
                onClick = { onOptionSelected(index) }
            )
        }
    }
}

@Composable
fun AnswerOptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) OrchidPrimary else Color.White,
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp,
            color = if (isSelected) OrchidPrimary else QuizBorder
        ),
        shadowElevation = if (isSelected) 10.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else QuizBorder,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else QuizTextDark,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun TextAnswerField(
    value: String,
    onValueChange: (String) -> Unit,
    onVoiceClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OrchidLight),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Share your thoughts here...", color = QuizTextLight) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrchidPrimary,
                    unfocusedBorderColor = QuizBorder,
                    focusedTextColor = InkDeep,
                    unfocusedTextColor = InkDeep
                ),
                minLines = 4,
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVoiceClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = InkDeep),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = OrchidPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voice Input", color = Color.White)
            }
        }
    }
}

@Composable
fun NavigationButtons(
    currentIndex: Int,
    totalQuestions: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 15.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentIndex > 0) {
                IconButton(
                    onClick = onPrevClick,
                    modifier = Modifier
                        .size(56.dp)
                        .border(1.dp, QuizBorder, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = InkDeep)
                }
            }

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = OrchidPrimary),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkDeep
                )
            ) {
                Text(
                    text = if (currentIndex == totalQuestions - 1) "REVEAL MY PATH" else "CONTINUE",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    if (currentIndex == totalQuestions - 1) Icons.Default.AutoAwesome else Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = OrchidPrimary
                )
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    profileBitmap: Bitmap?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SoftPinkGlow)
            .border(2.dp, OrchidPrimary, CircleShape)
            .clickable { onClick() }
    ) {
        when {
            !imageUrl.isNullOrEmpty() -> {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.baseline_face_24)
                                .error(R.drawable.baseline_face_24)
                                .circleCrop()
                                .into(this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            profileBitmap != null -> {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageBitmap(profileBitmap)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = OrchidPrimary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileMenuDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAccountClick: () -> Unit,
    onChatClick: () -> Unit,
    onConferenceClick: () -> Unit,
    onNotesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(220.dp)
            .background(PureWhite, RoundedCornerShape(16.dp))
            .border(1.dp, SoftPinkGlow, RoundedCornerShape(16.dp))
    ) {
        DropdownMenuItem(
            text = { Text("My Account", fontWeight = FontWeight.Bold) },
            leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = OrchidPrimary) },
            onClick = onAccountClick
        )
        DropdownMenuItem(
            text = { Text("Messages") },
            leadingIcon = { Icon(Icons.Default.ChatBubble, null, tint = OrchidPrimary) },
            onClick = onChatClick
        )
        DropdownMenuItem(
            text = { Text("Conferences") },
            leadingIcon = { Icon(Icons.Default.VideoCall, null, tint = OrchidPrimary) },
            onClick = onConferenceClick
        )
        DropdownMenuItem(
            text = { Text("Notes") },
            leadingIcon = { Icon(Icons.Default.Description, null, tint = OrchidPrimary) },
            onClick = onNotesClick
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SoftPinkGlow)
        DropdownMenuItem(
            text = { Text("Logout", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
            leadingIcon = { Icon(Icons.Default.Logout, null, tint = Color(0xFFEF4444)) },
            onClick = onLogoutClick
        )
    }
}

private fun shouldUseTextInput(modeType: String, questionId: Long): Boolean {
    return (modeType == "career" && questionId == 4L) ||
            (modeType == "master" && questionId == 6L)
}