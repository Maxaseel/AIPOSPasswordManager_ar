package com.aipos.aipospm.ui.screens

import com.aipos.aipospm.R

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import com.aipos.aipospm.ui.components.VaultIconRegistry
import com.aipos.aipospm.ui.theme.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.rememberModalBottomSheetState
import com.aipos.aipospm.data.SortOption
import com.aipos.aipospm.ui.components.SortBottomSheet
import com.aipos.aipospm.ui.components.VaultSectionHeader
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aipos.aipospm.data.PasswordEntry
import com.aipos.aipospm.ui.theme.DangerRed
import com.aipos.aipospm.ui.theme.SecurityGreen
import com.aipos.aipospm.ui.theme.WarningAmber
import com.aipos.aipospm.ui.viewmodels.CategoryViewModel
import com.aipos.aipospm.ui.viewmodels.PasswordViewModel
import kotlinx.coroutines.launch

import androidx.compose.foundation.interaction.MutableInteractionSource
import com.aipos.aipospm.ui.components.bounceClick
import com.aipos.aipospm.ui.components.pressScale
import com.aipos.aipospm.security.ClipboardHelper
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordListScreen(
    passwordViewModel: PasswordViewModel,
    categoryViewModel: CategoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.txt_passwords_02c830), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.txt_back_b52b36))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            FloatingActionButton(
                onClick = onNavigateToAdd,
                interactionSource = interactionSource,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.pressScale(interactionSource)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.txt_add_password_6e19a6))
            }
        }
    ) { padding ->
        PasswordListContent(
            passwordViewModel = passwordViewModel,
            categoryViewModel = categoryViewModel,
            onNavigateToDetail = onNavigateToDetail,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(padding)
        )
    }
}

/**
 * Reusable password list body (search bar, category chips, and list).
 * Can be embedded inside HomeScreen tabs or used standalone via PasswordListScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordListContent(
    passwordViewModel: PasswordViewModel,
    categoryViewModel: CategoryViewModel,
    onNavigateToDetail: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val passwords by passwordViewModel.passwords.collectAsStateWithLifecycle()
    val categories by categoryViewModel.passwordCategories.collectAsStateWithLifecycle()
    val selectedCategoryIdFilter by passwordViewModel.selectedCategoryIdFilter.collectAsStateWithLifecycle()
    val showCompromisedOnlyFilter by passwordViewModel.showCompromisedOnlyFilter.collectAsStateWithLifecycle()
    val showReusedOnlyFilter by passwordViewModel.showReusedOnlyFilter.collectAsStateWithLifecycle()
    val breachedPasswordCount by passwordViewModel.breachedPasswordCount.collectAsStateWithLifecycle()
    val reusedPasswordCount by passwordViewModel.reusedPasswordCount.collectAsStateWithLifecycle()
    val reusedPasswordIds by passwordViewModel.reusedPasswordIds.collectAsStateWithLifecycle()
    val compromisedPasswordIds by passwordViewModel.compromisedPasswordIds.collectAsStateWithLifecycle()
    val searchQuery by passwordViewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by passwordViewModel.sortOption.collectAsStateWithLifecycle()
    val pinFavorites by passwordViewModel.pinFavorites.collectAsStateWithLifecycle()
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showSortSheet) {
        SortBottomSheet(
            sheetState = sortSheetState,
            currentSortOption = sortOption,
            pinFavorites = pinFavorites,
            onSortOptionSelected = { passwordViewModel.setSortOption(it) },
            onPinFavoritesToggled = { passwordViewModel.setPinFavorites(it) },
            onDismissRequest = { showSortSheet = false }
        )
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar & Sort Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { passwordViewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.txt_search_bce064),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.ui_search_passwords_33d307),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { passwordViewModel.updateSearchQuery("") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.txt_clear_719ea3),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { showSortSheet = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (sortOption != SortOption.UPDATED_DESC || pinFavorites)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = stringResource(R.string.txt_sort_passwords_0da6e8),
                        tint = if (sortOption != SortOption.UPDATED_DESC || pinFavorites)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Category & Security filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (breachedPasswordCount > 0 || showCompromisedOnlyFilter) {
                    item {
                        FilterChip(
                            selected = showCompromisedOnlyFilter,
                            onClick = {
                                passwordViewModel.setShowCompromisedOnlyFilter(!showCompromisedOnlyFilter)
                            },
                            label = { Text(stringResource(R.string.txt_compromised_count, breachedPasswordCount)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = DangerRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DangerRed.copy(alpha = 0.15f),
                                selectedLabelColor = DangerRed,
                                selectedLeadingIconColor = DangerRed,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = showCompromisedOnlyFilter,
                                selectedBorderColor = DangerRed.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                if (reusedPasswordCount > 0 || showReusedOnlyFilter) {
                    item {
                        FilterChip(
                            selected = showReusedOnlyFilter,
                            onClick = {
                                passwordViewModel.setShowReusedOnlyFilter(!showReusedOnlyFilter)
                            },
                            label = { Text(stringResource(R.string.txt_reused_count, reusedPasswordCount)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarningAmber.copy(alpha = 0.15f),
                                selectedLabelColor = WarningAmber,
                                selectedLeadingIconColor = WarningAmber,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = showReusedOnlyFilter,
                                selectedBorderColor = WarningAmber.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                item {
                    FilterChip(
                        selected = selectedCategoryIdFilter == null && !showCompromisedOnlyFilter && !showReusedOnlyFilter,
                        onClick = {
                            passwordViewModel.setShowCompromisedOnlyFilter(false)
                            passwordViewModel.setShowReusedOnlyFilter(false)
                            passwordViewModel.selectCategoryFilter(null)
                        },
                        label = { Text(stringResource(R.string.txt_all_6a7208)) }
                    )
                }
                items(categories, key = { it.id }) { category ->
                    FilterChip(
                        selected = selectedCategoryIdFilter == category.id && !showCompromisedOnlyFilter && !showReusedOnlyFilter,
                        onClick = {
                            passwordViewModel.setShowCompromisedOnlyFilter(false)
                            passwordViewModel.setShowReusedOnlyFilter(false)
                            passwordViewModel.selectCategoryFilter(category.id)
                        },
                        label = { Text(category.name) }
                    )
                }
            }

            if (passwords.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    showCompromisedOnlyFilter -> DangerRed.copy(alpha = 0.1f)
                                    showReusedOnlyFilter -> WarningAmber.copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                showCompromisedOnlyFilter -> Icons.Default.Shield
                                showReusedOnlyFilter -> Icons.Default.Repeat
                                else -> Icons.Default.Password
                            },
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = when {
                                showCompromisedOnlyFilter -> DangerRed
                                showReusedOnlyFilter -> WarningAmber
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when {
                            showCompromisedOnlyFilter -> stringResource(R.string.empty_no_compromised)
                            showReusedOnlyFilter -> stringResource(R.string.empty_no_reused)
                            searchQuery.isNotEmpty() || selectedCategoryIdFilter != null -> stringResource(R.string.empty_no_results)
                            else -> stringResource(R.string.empty_no_passwords)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when {
                            showCompromisedOnlyFilter -> stringResource(R.string.empty_passwords_clean)
                            showReusedOnlyFilter -> stringResource(R.string.empty_passwords_unique)
                            searchQuery.isNotEmpty() || selectedCategoryIdFilter != null -> stringResource(R.string.empty_try_search)
                            else -> stringResource(R.string.empty_add_first_password)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (showCompromisedOnlyFilter || showReusedOnlyFilter) {
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.TextButton(
                            onClick = {
                                passwordViewModel.setShowCompromisedOnlyFilter(false)
                                passwordViewModel.setShowReusedOnlyFilter(false)
                            }
                        ) {
                            Text(stringResource(R.string.txt_show_all_passwords_4ba392))
                        }
                    }
                }
            } else {
                val categoryMap = remember(categories) { categories.associateBy { it.id } }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = passwords,
                        key = { _, it -> it.id },
                        contentType = { _, _ -> "password_item" }
                    ) { index, entry ->
                        val hasBoth = pinFavorites && passwords.any { it.isFavorite } && passwords.any { !it.isFavorite }
                        val isFirstFavorite = hasBoth && index == 0 && entry.isFavorite
                        val isFirstNonFavorite = hasBoth && !entry.isFavorite && (index == 0 || passwords[index - 1].isFavorite)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (isFirstFavorite) {
                                VaultSectionHeader(
                                    title = stringResource(R.string.ui_favorites_07b3e4),
                                    count = passwords.count { it.isFavorite },
                                    icon = Icons.Default.Star,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else if (isFirstNonFavorite) {
                                VaultSectionHeader(
                                    title = stringResource(R.string.ui_all_passwords_4e60d6),
                                    count = passwords.count { !it.isFavorite },
                                    icon = Icons.Default.Password,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            @Suppress("DEPRECATION")
                            val dismissState = rememberSwipeToDismissBoxState(
                                positionalThreshold = { distance -> distance * 0.5f },
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        passwordViewModel.deletePassword(entry)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = context.getString(R.string.ui_password_deleted_9dcca4, entry.title),
                                                actionLabel = "Undo"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                passwordViewModel.restorePassword(entry)
                                            }
                                        }
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                modifier = Modifier.animateItem(),
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        DangerRed.copy(alpha = 0.15f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.txt_delete_f6fdbe),
                                            tint = DangerRed,
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .size(24.dp)
                                        )
                                    }
                                },
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true
                            ) {
                                val isBreached = entry.id in compromisedPasswordIds
                                val isReused = entry.id in reusedPasswordIds
                                val isCustomReorderEnabled = sortOption == SortOption.CUSTOM &&
                                    searchQuery.isEmpty() &&
                                    !showCompromisedOnlyFilter &&
                                    !showReusedOnlyFilter

                                val canMoveUp = isCustomReorderEnabled && index > 0 &&
                                    (!pinFavorites || entry.isFavorite == passwords[index - 1].isFavorite)

                                val canMoveDown = isCustomReorderEnabled && index < passwords.size - 1 &&
                                    (!pinFavorites || entry.isFavorite == passwords[index + 1].isFavorite)

                                PasswordCard(
                                    entry = entry,
                                    categoryName = categoryMap[entry.categoryId]?.name,
                                    isBreached = isBreached,
                                    isReused = isReused,
                                    onClick = { onNavigateToDetail(entry.id) },
                                    onFavoriteClick = { passwordViewModel.toggleFavorite(entry) },
                                    onCopyUsername = {
                                        ClipboardHelper.copyAndScheduleClear(context, "Username", entry.username)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(context.getString(R.string.ui_username_copied_clears_in_30s_8461b7))
                                        }
                                    },
                                    onMoveUp = if (canMoveUp) {
                                        { passwordViewModel.movePassword(passwords, index, -1) }
                                    } else null,
                                    onMoveDown = if (canMoveDown) {
                                        { passwordViewModel.movePassword(passwords, index, 1) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getCategoryColors(name: String): Pair<Color, Color> {
    val hash = name.hashCode().absoluteValue
    val hue = (hash % 360).toFloat()
    val isDark = isSystemInDarkTheme()
    val containerColor = Color.hsv(hue, 0.15f, if (isDark) 0.15f else 0.95f)
    val contentColor = Color.hsv(hue, 0.8f, if (isDark) 0.9f else 0.4f)
    return Pair(containerColor, contentColor)
}

@Composable
private fun PasswordCard(
    entry: PasswordEntry,
    categoryName: String?,
    isBreached: Boolean,
    isReused: Boolean = false,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCopyUsername: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    val barColor = when {
        isBreached -> DangerRed
        isReused -> WarningAmber
        else -> MaterialTheme.colorScheme.primary
    }
    val borderStroke = when {
        isBreached -> BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
        isReused -> BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f))
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(barColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 14.dp, end = 12.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isBreached -> DangerRed.copy(alpha = 0.12f)
                            isReused -> WarningAmber.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isBreached -> {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = stringResource(R.string.txt_breached_e24f1b),
                                    tint = DangerRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            isReused -> {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = stringResource(R.string.txt_reused_103b12),
                                    tint = WarningAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            else -> {
                                val customIcon = VaultIconRegistry.getIcon(entry.icon)
                                if (customIcon != null) {
                                    Icon(
                                        imageVector = customIcon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Text(
                                        text = entry.title.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = entry.username,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isBreached) {
                            Card(
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = DangerRed.copy(alpha = 0.12f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DangerRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = stringResource(R.string.ui_compromised_acf32d),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DangerRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else if (isReused) {
                            Card(
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = WarningAmber.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Repeat,
                                        contentDescription = null,
                                        tint = WarningAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = stringResource(R.string.ui_reused_103b12),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarningAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (categoryName != null) {
                            val (containerColor, contentColor) = getCategoryColors(categoryName)
                            Card(
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor
                                )
                            ) {
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = contentColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onCopyUsername, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.txt_copy_username_872e64),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (entry.isFavorite) Icons.Default.Star
                        else Icons.Default.StarBorder,
                        contentDescription = stringResource(R.string.txt_favorite_6b90b6),
                        modifier = Modifier.size(18.dp),
                        tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                }

                if (onMoveUp != null || onMoveDown != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onMoveUp?.invoke() },
                            enabled = onMoveUp != null,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.txt_move_up_b4f57c),
                                modifier = Modifier.size(18.dp),
                                tint = if (onMoveUp != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                        IconButton(
                            onClick = { onMoveDown?.invoke() },
                            enabled = onMoveDown != null,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.txt_move_down_260ff8),
                                modifier = Modifier.size(18.dp),
                                tint = if (onMoveDown != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}
