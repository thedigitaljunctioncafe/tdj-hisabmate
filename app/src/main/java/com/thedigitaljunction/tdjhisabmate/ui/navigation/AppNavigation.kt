package com.thedigitaljunction.tdjhisabmate.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.thedigitaljunction.tdjhisabmate.ui.screens.AboutPrivacyScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.AccountsScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.AddTransactionScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.BackupRestoreScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.BudgetsScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.FeedbackScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.GoalsScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.HisabGuardScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.HomeScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.MoreHubScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.OnboardingScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.PinLockScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.RecurringScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.ReportsScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.SecurityScreen
import com.thedigitaljunction.tdjhisabmate.ui.screens.TransactionsScreen
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

sealed class Screen(val route: String, val title: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Transactions : Screen("transactions", "Transactions", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    data object Guard : Screen("guard", "Hisab Guard", Icons.Filled.Shield, Icons.Outlined.Shield)
    data object More : Screen("more", "More", Icons.Filled.Widgets, Icons.Outlined.Widgets)
}

@Composable
fun MainApp(
    viewModel: HisabViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    var isSessionUnlocked by remember { mutableStateOf(!preferences.isPinEnabled) }

    // If PIN lock is active and not unlocked, show PIN lock
    if (preferences.isPinEnabled && !isSessionUnlocked) {
        PinLockScreen(
            correctPin = preferences.pinCode,
            onUnlocked = { isSessionUnlocked = true }
        )
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Guard,
        Screen.More
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.filledIcon else screen.outlinedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            modifier = Modifier.testTag("nav_item_${screen.route}"),
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && currentRoute != Screen.Transactions.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_transaction") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("quick_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (preferences.isOnboarded) Screen.Home.route else "onboarding",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Onboarding
            composable("onboarding") {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinish = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { presetCat, presetAmt ->
                        val url = if (presetCat != null && presetAmt != null) {
                            "add_transaction?category=$presetCat&amount=$presetAmt"
                        } else if (presetCat != null) {
                            "add_transaction?category=$presetCat"
                        } else {
                            "add_transaction"
                        }
                        navController.navigate(url)
                    },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onNavigateToGuard = { navController.navigate(Screen.Guard.route) },
                    onNavigateToBudgets = { navController.navigate("budgets") }
                )
            }

            // Transactions Screen
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    viewModel = viewModel,
                    onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                    onNavigateToEditTransaction = { txnId -> navController.navigate("add_transaction?editId=$txnId") }
                )
            }

            // Hisab Guard Screen
            composable(Screen.Guard.route) {
                HisabGuardScreen(
                    viewModel = viewModel,
                    onNavigateToAddWithPreset = { cat, amt ->
                        val url = if (cat != null && amt != null) {
                            "add_transaction?category=$cat&amount=$amt"
                        } else if (cat != null) {
                            "add_transaction?category=$cat"
                        } else {
                            "add_transaction"
                        }
                        navController.navigate(url)
                    },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) }
                )
            }

            // More Hub Screen
            composable(Screen.More.route) {
                MoreHubScreen(
                    viewModel = viewModel,
                    onNavigateToAccounts = { navController.navigate("accounts") },
                    onNavigateToBudgets = { navController.navigate("budgets") },
                    onNavigateToReports = { navController.navigate("reports") },
                    onNavigateToRecurring = { navController.navigate("recurring") },
                    onNavigateToGoals = { navController.navigate("goals") },
                    onNavigateToBackup = { navController.navigate("backup") },
                    onNavigateToSecurity = { navController.navigate("security") },
                    onNavigateToFeedback = { navController.navigate("feedback") },
                    onNavigateToAbout = { navController.navigate("about") }
                )
            }

            // Add Transaction Screen
            composable(
                route = "add_transaction?category={category}&amount={amount}&editId={editId}",
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("amount") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("editId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val categoryArg = backStackEntry.arguments?.getString("category")
                val amountArg = backStackEntry.arguments?.getString("amount")?.toDoubleOrNull()
                val editIdArg = backStackEntry.arguments?.getString("editId")?.toLongOrNull()

                AddTransactionScreen(
                    viewModel = viewModel,
                    presetCategory = categoryArg,
                    presetAmount = amountArg,
                    editTransactionId = editIdArg,
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            // Accounts Screen
            composable("accounts") {
                AccountsScreen(viewModel = viewModel)
            }

            // Budgets Screen
            composable("budgets") {
                BudgetsScreen(viewModel = viewModel)
            }

            // Reports Screen
            composable("reports") {
                ReportsScreen(viewModel = viewModel)
            }

            // Recurring Screen
            composable("recurring") {
                RecurringScreen(viewModel = viewModel)
            }

            // Goals Screen
            composable("goals") {
                GoalsScreen(viewModel = viewModel)
            }

            // Backup & Restore Screen
            composable("backup") {
                BackupRestoreScreen(viewModel = viewModel)
            }

            // Security Screen
            composable("security") {
                SecurityScreen(viewModel = viewModel)
            }

            // Feedback Screen
            composable("feedback") {
                FeedbackScreen(viewModel = viewModel)
            }

            // About Screen
            composable("about") {
                AboutPrivacyScreen()
            }
        }
    }
}
