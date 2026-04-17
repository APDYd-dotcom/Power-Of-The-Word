package com.poweroftheword.poweroftheword.ui.screens.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import com.poweroftheword.poweroftheword.util.localizedString
import com.poweroftheword.poweroftheword.util.truncate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val userDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isDark = userDarkMode ?: isSystemInDarkTheme()

    var selectedAmount by remember { mutableStateOf<Int?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var isMonthly by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("Credit/Debit Card") }

    val gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFF3D74F6), Color(0xFF8E44AD))
    )

    Scaffold(
        containerColor = if (isDark) Color(0xFF121826) else MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(localizedString(R.string.donate), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF121826) else MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // HERO
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6A5AE0), Color(0xFFE9408E))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, null, tint = Color.White)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                localizedString(R.string.support_ministry),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            localizedString(R.string.donation_impact),
                            color = Color.White.copy(0.9f)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // STATS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("5000+", localizedString(R.string.lives_touched).truncate(12), Color(0xFF3D74F6), Modifier.weight(1f), isDark)
                    StatCard("100+", localizedString(R.string.countries).truncate(12), Color(0xFF9B59B6), Modifier.weight(1f), isDark)
                    StatCard("24/7", localizedString(R.string.broadcast).truncate(12), Color(0xFFE84393), Modifier.weight(1f), isDark)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // AMOUNT
            item {
                CardShape(isDark) {
                    Column {

                        Text(localizedString(R.string.select_amount), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)

                        Spacer(Modifier.height(12.dp))

                        val amounts = listOf(10, 25, 50, 100, 250, 500)

                        amounts.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { amount ->
                                    AmountChip(
                                        amount = amount,
                                        selected = selectedAmount == amount,
                                        modifier = Modifier.weight(1f),
                                        isDark = isDark
                                    ) {
                                        selectedAmount = amount
                                        customAmount = ""
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customAmount,
                            onValueChange = {
                                customAmount = it
                                selectedAmount = null
                            },
                            placeholder = { Text(localizedString(R.string.enter_custom_amount)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // MONTHLY
            item {
                CardShape(isDark) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isMonthly,
                            onCheckedChange = { isMonthly = it }
                        )
                        Column {
                            Text(localizedString(R.string.make_monthly), color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)
                            Text(
                                localizedString(R.string.recurring_donation),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // PAYMENT
            item {
                CardShape(isDark) {
                    Column {
                        Text(localizedString(R.string.payment_method), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)

                        Spacer(Modifier.height(12.dp))

                        listOf(
                            localizedString(R.string.credit_debit_card) to "Credit/Debit Card",
                            localizedString(R.string.bank_transfer) to "Bank Transfer",
                            localizedString(R.string.lumicash) to "Lumicash",
                            localizedString(R.string.ecocash) to "Ecocash",
                            localizedString(R.string.bankobu_payment) to "Bankobu",
                            localizedString(R.string.ihela_payment) to "Ihela"
                        ).forEach { (display, key) ->
                            PaymentOption(display, selectedPayment == key, isDark) {
                                selectedPayment = key
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

// 🔥 DYNAMIC PAYMENT DETAILS
            item {
                when (selectedPayment) {

                    "Bank Transfer" -> BankDetailsCard(isDark)

                    "Lumicash" -> LumicashCard(isDark)

                    "Ecocash" -> EcocashCard(isDark)

                    "Bankobu" -> BankobuCard(isDark)

                    "Ihela" -> IhelaCard(isDark)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }


            // BUTTON
            item {
                val finalAmount = selectedAmount ?: customAmount.toIntOrNull()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gradientButton, RoundedCornerShape(14.dp))
                        .clickable { }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (finalAmount == null) localizedString(R.string.select_amount)
                        else localizedString(R.string.donate_with_amount, finalAmount.toString()),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CardShape(isDark: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun AmountChip(
    amount: Int,
    selected: Boolean,
    modifier: Modifier,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (selected) Color(0xFF3D74F6)
                else if (isDark) Color(0xFF2A3142) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$$amount",
            color = if (selected) Color.White else if (isDark) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentOption(
    title: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF2D4FFF)
                else if (isDark) Color(0xFF2A3142) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CreditCard, null, tint = if (isSelected || isDark) Color.White else MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(10.dp))
        Text(title, modifier = Modifier.weight(1f), color = if (isSelected || isDark) Color.White else MaterialTheme.colorScheme.onSurface)

        if (isSelected) {
            Icon(Icons.Default.Check, null, tint = Color.White)
        }
    }

    Spacer(Modifier.height(8.dp))
}

@Composable
fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier,
    isDark: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
fun BankDetailsCard(isDark: Boolean) {
    GradientBorderCard(borderColor = Color(0xFF3D74F6), isDark = isDark) {
        Column {
            Text(localizedString(R.string.bank_transfer_details), color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            InfoRow(localizedString(R.string.bank_name), "First National Bank", isDark)
            InfoRow(localizedString(R.string.account_name), "Power of the Word Ministry", isDark)
            InfoRow(localizedString(R.string.account_number), "1234567890", isDark)
            InfoRow(localizedString(R.string.swift_code), "FNBXXX123", isDark)
        }
    }
}




@Composable
fun LumicashCard(isDark: Boolean) {
    GradientBorderCard(borderColor = Color(0xFFFF6A00), isDark = isDark) {

        Column {
            PaymentHeader(localizedString(R.string.lumicash_payment), Color(0xFFFF6A00), isDark)

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 79 XX XX XX", localizedString(R.string.lumicash_number), isDark)
            InfoBox("Power of the Word", localizedString(R.string.account_name), isDark)

            InstructionBox(
                steps = listOf(
                    "Dial *155# on your phone",
                    "Select Send Money",
                    "Enter number above",
                    "Enter amount and confirm",
                    localizedString(R.string.keep_sms_receipt)
                ),
                color = if (isDark) Color(0xFF5A2A1A) else Color(0xFFFFEBDD)
            )
        }
    }
}



@Composable
fun EcocashCard(isDark: Boolean) {
    GradientBorderCard(borderColor = Color(0xFF00C853), isDark = isDark) {

        Column {
            PaymentHeader(localizedString(R.string.ecocash_payment), Color(0xFF00C853), isDark)

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 71 XX XX XX", localizedString(R.string.ecocash_number), isDark)
            InfoBox("Power of the Word", localizedString(R.string.account_name), isDark)

            InstructionBox(
                listOf(
                    "Dial *144#",
                    "Select Send Money",
                    "Enter number",
                    "Enter PIN",
                    localizedString(R.string.save_confirmation_sms)
                ),
                color = if (isDark) Color(0xFF0F3D2E) else Color(0xFFE8F5E9)
            )
        }
    }
}


@Composable
fun BankobuCard(isDark: Boolean) {
    GradientBorderCard(borderColor = Color(0xFF2962FF), isDark = isDark) {

        Column {
            PaymentHeader(localizedString(R.string.bankobu_payment), Color(0xFF2962FF), isDark)

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 76 XX XX XX", localizedString(R.string.bankobu_account), isDark)
            InfoBox("Power of the Word Ministry", localizedString(R.string.account_name), isDark)

            InstructionBox(
                listOf(
                    "Dial *365#",
                    "Select Transfer",
                    "Enter account number",
                    "Enter amount",
                    "Confirm PIN"
                ),
                color = if (isDark) Color(0xFF1A2F5A) else Color(0xFFE3F2FD)
            )
        }
    }
}


@Composable
fun IhelaCard(isDark: Boolean) {
    GradientBorderCard(borderColor = Color(0xFF8E44AD), isDark = isDark) {

        Column {
            PaymentHeader(localizedString(R.string.ihela_payment), Color(0xFF8E44AD), isDark)

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 22 XX XX XX", localizedString(R.string.ihela_number), isDark)
            InfoBox("Power of the Word", localizedString(R.string.account_name), isDark)

            InstructionBox(
                listOf(
                    "Dial *505#",
                    "Select Send Money",
                    "Enter number",
                    "Enter amount",
                    localizedString(R.string.keep_reference)
                ),
                color = if (isDark) Color(0xFF3A1F4F) else Color(0xFFF3E5F5)
            )
        }
    }
}


@Composable
fun InfoRow(label: String, value: String, isDark: Boolean) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text(value, fontWeight = FontWeight.Medium, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)
    }
}



@Composable
fun InfoBox(value: String, label: String, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF2A3142) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(Modifier.height(10.dp))
}


@Composable
fun InstructionBox(steps: List<String>, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF2A3142) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(localizedString(R.string.instructions), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)

            Spacer(Modifier.height(6.dp))

            steps.forEachIndexed { index, step ->
                Text(
                    "${index + 1}. $step",
                    fontSize = 12.sp,
                    color = if (isDark) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}




@Composable
fun GradientBorderCard(
    borderColor: Color,
    isDark: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(borderColor, borderColor.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(1.5.dp) // border thickness
    ) {
        Column(
            modifier = Modifier
                .background(if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}


@Composable
fun PaymentHeader(title: String, color: Color, isDark: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CreditCard, null, tint = Color.White)
        }

        Spacer(Modifier.width(12.dp))

        Text(title, color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun InstructionBox(
    steps: List<String>,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(localizedString(R.string.instructions), color = if (color.luminance() < 0.5) Color.White else Color.Black, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            steps.forEachIndexed { i, step ->
                Text(
                    "${i + 1}. $step",
                    color = if (color.luminance() < 0.5) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
