package com.example.phasmatic.ui.userinfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import kotlin.math.sin

val InkBlack = Color(0xFF000000)
val InkDeep = Color(0xFF1E1B4B)
val HeroIndigoEnd = Color(0xFF312E81)
val OrchidPrimary = Color(0xFFD946EF)
val OrchidLight = Color(0xFFFDF4FF)
val SoftPinkGlow = Color(0xFFFFE4FF)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun UserInfoScreen(
    userFullName: String?,
    universities: List<String>,
    selectedUniversity: String,
    academicLevel: String,
    languages: String,
    gpa: String,
    field: String,
    budget: String,
    selectedYear: String,
    //advisorType: String?,
    isLoading: Boolean,
    isSaving: Boolean,
    infoMessage: String?,
    onUniversityChange: (String) -> Unit,
    onAcademicLevelChange: (String) -> Unit,
    onLanguagesChange: (String) -> Unit,
    onGpaChange: (String) -> Unit,
    onFieldChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onAdvisorSelected: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        AnimatedMeshBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            TopBrand()
            Spacer(Modifier.height(24.dp))

            UserInfoHeroCard(name = userFullName?.split(" ")?.firstOrNull() ?: "Scholar")

            Spacer(Modifier.height(24.dp))

            UserInfoFormCard(
                universities = universities,
                selectedUniversity = selectedUniversity,
                academicLevel = academicLevel,
                languages = languages,
                gpa = gpa,
                field = field,
                budget = budget,
                selectedYear = selectedYear,
                //advisorType = advisorType,
                isLoading = isLoading,
                isSaving = isSaving,
                infoMessage = infoMessage,
                onUniversityChange = onUniversityChange,
                onAcademicLevelChange = onAcademicLevelChange,
                onLanguagesChange = onLanguagesChange,
                onGpaChange = onGpaChange,
                onFieldChange = onFieldChange,
                onBudgetChange = onBudgetChange,
                onYearChange = onYearChange,
                onAdvisorSelected = onAdvisorSelected,
                onSaveClick = onSaveClick
            )
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
            brush = Brush.linearGradient(colors = listOf(InkDeep, OrchidPrimary))
        )
    )
}

@Composable
fun UserInfoHeroCard(name: String) {
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
                .offset(x = 180.dp, y = (-40).dp)
                .background(OrchidPrimary.copy(alpha = 0.25f), CircleShape)
                .blur(45.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuralPrismAura()
            Spacer(Modifier.width(22.dp))
            Column {
                Text(
                    "Before we begin,",
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    name,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Set up your academic profile to get personalized guidance.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun UserInfoFormCard(
    universities: List<String>,
    selectedUniversity: String,
    academicLevel: String,
    languages: String,
    gpa: String,
    field: String,
    budget: String,
    selectedYear: String,
    //advisorType: String?,
    isLoading: Boolean,
    isSaving: Boolean,
    infoMessage: String?,
    onUniversityChange: (String) -> Unit,
    onAcademicLevelChange: (String) -> Unit,
    onLanguagesChange: (String) -> Unit,
    onGpaChange: (String) -> Unit,
    onFieldChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onAdvisorSelected: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.96f))
            .border(1.dp, Color(0xFFF1E8F7), RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        AnimatedShimmerTitle("ACADEMIC PROFILE")
        Spacer(Modifier.height(18.dp))

        if (isLoading) {
            Text("Loading profile...", color = Color.Gray)
            Spacer(Modifier.height(10.dp))
        }

        PremiumDropdownField(
            label = "University",
            options = universities,
            selectedValue = selectedUniversity,
            onValueSelected = onUniversityChange
        )

        Spacer(Modifier.height(14.dp))

        PremiumDropdownField(
            label = "Year of studies",
            options = listOf("1", "2", "3", "4"),
            selectedValue = selectedYear,
            onValueSelected = onYearChange
        )

        Spacer(Modifier.height(14.dp))

        PremiumTextField(
            value = academicLevel,
            onValueChange = onAcademicLevelChange,
            label = "Academic level",
            leadingIcon = Icons.Default.School
        )

        Spacer(Modifier.height(14.dp))

        PremiumTextField(
            value = languages,
            onValueChange = onLanguagesChange,
            label = "Languages",
            leadingIcon = Icons.Default.Language
        )

        Spacer(Modifier.height(14.dp))

        PremiumTextField(
            value = gpa,
            onValueChange = onGpaChange,
            label = "GPA",
            leadingIcon = Icons.Default.Star
        )

        Spacer(Modifier.height(14.dp))

        PremiumTextField(
            value = field,
            onValueChange = onFieldChange,
            label = "Field of interest",
            leadingIcon = Icons.Default.AutoAwesome
        )

        Spacer(Modifier.height(14.dp))

        PremiumTextField(
            value = budget,
            onValueChange = onBudgetChange,
            label = "Budget per year",
            leadingIcon = Icons.Default.AccountBalanceWallet,
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(20.dp))

        AnimatedVisibility(visible = !infoMessage.isNullOrBlank()) {
            Text(
                text = infoMessage.orEmpty(),
                color = if (infoMessage?.contains("failed", true) == true ||
                    infoMessage?.contains("invalid", true) == true ||
                    infoMessage?.contains("fill", true) == true
                ) Color(0xFFDC2626) else OrchidPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        PremiumPrimaryButton(
            text = if (isSaving) "SAVING..." else "SAVE & CONTINUE",
            onClick = onSaveClick,
            enabled = !isSaving
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumDropdownField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrchidPrimary,
                unfocusedBorderColor = Color(0xFFE9D5F5),
                focusedLabelColor = OrchidPrimary,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onValueSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = keyboardType
        ),
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
fun AdvisorCard(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) OrchidPrimary else Color(0xFFE9D5F5)
    val bgColor = if (selected) OrchidLight else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = OrchidPrimary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = InkDeep,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
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
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(15000, easing = LinearEasing)
        ),
        label = "rot"
    )

    Box(
        modifier = Modifier
            .size(84.dp)
            .rotate(rotation)
            .drawBehind {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(OrchidPrimary, Color.Transparent, OrchidPrimary)
                    ),
                    style = Stroke(width = 6f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(OrchidPrimary.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    radius = size.width / 1.5f
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = OrchidPrimary,
            modifier = Modifier.size(30.dp)
        )
    }
}