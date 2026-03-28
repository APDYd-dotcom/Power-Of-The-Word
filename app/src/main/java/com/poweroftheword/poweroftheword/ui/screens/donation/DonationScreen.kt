package com.poweroftheword.poweroftheword.ui.screens.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(onBackClick: () -> Unit) {

    var selectedAmount by remember { mutableStateOf<Int?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var isMonthly by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("Card") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Donation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

        }
    ) { innerPadding ->
    LazyColumn(
        modifier = Modifier.padding(innerPadding)
            .padding(16.dp)
    ) {
       item {
           // HERO CARD
           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .height(140.dp)
                   .background(
                       Brush.horizontalGradient(
                           listOf(Color(0xFF6A5AE0), Color(0xFFE9408E))
                       ),
                       shape = RoundedCornerShape(16.dp)
                   )
                   .padding(16.dp)
           ) {
               Column {
                   Text("Support the Ministry", color = Color.White, fontWeight = FontWeight.Bold)
                   Spacer(modifier = Modifier.height(6.dp))
                   Text(
                       "Your generous donation helps spread the Gospel...",
                       color = Color.White.copy(0.9f)
                   )
               }
           }
       }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
            StatCard("5000+", "Lives Touched")
            StatCard("100+", "Countries")
            StatCard("24/7", "Broadcasting")
        }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Select Amount", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    val amounts = listOf(10, 25, 50, 100, 250, 500)

                    amounts.chunked(3).forEach { row ->
                        Row {
                            row.forEach { amount ->
                                AmountChip(
                                    amount,
                                    selected = selectedAmount == amount
                                ) {
                                    selectedAmount = amount
                                    customAmount = ""
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BasicTextField(
                        value = customAmount,
                        onValueChange = {
                            customAmount = it
                            selectedAmount = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        decorationBox = {
                            if (customAmount.isEmpty()) {
                                Text("Enter amount", color = Color.Gray)
                            }
                            it()
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isMonthly, onCheckedChange = { isMonthly = it })
                Text("Make this monthly")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Payment Method", fontWeight = FontWeight.Bold)

                    PaymentItem("Card", Icons.Default.CreditCard, selectedPayment) {
                        selectedPayment = "Card"
                    }

                    PaymentItem("Mobile", Icons.Default.PhoneAndroid, selectedPayment) {
                        selectedPayment = "Mobile"
                    }

                    PaymentItem("Bank", Icons.Default.AccountBalance, selectedPayment) {
                        selectedPayment = "Bank"
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Your Donation Supports", fontWeight = FontWeight.Bold)

                    listOf(
                        "📻 Daily radio broadcasts",
                        "🎥 Video production",
                        "❤️ Community outreach",
                        "📖 Bible study resources"
                    ).forEach {
                        Text(it, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {

        }
    }

//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//
//            // 🔁 MONTHLY
//
//            // 💳 PAYMENT
//
//            // 📌 SUPPORT LIST
//
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // 🔘 BUTTON
//
//        }
    }
}

private fun LazyItemScope.Button(
    onClick: () -> Unit,
    modifier: Modifier,
    colors: Color,
    shape: RoundedCornerShape,
    content: () -> Unit,
    function: @Composable () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = colors,
        shape = shape,
        content = content
    ) {
        Text("Contunue")
    }
}


@Composable
fun StatCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}



@Composable
fun PaymentItem(
    title: String,
    icon: ImageVector,
    selected: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(10.dp))

        Text(title, modifier = Modifier.weight(1f))

        if (selected == title) {
            Icon(Icons.Default.Check, contentDescription = null)
        }
    }
}