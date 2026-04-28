package com.example.phasmatic.ui.register

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.example.phasmatic.ui.modeSelection.OrchidPrimary
import kotlin.math.sin

// Ίδια palette όπως στο ModeSelectionScreen
val InkBlack = Color(0xFF000000)
val InkDeep = Color(0xFF1E1B4B)
val HeroIndigoEnd = Color(0xFF312E81)
val OrchidPrimary = Color(0xFFD946EF)
val OrchidLight = Color(0xFFFDF4FF)
val SoftPinkGlow = Color(0xFFFFE4FF)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun RegisterScreen(
    fullName: String,
    email: String,
    password: String,
    phone: String,
    isLoading: Boolean,
    showCameraPreview: Boolean,
    currentAction: FaceAction,
    actionProgress: Int,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onCaptureFaceClick: () -> Unit,
    cameraPreview: @Composable () -> Unit,
    onBackFromCamera: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        AnimatedMeshBackground()

        if (showCameraPreview) {
            FaceCaptureFlow(
                cameraPreview = cameraPreview,
                currentAction = currentAction,
                actionProgress = actionProgress,
                onBackClick = onBackFromCamera
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBrand()
                Spacer(Modifier.height(24.dp))
                RegisterHeroCard()
                Spacer(Modifier.height(24.dp))
                RegisterFormCard(
                    fullName = fullName,
                    email = email,
                    password = password,
                    phone = phone,
                    passwordVisible = passwordVisible,
                    isLoading = isLoading,
                    onFullNameChange = onFullNameChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onPhoneChange = onPhoneChange,
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                    onCaptureFaceClick = onCaptureFaceClick,
                    onRegisterClick = onRegisterClick
                )
                Spacer(Modifier.height(16.dp))
                LoginRedirectRow(onLoginClick = onLoginClick)
            }
        }
    }
}

@Composable
fun TopBrand() {
    Text(
        text = "DECYRA",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            letterSpacing = 8.sp,
            brush = Brush.linearGradient(
                colors = listOf(InkDeep, OrchidPrimary)
            )
        )
    )
}

@Composable
fun RegisterHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
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
                .size(130.dp)
                .offset(x = 170.dp, y = (-30).dp)
                .background(OrchidPrimary.copy(alpha = 0.25f), CircleShape)
                .blur(45.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuralPrismAura()
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    "Join DECYRA",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Create your secure academic profile.",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun RegisterFormCard(
    fullName: String,
    email: String,
    password: String,
    phone: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onCaptureFaceClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .border(1.dp, Color(0xFFF1E8F7), RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        AnimatedShimmerTitle("CREATE ACCOUNT")
        Spacer(Modifier.height(16.dp))

        PremiumTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = "Full name",
            leadingIcon = Icons.Outlined.Person,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(12.dp))

        PremiumTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email address",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(12.dp))

        PremiumTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            leadingIcon = Icons.Outlined.Lock,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = OrchidPrimary
                    )
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        PremiumTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone (optional)",
            leadingIcon = Icons.Outlined.Phone,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            )
        )

        Spacer(Modifier.height(18.dp))

        PremiumSecondaryButton(
            text = "CAPTURE FACE",
            icon = Icons.Default.Face,
            enabled = !isLoading,
            onClick = onCaptureFaceClick
        )

        Spacer(Modifier.height(10.dp))

        PremiumPrimaryButton(
            text = if (isLoading) "CREATING ACCOUNT..." else "REGISTER",
            onClick = onRegisterClick,
            enabled = !isLoading
        )
    }
}

@Composable
fun FaceCaptureFlow(
    cameraPreview: @Composable () -> Unit,
    currentAction: FaceAction,
    actionProgress: Int,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        cameraPreview()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        AnimatedFaceGuide(
            action = currentAction,
            progress = actionProgress,
            modifier = Modifier.align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                getInstructionForAction(currentAction),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Progress: $actionProgress / 3",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun AnimatedFaceGuide(action: FaceAction, progress: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "guide")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = OrchidPrimary.copy(alpha = 0.3f),
                radius = size.width / 2 * pulse,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Icon(
            imageVector = getIconForAction(action),
            contentDescription = null,
            tint = OrchidPrimary,
            modifier = Modifier.size(60.dp)
        )
    }
}

fun getInstructionForAction(action: FaceAction): String = when (action) {
    FaceAction.CENTER -> "Look straight at the camera"
    FaceAction.LOOK_LEFT -> "Turn your head left"
    FaceAction.LOOK_RIGHT -> "Turn your head right"
    FaceAction.LOOK_UP -> "Look up"
    FaceAction.LOOK_DOWN -> "Look down"
    FaceAction.BLINK -> "Blink your eyes"
    FaceAction.DONE -> "Face capture complete!"
}

fun getIconForAction(action: FaceAction) = when (action) {
    FaceAction.CENTER -> Icons.Default.FaceRetouchingNatural
    FaceAction.LOOK_LEFT -> Icons.Default.ArrowBack
    FaceAction.LOOK_RIGHT -> Icons.Default.ArrowForward
    FaceAction.LOOK_UP -> Icons.Default.ArrowUpward
    FaceAction.LOOK_DOWN -> Icons.Default.ArrowDownward
    FaceAction.BLINK -> Icons.Default.RemoveRedEye
    FaceAction.DONE -> Icons.Default.CheckCircle
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        label = { Text(label) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = OrchidPrimary)
        },
        trailingIcon = trailing,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrchidPrimary,
            unfocusedBorderColor = Color(0xFFE9D5F5),
            focusedLabelColor = OrchidPrimary,
            cursorColor = OrchidPrimary,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun PremiumPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "primaryScale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = InkDeep,
            disabledContainerColor = InkDeep.copy(alpha = 0.45f)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.6.sp
        )
    }
}

@Composable
fun PremiumSecondaryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "secondaryScale"
    )

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.4.dp, OrchidPrimary.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = OrchidLight,
            contentColor = InkDeep,
            disabledContainerColor = OrchidLight.copy(alpha = 0.5f),
            disabledContentColor = Color.Gray
        )
    ) {
        Icon(icon, contentDescription = null, tint = OrchidPrimary)
        Spacer(Modifier.width(10.dp))
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LoginRedirectRow(onLoginClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Already registered?",
            color = Color.Gray
        )
        TextButton(onClick = onLoginClick) {
            Text(
                "Sign in",
                color = OrchidPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AnimatedShimmerTitle(text: String) {
    val shimmerColors = listOf(InkBlack, InkBlack, OrchidPrimary, InkBlack, InkBlack)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing)
        ),
        label = "shimmerMove"
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
                    size.height * 0.12f
                ),
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
