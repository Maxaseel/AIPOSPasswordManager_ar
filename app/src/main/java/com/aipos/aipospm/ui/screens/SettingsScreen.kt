package com.aipos.aipospm.ui.screens

import com.aipos.aipospm.R

import android.content.Intent
import com.aipos.aipospm.MainActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.aipos.aipospm.ui.components.bounceClick
import com.aipos.aipospm.ui.components.pressScale
import android.os.Build
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aipos.aipospm.security.ClipboardHelper
import com.aipos.aipospm.ui.theme.DangerRed
import com.aipos.aipospm.ui.theme.SecurityGreen
import com.aipos.aipospm.ui.theme.WarningAmber
import com.aipos.aipospm.ui.viewmodels.ApiKeyViewModel
import com.aipos.aipospm.ui.viewmodels.AuthViewModel
import com.aipos.aipospm.ui.viewmodels.CategoryViewModel
import com.aipos.aipospm.ui.viewmodels.PasswordViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    passwordViewModel: PasswordViewModel,
    apiKeyViewModel: ApiKeyViewModel? = null,
    categoryViewModel: CategoryViewModel,
    canUseBiometric: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToManageCategories: () -> Unit,
    onNavigateToTrash: () -> Unit = {}
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val deletedPasswordCount by passwordViewModel.deletedPasswordCount.collectAsStateWithLifecycle()
    val deletedApiKeyCount by (apiKeyViewModel?.deletedApiKeyCount ?: remember { kotlinx.coroutines.flow.MutableStateFlow(0) }).collectAsStateWithLifecycle()
    val totalDeletedCount = deletedPasswordCount + deletedApiKeyCount
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isAutofillEnabled by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val am = context.getSystemService(android.view.autofill.AutofillManager::class.java)
                am != null && am.isAutofillSupported && am.hasEnabledAutofillServices()
            } else false
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val am = context.getSystemService(android.view.autofill.AutofillManager::class.java)
                    isAutofillEnabled = am != null && am.isAutofillSupported && am.hasEnabledAutofillServices()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(authState.error) {
        authState.error?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearError()
        }
    }

    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val isBiometricEnabled = authViewModel.isBiometricEnabled()

    // Backup states
    var backupPasswordToSet by rememberSaveable { mutableStateOf("") }
    var confirmBackupPasswordToSet by rememberSaveable { mutableStateOf("") }
    var backupPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmBackupPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var showSetBackupPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var pendingExportPassword by rememberSaveable { mutableStateOf("") }
    var isBackupInProgress by rememberSaveable { mutableStateOf(false) }

    var backupPasswordToEnter by rememberSaveable { mutableStateOf("") }
    var backupPasswordToEnterVisible by rememberSaveable { mutableStateOf(false) }
    var showEnterBackupPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var importUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val importUri = remember(importUriString) { importUriString?.let { android.net.Uri.parse(it) } }

    // Recovery Key states
    var showViewKeyPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordInput by rememberSaveable { mutableStateOf("") }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var showRecoveryKeyDialog by rememberSaveable { mutableStateOf(false) }
    var decryptedRecoveryKey by rememberSaveable { mutableStateOf("") }

    val csvImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}

            passwordViewModel.importCsv(
                uri = uri,
                onSuccess = { insertedCount ->
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.ui_successfully_imported_entries_6cac85, insertedCount))
                    }
                },
                onError = { error ->
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.ui_csv_import_failed_1bdf46, error))
                    }
                }
            )
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val pwd = pendingExportPassword
        pendingExportPassword = ""
        backupPasswordToSet = ""
        confirmBackupPasswordToSet = ""
        if (uri != null && pwd.isNotEmpty()) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* Some providers don't support persistable permissions — that's OK */ }

            isBackupInProgress = true
            passwordViewModel.exportBackup(
                uri = uri,
                password = pwd,
                onSuccess = {
                    isBackupInProgress = false
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.ui_backup_exported_successfully_659960))
                    }
                },
                onError = { error ->
                    isBackupInProgress = false
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.ui_export_failed_fccfa9, error))
                    }
                }
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* Some providers don't support persistable permissions — that's OK */ }
            importUriString = uri.toString()
            backupPasswordToEnter = ""
            backupPasswordToEnterVisible = false
            showEnterBackupPasswordDialog = true
        }
    }

    // Set Backup Password Dialog (for export)
    if (showSetBackupPasswordDialog) {
        val passwordsMatch = backupPasswordToSet.isNotEmpty() && backupPasswordToSet == confirmBackupPasswordToSet
        AlertDialog(
            onDismissRequest = {
                if (!isBackupInProgress) {
                    showSetBackupPasswordDialog = false
                    backupPasswordToSet = ""
                    confirmBackupPasswordToSet = ""
                }
            },
            title = { Text(stringResource(R.string.txt_set_backup_password_3b967a)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.ui_enter_a_password_to_encrypt_your_bac_232579),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = backupPasswordToSet,
                        onValueChange = { backupPasswordToSet = it },
                        label = { Text(stringResource(R.string.txt_backup_password_f6bc21)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (backupPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { backupPasswordVisible = !backupPasswordVisible }) {
                                Icon(
                                    imageVector = if (backupPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (backupPasswordVisible) stringResource(R.string.ui_hide_password_e40123) else stringResource(R.string.ui_show_password_044b85)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmBackupPasswordToSet,
                        onValueChange = { confirmBackupPasswordToSet = it },
                        label = { Text(stringResource(R.string.txt_confirm_backup_password_462305)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmBackupPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmBackupPasswordVisible = !confirmBackupPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmBackupPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmBackupPasswordVisible) stringResource(R.string.ui_hide_password_e40123) else stringResource(R.string.ui_show_password_044b85)
                                )
                            }
                        },
                        isError = confirmBackupPasswordToSet.isNotEmpty() && !passwordsMatch,
                        supportingText = {
                            if (confirmBackupPasswordToSet.isNotEmpty() && !passwordsMatch) {
                                Text(stringResource(R.string.txt_passwords_do_not_match_d69c3b), color = MaterialTheme.colorScheme.error)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (passwordsMatch) {
                            pendingExportPassword = backupPasswordToSet
                            showSetBackupPasswordDialog = false
                            MainActivity.setExpectingExternalActivity(true)
                            exportLauncher.launch("aipospm_backup.bin")
                        }
                    },
                    enabled = passwordsMatch && !isBackupInProgress
                ) {
                    Text(stringResource(R.string.txt_save_to_file_c8294a))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSetBackupPasswordDialog = false
                        backupPasswordToSet = ""
                        confirmBackupPasswordToSet = ""
                    },
                    enabled = !isBackupInProgress
                ) {
                    Text(stringResource(R.string.txt_cancel_77dfd2))
                }
            }
        )
    }

    // Enter Backup Password Dialog (for import)
    if (showEnterBackupPasswordDialog) {
        AlertDialog(
            onDismissRequest = { if (!isBackupInProgress) showEnterBackupPasswordDialog = false },
            title = { Text(stringResource(R.string.txt_enter_backup_password_4b9aa1)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.ui_enter_the_password_that_was_used_to__3bd9a0),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = backupPasswordToEnter,
                        onValueChange = { backupPasswordToEnter = it },
                        label = { Text(stringResource(R.string.txt_backup_password_f6bc21)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (backupPasswordToEnterVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { backupPasswordToEnterVisible = !backupPasswordToEnterVisible }) {
                                Icon(
                                    imageVector = if (backupPasswordToEnterVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (backupPasswordToEnterVisible) stringResource(R.string.ui_hide_password_e40123) else stringResource(R.string.ui_show_password_044b85)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val uri = importUri
                        if (uri != null && backupPasswordToEnter.isNotEmpty()) {
                            isBackupInProgress = true
                            showEnterBackupPasswordDialog = false
                            val pwd = backupPasswordToEnter
                            backupPasswordToEnter = ""
                            passwordViewModel.importBackup(
                                uri = uri,
                                password = pwd,
                                onSuccess = {
                                    isBackupInProgress = false
                                    importUriString = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.ui_database_restored_successfully_5405b0))
                                    }
                                },
                                onError = { error ->
                                    isBackupInProgress = false
                                    importUriString = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.ui_restore_failed_262b74, error))
                                    }
                                }
                            )
                        }
                    },
                    enabled = backupPasswordToEnter.isNotEmpty() && !isBackupInProgress
                ) {
                    Text(stringResource(R.string.txt_restore_3cbe6d))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEnterBackupPasswordDialog = false
                        importUriString = null
                        backupPasswordToEnter = ""
                    },
                    enabled = !isBackupInProgress
                ) {
                    Text(stringResource(R.string.txt_cancel_77dfd2))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.txt_settings_c7f73b), fontWeight = FontWeight.Bold) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Security Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ui_security_f25ce1),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (canUseBiometric) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.ui_biometric_unlock_d9168c),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.ui_use_fingerprint_or_face_to_unlock_th_686d9b),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { authViewModel.setBiometricEnabled(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Auto lock timeout
            var timeoutMenuExpanded by remember { mutableStateOf(false) }
            val timeoutOptions = listOf(
                Pair("Immediately", 0),
                Pair(stringResource(R.string.auto_lock_one), 1),
                Pair(stringResource(R.string.auto_lock_five), 5),
                Pair(stringResource(R.string.auto_lock_ten), 10),
                Pair("Never", -1)
            )
            val currentTimeoutLabel = timeoutOptions.firstOrNull { it.second == authState.autoLockTimeout }?.first ?: "Never"

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.ui_auto_lock_timeout_d3ba9b),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.ui_lock_app_after_backgrounding_5989ac),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        TextButton(onClick = { timeoutMenuExpanded = true }) {
                            Text(currentTimeoutLabel)
                        }
                        DropdownMenu(
                            expanded = timeoutMenuExpanded,
                            onDismissRequest = { timeoutMenuExpanded = false }
                        ) {
                            timeoutOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.first) },
                                    onClick = {
                                        authViewModel.setAutoLockTimeout(option.second)
                                        timeoutMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Screen Privacy (FLAG_SECURE)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.ui_screen_privacy_ebfaf4),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.ui_block_screenshots_and_hide_content_i_d6600c),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = authState.isScreenSecurityEnabled,
                        onCheckedChange = { authViewModel.setScreenSecurity(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Android Autofill Service
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                                    data = android.net.Uri.parse("package:com.aipos.aipospm")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                try {
                                    val fallback = Intent(android.provider.Settings.ACTION_SETTINGS)
                                    context.startActivity(fallback)
                                } catch (_: Exception) {}
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.ui_autofill_requires_android_8_0_or_hig_03a52d))
                            }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.ui_autofill_service_db7d4a),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isAutofillEnabled) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecurityGreen.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.ui_active_a733b8),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SecurityGreen
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(WarningAmber.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.ui_disabled_f4f447),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningAmber
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isAutofillEnabled)
                                stringResource(R.string.autofill_ready)
                            else
                                stringResource(R.string.autofill_enable),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.txt_configure_autofill_0141f5),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Change password
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { showChangePassword = !showChangePassword },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.ui_change_master_password_78960e),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.ui_update_your_master_password_c68991),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (showChangePassword) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text(stringResource(R.string.txt_current_password_4d5967)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (currentPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    currentPasswordVisible = !currentPasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(stringResource(R.string.txt_new_password_4894cb)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    newPasswordVisible = !newPasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text(stringResource(R.string.txt_confirm_new_password_68dcd7)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val changePasswordBtnInteractionSource = remember { MutableInteractionSource() }
                        Button(
                            onClick = {
                                val success = authViewModel.changeMasterPassword(
                                    currentPassword, newPassword, confirmNewPassword
                                )
                                if (success) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.ui_password_changed_successfully_e12081))
                                    }
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmNewPassword = ""
                                    showChangePassword = false
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.ui_failed_to_change_password_2398d6))
                                    }
                                }
                            },
                            interactionSource = changePasswordBtnInteractionSource,
                            modifier = Modifier.fillMaxWidth().pressScale(changePasswordBtnInteractionSource),
                            shape = RoundedCornerShape(12.dp),
                            enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmNewPassword.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.txt_change_password_49289d))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { showViewKeyPasswordDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.ui_emergency_recovery_key_660854),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.ui_view_or_copy_your_emergency_recovery_9f3b48),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // General Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ui_general_9239ee),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Manage Categories button card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onNavigateToManageCategories),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.ui_manage_categories_9a3491),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.ui_create_and_organize_custom_folders_6525bc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trash / Recently Deleted button card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onNavigateToTrash),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.ui_trash_e3bf62),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            if (totalDeletedCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DangerRed.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.ui_message_f8fb74, totalDeletedCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DangerRed
                                    )
                                }
                            }
                        }
                        Text(
                            text = stringResource(R.string.ui_restore_or_permanently_delete_items__0a49f0),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Backup & Restore Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ui_backup_restore_3bdd40),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.ui_encrypted_local_backups_ab4aab),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.ui_export_your_database_to_a_password_e_322f7e),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val exportBtnInteractionSource = remember { MutableInteractionSource() }
                    val importBtnInteractionSource = remember { MutableInteractionSource() }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                backupPasswordToSet = ""
                                confirmBackupPasswordToSet = ""
                                backupPasswordVisible = false
                                confirmBackupPasswordVisible = false
                                showSetBackupPasswordDialog = true
                            },
                            interactionSource = exportBtnInteractionSource,
                            modifier = Modifier.weight(1f).pressScale(exportBtnInteractionSource),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isBackupInProgress
                        ) {
                            Text(stringResource(if (isBackupInProgress) R.string.txt_working else R.string.txt_export_backup))
                        }
                        OutlinedButton(
                            onClick = {
                                MainActivity.setExpectingExternalActivity(true)
                                importLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                            },
                            interactionSource = importBtnInteractionSource,
                            modifier = Modifier.weight(1f).pressScale(importBtnInteractionSource),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isBackupInProgress
                        ) {
                            Text(stringResource(if (isBackupInProgress) R.string.txt_working else R.string.txt_import_backup))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.ui_import_third_party_vaults_5ec2c2),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.ui_import_credentials_from_bitwarden_ke_2d3605),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val csvImportBtnInteractionSource = remember { MutableInteractionSource() }
                    Button(
                        onClick = {
                            MainActivity.setExpectingExternalActivity(true)
                            csvImportLauncher.launch(arrayOf("text/comma-separated-values", "text/csv", "application/csv", "*/*"))
                        },
                        interactionSource = csvImportBtnInteractionSource,
                        modifier = Modifier.fillMaxWidth().pressScale(csvImportBtnInteractionSource),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.txt_select_and_import_csv_0376ef))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ui_about_6b21fb),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(stringResource(R.string.about_app_name), stringResource(R.string.app_name))
                    InfoRow("Version", "v1.6.1")
                    InfoRow(stringResource(R.string.about_developer), stringResource(R.string.about_author))
                    InfoRow(stringResource(R.string.about_security), stringResource(R.string.about_security_encryption))
                    InfoRow(stringResource(R.string.about_storage), stringResource(R.string.about_offline))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showViewKeyPasswordDialog) {
        var verifyPasswordInput by remember { mutableStateOf("") }
        var verifyPasswordVisible by remember { mutableStateOf(false) }
        var verifyError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { 
                showViewKeyPasswordDialog = false 
                verifyPasswordInput = ""
                verifyError = null
            },
            title = { Text(stringResource(R.string.txt_confirm_master_password_02d580)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.ui_to_view_your_emergency_recovery_key__07c9e1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = verifyPasswordInput,
                        onValueChange = { verifyPasswordInput = it },
                        label = { Text(stringResource(R.string.txt_master_password_5654ce)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (verifyPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verifyPasswordVisible = !verifyPasswordVisible }) {
                                Icon(
                                    imageVector = if (verifyPasswordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (verifyError != null) {
                        Text(
                            text = verifyError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val isCorrect = authViewModel.verifyMasterPassword(verifyPasswordInput)
                        if (isCorrect) {
                            val key = authViewModel.getDecryptedRecoveryKey()
                            if (key != null) {
                                decryptedRecoveryKey = key
                                showRecoveryKeyDialog = true
                                showViewKeyPasswordDialog = false
                                verifyPasswordInput = ""
                                verifyError = null
                            } else {
                                verifyError = context.getString(R.string.auth_recovery_not_found)
                            }
                        } else {
                            verifyError = context.getString(R.string.auth_incorrect_master)
                        }
                    },
                    enabled = verifyPasswordInput.isNotBlank()
                ) {
                    Text(stringResource(R.string.txt_confirm_04a212))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showViewKeyPasswordDialog = false
                        verifyPasswordInput = ""
                        verifyError = null
                    }
                ) {
                    Text(stringResource(R.string.txt_cancel_77dfd2))
                }
            }
        )
    }

    if (showRecoveryKeyDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryKeyDialog = false },
            title = { Text(stringResource(R.string.txt_emergency_recovery_key_660854)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.ui_write_down_this_key_or_save_it_in_a__985501),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Text(
                            text = decryptedRecoveryKey,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ClipboardHelper.copyAndScheduleClear(context, context.getString(R.string.txt_recovery_key_780fbb), decryptedRecoveryKey)
                    }
                ) {
                    Text(stringResource(R.string.txt_copy_af74f7))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryKeyDialog = false }) {
                    Text(stringResource(R.string.txt_close_bbfa77))
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
