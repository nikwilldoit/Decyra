package com.example.phasmatic.ui.shared_chat

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.phasmatic.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

val InkBlack = Color(0xFF000000)
val InkDeep = Color(0xFF1E1B4B)
val HeroIndigoEnd = Color(0xFF312E81)
val OrchidPrimary = Color(0xFFD946EF)
val OrchidSecondary = Color(0xFFA855F7)
val OrchidGradient = Brush.linearGradient(listOf(OrchidPrimary, OrchidSecondary))
val OrchidLight = Color(0xFFFDF4FF)
val AssistantBubbleBg = Color(0xFFFDF4FF)
val SoftPinkGlow = Color(0xFFFFE4FF)
val PureWhite = Color(0xFFFFFFFF)
val SoftGray = Color(0xFFF1F5F9)
val HintGray = Color(0xFF94A3B8)

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
                colors = listOf(OrchidPrimary.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(
                    size.width * (0.85f + (0.05f * sin(wave * 2 * Math.PI.toFloat()))),
                    size.height * 0.1f
                ),
                radius = 1100f
            )
        )
    }
}

@Composable
fun NeuralPrismAura(isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "prism")

    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "outerRot"
    )

    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "innerRot"
    )

    val nodeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "nodes"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .rotate(outerRotation)
            .drawBehind {
                drawCircle(
                    brush = Brush.sweepGradient(listOf(OrchidPrimary, Color.Transparent, OrchidPrimary)),
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
                drawCircle(
                    brush = Brush.radialGradient(listOf(OrchidPrimary.copy(0.15f), Color.Transparent)),
                    radius = size.width / 1.3f
                )

                if (isSpeaking) {
                    val radius = size.width / 2.8f
                    val angle1 = Math.toRadians(nodeRotation.toDouble())
                    val angle2 = angle1 + Math.PI

                    drawCircle(
                        color = OrchidPrimary,
                        radius = 5f,
                        center = Offset(
                            center.x + (radius * cos(angle1)).toFloat(),
                            center.y + (radius * sin(angle1)).toFloat()
                        )
                    )
                    drawCircle(
                        color = OrchidSecondary,
                        radius = 5f,
                        center = Offset(
                            center.x + (radius * cos(angle2)).toFloat(),
                            center.y + (radius * sin(angle2)).toFloat()
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = null,
            tint = OrchidPrimary,
            modifier = Modifier
                .size(22.dp)
                .rotate(innerRotation - outerRotation)
        )
    }
}

@Composable
fun ThinkingShimmerText() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Analyzing",
            color = OrchidPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        )
        repeat(3) { index ->
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(400, delayMillis = index * 150), RepeatMode.Reverse),
                label = "dot$index"
            )
            Text(
                text = ".",
                color = OrchidPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.graphicsLayer { this.alpha = dotAlpha }
            )
        }
    }
}

@Composable
fun FastForwardBadge() {
    Row(
        modifier = Modifier
            .background(OrchidPrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.FastForward, null, tint = OrchidPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "FAST FORWARD",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OrchidPrimary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun TypewriterText(text: String, color: Color, onCharTyped: () -> Unit, onFinish: () -> Unit) {
    var displayedText by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        displayedText = ""
        text.forEach { char ->
            displayedText += char
            onCharTyped()
            delay(20)
        }
        onFinish()
    }
    Text(text = displayedText, color = color, fontSize = 15.sp, lineHeight = 22.sp)
}

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    profileBitmap: Bitmap?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
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
            .background(PureWhite)
            .border(1.dp, SoftPinkGlow, RoundedCornerShape(16.dp))
    ) {
        DropdownMenuItem(
            text = { Text("My Account") },
            leadingIcon = { Icon(Icons.Outlined.AccountCircle, null, tint = OrchidPrimary) },
            onClick = onAccountClick
        )
        DropdownMenuItem(
            text = { Text("Messages") },
            leadingIcon = { Icon(Icons.Outlined.ChatBubbleOutline, null, tint = OrchidPrimary) },
            onClick = onChatClick
        )
        DropdownMenuItem(
            text = { Text("Conferences") },
            leadingIcon = { Icon(Icons.Outlined.VideoCall, null, tint = OrchidPrimary) },
            onClick = onConferenceClick
        )
        DropdownMenuItem(
            text = { Text("Notes") },
            leadingIcon = { Icon(Icons.Outlined.Description, null, tint = OrchidPrimary) },
            onClick = onNotesClick
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = SoftPinkGlow
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Logout",
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Logout, null, tint = Color(0xFFEF4444))
            },
            onClick = onLogoutClick
        )
    }
}

@Composable
fun PremiumTopBar(
    title: String,
    subtitle: String,
    profileImageUrl: String?,
    profileBitmap: Bitmap?,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Surface(color = PureWhite, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, null, tint = InkDeep)
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Black, color = InkDeep)) {
                            append("$title ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light, color = OrchidPrimary)) {
                            append(subtitle)
                        }
                    },
                    fontSize = 22.sp
                )
                Surface(
                    color = OrchidPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        "AI",
                        color = OrchidPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            ProfileAvatar(
                imageUrl = profileImageUrl,
                profileBitmap = profileBitmap,
                onClick = onAvatarClick
            )
        }
    }
}

@Composable
fun ChatInputArea(
    inputText: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onSaveClick: () -> Unit,
    placeholder: String = "How can I help you?"
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PureWhite,
        shadowElevation = 15.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Outlined.Save, null, tint = HintGray)
            }
            IconButton(onClick = onVoiceClick) {
                Icon(Icons.Default.Mic, null, tint = OrchidPrimary)
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text(placeholder, color = HintGray) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = InkDeep,
                    unfocusedTextColor = InkDeep,
                    focusedContainerColor = SoftGray,
                    unfocusedContainerColor = SoftGray,
                    focusedBorderColor = OrchidPrimary,
                    unfocusedBorderColor = Color.Transparent
                ),
                enabled = !isSending
            )

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.94f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "sendScale"
            )

            FloatingActionButton(
                onClick = onSendClick,
                interactionSource = interactionSource,
                containerColor = OrchidPrimary,
                contentColor = PureWhite,
                shape = CircleShape,
                modifier = Modifier
                    .size(52.dp)
                    .scale(scale)
            ) {
                Icon(
                    if (isSending) Icons.Default.HourglassEmpty else Icons.Default.Send,
                    null
                )
            }
        }
    }
}