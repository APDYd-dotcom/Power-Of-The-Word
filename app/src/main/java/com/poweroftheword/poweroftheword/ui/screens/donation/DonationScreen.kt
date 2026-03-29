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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(onBackClick: () -> Unit) {

    var selectedAmount by remember { mutableStateOf<Int?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var isMonthly by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("Credit/Debit Card") }

    val gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFF3D74F6), Color(0xFF8E44AD))
    )

    Scaffold(
        containerColor = Color(0xFF121826),
        topBar = {
            TopAppBar(
                title = { Text("Donate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121826)
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
                                "Support the Ministry",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Your generous donation helps us spread the Gospel and support community outreach.",
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
                    StatCard("5000+", "Lives Touched", Color(0xFF3D74F6), Modifier.weight(1f))
                    StatCard("100+", "Countries", Color(0xFF9B59B6), Modifier.weight(1f))
                    StatCard("24/7", "Broadcast", Color(0xFFE84393), Modifier.weight(1f))
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // AMOUNT
            item {
                CardShape {
                    Column {

                        Text("Select Amount", fontWeight = FontWeight.Bold)

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
                                        modifier = Modifier.weight(1f)
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
                            placeholder = { Text("Enter custom amount") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // MONTHLY
            item {
                CardShape {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isMonthly,
                            onCheckedChange = { isMonthly = it }
                        )
                        Column {
                            Text("Make this monthly")
                            Text(
                                "Recurring donation",
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
                CardShape {
                    Column {
                        Text("Payment Method", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(12.dp))

                        listOf(
                            "Credit/Debit Card",
                            "Bank Transfer",
                            "Lumicash",
                            "Ecocash"
                        ).forEach {
                            PaymentOption(it, selectedPayment) {
                                selectedPayment = it
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

// 🔥 DYNAMIC PAYMENT DETAILS
            item {
                when (selectedPayment) {

                    "Bank Transfer" -> BankDetailsCard()

                    "Lumicash" -> LumicashCard()

                    "Ecocash" -> EcocashCard()

                    "Bankobu" -> BankobuCard()

                    "Ihela" -> IhelaCard()
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
                        if (finalAmount == null) "Select Amount"
                        else "Donate $$finalAmount",
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
fun CardShape(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2635)),
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
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (selected) Color(0xFF3D74F6)
                else Color(0xFF2A3142),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$$amount",
            color = if (selected) Color.White else Color.LightGray,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentOption(
    title: String,
    selected: String,
    onClick: (String) -> Unit
) {
    val isSelected = title == selected

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF2D4FFF)
                else Color(0xFF2A3142),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick(title) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CreditCard, null, tint = Color.White)
        Spacer(Modifier.width(10.dp))
        Text(title, modifier = Modifier.weight(1f), color = Color.White)

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
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2635)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}



@Composable
fun BankDetailsCard() {
    GradientBorderCard(borderColor = Color(0xFF3D74F6)) {
        Column {
            Text("Bank Transfer Details", color = Color.White, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            InfoRow("Bank Name:", "First National Bank")
            InfoRow("Account Name:", "Power of the Word Ministry")
            InfoRow("Account Number:", "1234567890")
            InfoRow("SWIFT Code:", "FNBXXX123")
        }
    }
}




@Composable
fun LumicashCard() {
    GradientBorderCard(borderColor = Color(0xFFFF6A00)) {

        Column {
            PaymentHeader("Lumicash Payment", Color(0xFFFF6A00))

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 79 XX XX XX", "Lumicash Number")
            InfoBox("Power of the Word", "Account Name")

            InstructionBox(
                steps = listOf(
                    "Dial *155# on your phone",
                    "Select Send Money",
                    "Enter number above",
                    "Enter amount and confirm",
                    "Keep SMS receipt"
                ),
                color = Color(0xFF5A2A1A)
            )
        }
    }
}



@Composable
fun EcocashCard() {
    GradientBorderCard(borderColor = Color(0xFF00C853)) {

        Column {
            PaymentHeader("Ecocash Payment", Color(0xFF00C853))

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 71 XX XX XX", "Ecocash Number")
            InfoBox("Power of the Word", "Account Name")

            InstructionBox(
                listOf(
                    "Dial *144#",
                    "Select Send Money",
                    "Enter number",
                    "Enter PIN",
                    "Save confirmation SMS"
                ),
                color = Color(0xFF0F3D2E)
            )
        }
    }
}


@Composable
fun BankobuCard() {
    GradientBorderCard(borderColor = Color(0xFF2962FF)) {

        Column {
            PaymentHeader("Bankobu Payment", Color(0xFF2962FF))

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 76 XX XX XX", "Bankobu Account")
            InfoBox("Power of the Word Ministry", "Account Name")

            InstructionBox(
                listOf(
                    "Dial *365#",
                    "Select Transfer",
                    "Enter account number",
                    "Enter amount",
                    "Confirm PIN"
                ),
                color = Color(0xFF1A2F5A)
            )
        }
    }
}


@Composable
fun IhelaCard() {
    GradientBorderCard(borderColor = Color(0xFF8E44AD)) {

        Column {
            PaymentHeader("Ihela Payment", Color(0xFF8E44AD))

            Spacer(Modifier.height(12.dp))

            InfoBox("+257 22 XX XX XX", "Ihela Number")
            InfoBox("Power of the Word", "Account Name")

            InstructionBox(
                listOf(
                    "Dial *505#",
                    "Select Send Money",
                    "Enter number",
                    "Enter amount",
                    "Keep reference"
                ),
                color = Color(0xFF3A1F4F)
            )
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text(value, fontWeight = FontWeight.Medium, color = Color.White)
    }
}



@Composable
fun InfoBox(value: String, label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A3142), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(Modifier.height(10.dp))
}


@Composable
fun InstructionBox(steps: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A3142), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text("Instructions:", fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(Modifier.height(6.dp))

            steps.forEachIndexed { index, step ->
                Text(
                    "${index + 1}. $step",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}




@Composable
fun GradientBorderCard(
    borderColor: Color,
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
                .background(Color(0xFF1E2635), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}


@Composable
fun PaymentHeader(title: String, color: Color) {
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

        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
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
            Text("Instructions:", color = Color.White, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            steps.forEachIndexed { i, step ->
                Text(
                    "${i + 1}. $step",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}