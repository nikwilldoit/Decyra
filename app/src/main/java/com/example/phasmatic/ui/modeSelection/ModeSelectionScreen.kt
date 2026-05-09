package com.example.phasmatic.ui.modeSelection

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlin.math.sin
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.phasmatic.R

// --- PREMIUM COLOR PALETTE ---
val InkBlack = Color(0xFF000000)
val InkDeep = Color(0xFF1E1B4B)
val HeroIndigoEnd = Color(0xFF312E81)
val OrchidPrimary = Color(0xFFD946EF)
val OrchidLight = Color(0xFFFDF4FF)
val SoftPinkGlow = Color(0xFFFFE4FF)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun ModeSelectionScreen(
    userId: String?,
    userFullName: String?,
    userEmail: String?,
    userPhone: String?,
    profileImageUrl: String?,
    profileBitmap: Bitmap?,
    onModeSelected: (String) -> Unit,
    onForumClick: () -> Unit,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onConferenceClick: () -> Unit,
    onNotesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureWhite
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedMeshBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
            ) {
                // --- TOP BAR ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DECYRA",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                letterSpacing = 8.sp,
                                brush = Brush.linearGradient(
                                    colors = listOf(InkDeep, OrchidPrimary)
                                )
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Box {
                            ProfileAvatar(
                                imageUrl = profileImageUrl,
                                profileBitmap = profileBitmap,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    menuExpanded = true
                                }
                            )

                            ProfileMenuDropdown(
                                expanded = menuExpanded,
                                onDismiss = { menuExpanded = false },
                                onChatClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onChatClick()
                                },
                                onConferenceClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onConferenceClick()
                                },
                                onNotesClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNotesClick()
                                },
                                onLogoutClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLogoutClick()
                                },
                                onAccountClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onProfileClick()
                                }
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }

                item {
                    HeroGlassCard(name = userFullName?.split(" ")?.firstOrNull() ?: "User")
                }

                item { Spacer(Modifier.height(48.dp)) }

                // --- MISSIONS SECTION ---
                item {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        AnimatedShimmerTitle(text = "CHOOSE YOUR MISSION")

                        Spacer(Modifier.height(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            ModeCard(
                                title = "Erasmus+",
                                subtitle = "Global Academic Mobility",
                                icon = Icons.Default.Public
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onModeSelected("erasmus")
                            }
                            ModeCard(
                                title = "Master's Degree",
                                subtitle = "Higher Education Research",
                                icon = Icons.Default.School
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onModeSelected("master")
                            }
                            ModeCard(
                                title = "Career Path",
                                subtitle = "Professional Placement",
                                icon = Icons.Default.AutoGraph
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onModeSelected("career")
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(40.dp)) }

                item {
                    ExtremeForumButtonUnified(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onForumClick()
                    })
                }
            }
        }
    }
}

// --- Υπόλοιπα Composables με ενσωματωμένο feedback ---

@Composable
fun HeroGlassCard(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(InkDeep, HeroIndigoEnd),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 160.dp, y = (-40).dp)
                .background(OrchidPrimary.copy(alpha = 0.25f), CircleShape)
                .blur(45.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuralPrismAura()
            Spacer(Modifier.width(24.dp))
            Column {
                Text("Welcome back,", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(0.6f))
                Text(name, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, color = Color.White))
            }
        }
    }
}

@Composable
fun ModeCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow), label = "scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth().height(90.dp).scale(scale),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().align(Alignment.CenterEnd)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(OrchidPrimary.copy(alpha = 0.04f), Color.Transparent),
                        center = Offset(size.width, size.height / 2),
                        radius = size.width * 0.6f
                    )
                )
            }

            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(OrchidLight), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = OrchidPrimary, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = InkDeep)
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.ArrowForward, null, tint = Color.LightGray.copy(0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ExtremeForumButtonUnified(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(if (isPressed) 1200 else 4000, easing = LinearEasing)), label = "rot"
    )

    Box(
        modifier = Modifier.fillMaxWidth().height(72.dp).scale(scale)
            .shadow(if (isPressed) 10.dp else 25.dp, RoundedCornerShape(28.dp), spotColor = OrchidPrimary)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(colors = listOf(InkDeep, HeroIndigoEnd)))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().rotate(rotation)) {
            drawRoundRect(
                brush = Brush.sweepGradient(0.0f to OrchidPrimary, 0.5f to Color.Transparent, 1.0f to OrchidPrimary, center = center),
                style = Stroke(width = 3.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx())
            )
        }
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Diversity3, null, tint = OrchidPrimary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text("ACCESS COMMUNITY", fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White)
        }
    }
}

@Composable
fun AnimatedShimmerTitle(text: String) {
    val shimmerColors = listOf(InkBlack, InkBlack, OrchidPrimary, InkBlack, InkBlack)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "s"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translateAnim - 500f, translateAnim - 500f),
                end = Offset(translateAnim, translateAnim)
            )
        )
    )
}

@Composable
fun AnimatedMeshBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse), label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(OrchidPrimary.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(size.width * (0.85f + (0.05f * sin(wave * 2 * Math.PI.toFloat()))), size.height * 0.1f),
                radius = 1100f
            )
        )
    }
}

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
                Image(
                    bitmap = profileBitmap.asImageBitmap(),
                    contentDescription = null,
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
    onLogoutClick: () -> Unit,
    onAccountClickAction: () -> Unit = onAccountClick
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(220.dp).background(PureWhite).border(1.dp, SoftPinkGlow, RoundedCornerShape(16.dp))
    ) {
        DropdownMenuItem(text = { Text("My Account") }, leadingIcon = { Icon(Icons.Outlined.AccountCircle, null, tint = OrchidPrimary) }, onClick = onAccountClickAction)
        DropdownMenuItem(text = { Text("Messages") }, leadingIcon = { Icon(Icons.Outlined.ChatBubbleOutline, null, tint = OrchidPrimary) }, onClick = onChatClick)
        DropdownMenuItem(text = { Text("Conferences") }, leadingIcon = { Icon(Icons.Outlined.VideoCall, null, tint = OrchidPrimary) }, onClick = onConferenceClick)
        DropdownMenuItem(text = { Text("Notes") }, leadingIcon = { Icon(Icons.Outlined.Description, null, tint = OrchidPrimary) }, onClick = onNotesClick)
        Divider(modifier = Modifier.padding(vertical = 4.dp), color = SoftPinkGlow)
        DropdownMenuItem(text = { Text("Logout", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }, leadingIcon = { Icon(Icons.Default.Logout, null, tint = Color(0xFFEF4444)) }, onClick = onLogoutClick)
    }
}