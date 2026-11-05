package com.aipos.aipospm.ui.screens

import com.aipos.aipospm.R

import android.content.ClipData
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import com.aipos.aipospm.ui.components.VaultIconRegistry
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aipos.aipospm.data.ApiKeyEntry
import com.aipos.aipospm.data.PasswordEntry
import com.aipos.aipospm.ui.theme.DangerRed
import com.aipos.aipospm.ui.theme.SecurityGreen
import com.aipos.aipospm.ui.theme.WarningAmber
import kotlinx.coroutines.launch
import com.aipos.aipospm.ui.viewmodels.ApiKeyViewModel
import com.aipos.aipospm.ui.viewmodels.CategoryViewModel
import com.aipos.aipospm.ui.viewmodels.PasswordViewModel
import com.aipos.aipospm.security.ClipboardHelper
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.aipos.aipospm.ui.components.bounceClick
import com.aipos.aipospm.ui.components.pressScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    passwordViewModel: PasswordViewModel,
    apiKeyViewModel: ApiKeyViewModel,
    categoryViewModel: CategoryViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddPassword: () -> Unit,
    onNavigateToAddApiKey: () -> Unit,
    onNavigateToPasswordDetail: (Int) -> Unit,
    onNavigateToApiKeyDetail: (Int) -> Unit,
    onNavigateToPasswordGenerator: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val passwordCount by passwordViewModel.passwordCount.collectAsStateWithLifecycle()
    val apiKeyCount by apiKeyViewModel.apiKeyCount.collectAsStateWithLifecycle()
    val favoritePasswords by passwordViewModel.favoritePasswords.collectAsStateWithLifecycle()
    val favoriteApiKeys by apiKeyViewModel.favoriteApiKeys.collectAsStateWithLifecycle()
    val breachedPasswordCount by passwordViewModel.breachedPasswordCount.collectAsStateWithLifecycle()
    val reusedPasswordCount by passwordViewModel.reusedPasswordCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Dynamic top bar title per tab
    val topBarTitle = when (selectedTab) {
        1 -> "Passwords"
        2 -> stringResource(R.string.ui_api_keys_e18ffc)
        else -> "AIPOS"
    }

    val topBarSubtitle = when (selectedTab) {
        0 -> stringResource(R.string.ui_password_manager)
        else -> null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = topBarTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (topBarSubtitle != null) {
                            Text(
                                text = topBarSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.txt_settings_c7f73b))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = stringResource(R.string.txt_home_70f8bb)) },
                    label = { Text(stringResource(R.string.txt_home_70f8bb)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        passwordViewModel.setShowCompromisedOnlyFilter(false)
                        passwordViewModel.setShowReusedOnlyFilter(false)
                        selectedTab = 1
                    },
                    icon = { Icon(Icons.Default.Password, contentDescription = stringResource(R.string.txt_passwords_02c830)) },
                    label = { Text(stringResource(R.string.txt_passwords_02c830)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.VpnKey, contentDescription = stringResource(R.string.txt_api_keys_e18ffc)) },
                    label = { Text(stringResource(R.string.txt_api_keys_e18ffc)) }
                )
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    val interactionSource = remember { MutableInteractionSource() }
                    ExtendedFloatingActionButton(
                        onClick = onNavigateToAddPassword,
                        interactionSource = interactionSource,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.pressScale(interactionSource)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.txt_add_61cc55))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.txt_add_new_07c4aa))
                    }
                }
                1 -> {
                    val interactionSource = remember { MutableInteractionSource() }
                    FloatingActionButton(
                        onClick = onNavigateToAddPassword,
                        interactionSource = interactionSource,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.pressScale(interactionSource)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.txt_add_password_6e19a6))
                    }
                }
                2 -> {
                    val interactionSource = remember { MutableInteractionSource() }
                    FloatingActionButton(
                        onClick = onNavigateToAddApiKey,
                        interactionSource = interactionSource,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.pressScale(interactionSource)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.txt_add_api_key_14b400))
                    }
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "tabContent"
        ) { tab ->
            when (tab) {
                0 -> DashboardContent(
                    passwordCount = passwordCount,
                    apiKeyCount = apiKeyCount,
                    favoritePasswords = favoritePasswords,
                    favoriteApiKeys = favoriteApiKeys,
                    breachedPasswordCount = breachedPasswordCount,
                    reusedPasswordCount = reusedPasswordCount,
                    onNavigateToPasswords = {
                        if (breachedPasswordCount > 0) {
                            passwordViewModel.setShowCompromisedOnlyFilter(true)
                        } else if (reusedPasswordCount > 0) {
                            passwordViewModel.setShowReusedOnlyFilter(true)
                        } else {
                            passwordViewModel.setShowCompromisedOnlyFilter(false)
                            passwordViewModel.setShowReusedOnlyFilter(false)
                        }
                        selectedTab = 1
                    },
                    onNavigateToApiKeys = { selectedTab = 2 },
                    onNavigateToPasswordDetail = onNavigateToPasswordDetail,
                    onNavigateToApiKeyDetail = onNavigateToApiKeyDetail,
                    onNavigateToPasswordGenerator = onNavigateToPasswordGenerator,
                    onNavigateToAddPassword = onNavigateToAddPassword,
                    onNavigateToAddApiKey = onNavigateToAddApiKey,
                    onCopyUsername = { entry ->
                        ClipboardHelper.copyAndScheduleClear(context, context.getString(R.string.ui_username), entry.username)
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.ui_username_copied_clears_in_30s_8461b7))
                        }
                    },
                    onCopyApiKey = { entry ->
                        val decrypted = apiKeyViewModel.decryptApiKey(entry)
                        ClipboardHelper.copyAndScheduleClear(context, context.getString(R.string.ui_api_key_47acd2), decrypted)
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.ui_api_key_copied_clears_in_30s_48a4a4))
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
                1 -> PasswordListContent(
                    passwordViewModel = passwordViewModel,
                    categoryViewModel = categoryViewModel,
                    onNavigateToDetail = onNavigateToPasswordDetail,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(padding)
                )
                2 -> ApiKeyListContent(
                    apiKeyViewModel = apiKeyViewModel,
                    categoryViewModel = categoryViewModel,
                    onNavigateToDetail = onNavigateToApiKeyDetail,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

/**
 * Dashboard tab content — vault health, summary cards, quick actions, favorites.
 */
@Composable
private fun DashboardContent(
    passwordCount: Int,
    apiKeyCount: Int,
    favoritePasswords: List<PasswordEntry>,
    favoriteApiKeys: List<ApiKeyEntry>,
    breachedPasswordCount: Int,
    reusedPasswordCount: Int = 0,
    onNavigateToPasswords: () -> Unit,
    onNavigateToApiKeys: () -> Unit,
    onNavigateToPasswordDetail: (Int) -> Unit,
    onNavigateToApiKeyDetail: (Int) -> Unit,
    onNavigateToPasswordGenerator: () -> Unit,
    onNavigateToAddPassword: () -> Unit,
    onNavigateToAddApiKey: () -> Unit,
    onCopyUsername: (PasswordEntry) -> Unit,
    onCopyApiKey: (ApiKeyEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalEntries = passwordCount + apiKeyCount
    val issueCount = (breachedPasswordCount + reusedPasswordCount).coerceAtMost(passwordCount)
    val score = if (passwordCount == 0) 100 else (((passwordCount - issueCount).toFloat() / passwordCount) * 100).toInt()

    val isCompromised = breachedPasswordCount > 0
    val isReused = reusedPasswordCount > 0

    val statusTitle = when {
        totalEntries == 0 -> stringResource(R.string.home_welcome)
        isCompromised -> stringResource(R.string.home_action_required)
        isReused -> stringResource(R.string.home_review_recommended)
        else -> stringResource(R.string.home_vault_protected)
    }

    val statusDesc = when {
        totalEntries == 0 -> stringResource(R.string.home_empty_desc)
        isCompromised -> stringResource(R.string.home_compromised_desc, breachedPasswordCount)
        isReused -> stringResource(R.string.home_reused_desc, reusedPasswordCount)
        else -> stringResource(R.string.home_secure_desc)
    }

    val ringColor = when {
        totalEntries == 0 -> MaterialTheme.colorScheme.primary
        isCompromised -> DangerRed
        isReused -> WarningAmber
        score == 100 -> SecurityGreen
        score >= 70 -> WarningAmber
        else -> DangerRed
    }

    val borderStroke = BorderStroke(
        width = 1.dp,
        color = ringColor.copy(alpha = 0.3f)
    )

    // Entrance animation state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // Combined Favorites List
    val favoritesList = remember(favoritePasswords, favoriteApiKeys) {
        val list = mutableListOf<FavoriteItem>()
        favoritePasswords.forEach { list.add(FavoriteItem.Password(it)) }
        favoriteApiKeys.forEach { list.add(FavoriteItem.ApiKey(it)) }
        list.sortBy { it.title.lowercase() }
        list
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vault Health Card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -40 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .bounceClick { onNavigateToPasswords() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                ringColor.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ringColor.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    radius = 500f
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VaultScoreRing(
                                score = score,
                                color = ringColor,
                                totalEntries = totalEntries,
                                modifier = Modifier.size(76.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ringColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = statusTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = statusDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Summary Cards Grid
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(tween(500, delayMillis = 100)) { -30 }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = stringResource(R.string.ui_passwords_02c830),
                        count = passwordCount,
                        icon = Icons.Default.Password,
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToPasswords,
                        onAddClick = onNavigateToAddPassword,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = stringResource(R.string.ui_api_keys_e18ffc),
                        count = apiKeyCount,
                        icon = Icons.Default.VpnKey,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToApiKeys,
                        onAddClick = onNavigateToAddApiKey,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Password Generator Row
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 150)) + slideInVertically(tween(500, delayMillis = 150)) { -30 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick { onNavigateToPasswordGenerator() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.ui_password_generator_00cac0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.ui_create_strong_random_passwords_local_0de268),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Unified Favorites Section
        if (favoritesList.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.ui_favorites_dc8f67, favoritesList.size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            items(favoritesList, key = { if (it.isPassword) "pwd_${it.id}" else "key_${it.id}" }) { item ->
                FavoriteItemCard(
                    item = item,
                    onClick = {
                        if (item.isPassword) {
                            onNavigateToPasswordDetail(item.id)
                        } else {
                            onNavigateToApiKeyDetail(item.id)
                        }
                    },
                    onCopy = {
                        if (item.isPassword) {
                            onCopyUsername((item as FavoriteItem.Password).entry)
                        } else {
                            onCopyApiKey((item as FavoriteItem.ApiKey).entry)
                        }
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }

        // Empty state
        if (passwordCount == 0 && apiKeyCount == 0) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Large shield icon with gradient backdrop
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = stringResource(R.string.ui_your_vault_is_ready_f22c5d),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.ui_start_by_adding_your_first_credentia_628b18),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultScoreRing(
    score: Int,
    color: Color,
    totalEntries: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalEntries == 0) 1.0f else (score / 100f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "scoreRingProgress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            
            // Draw background track ring
            drawArc(
                color = color.copy(alpha = 0.12f),
                startAngle = -210f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw progress sweep
            drawArc(
                color = color,
                startAngle = -210f,
                sweepAngle = 240f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (totalEntries == 0) "-" else stringResource(R.string.ui_message_37a23c, score),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (totalEntries == 0) MaterialTheme.colorScheme.onSurfaceVariant else color
            )
            Text(
                text = stringResource(R.string.ui_score_489f48),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .animateContentSize()
            .bounceClick(onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, containerColor.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(containerColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = containerColor
                    )
                }
                
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(containerColor.copy(alpha = 0.15f))
                        .bounceClick { onAddClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.txt_add_named, title),
                        modifier = Modifier.size(16.dp),
                        tint = containerColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteItemCard(
    item: FavoriteItem,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val customIcon = VaultIconRegistry.getIcon(item.icon)
            val icon = customIcon ?: if (item.isPassword) Icons.Default.Shield else Icons.Default.VpnKey
            val iconColor = if (item.isPassword) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            val iconBg = iconColor.copy(alpha = 0.1f)

            // Left color accent bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(iconColor)
            )

            Spacer(modifier = Modifier.width(10.dp))
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.txt_copy_af74f7),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.txt_view_details_badd38),
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

sealed interface FavoriteItem {
    val id: Int
    val title: String
    val subtitle: String
    val isPassword: Boolean
    val icon: String?

    data class Password(val entry: PasswordEntry) : FavoriteItem {
        override val id = entry.id
        override val title = entry.title
        override val subtitle = entry.username
        override val isPassword = true
        override val icon = entry.icon
    }

    data class ApiKey(val entry: ApiKeyEntry) : FavoriteItem {
        override val id = entry.id
        override val title = entry.serviceName
        override val subtitle = stringResource(R.string.ui_api_key_47acd2)
        override val isPassword = false
        override val icon = entry.icon
    }
}
