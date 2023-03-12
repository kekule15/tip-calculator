package com.example.tipcaculator


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcaculator.components.CustomTextField
import com.example.tipcaculator.ui.theme.TipCaculatorTheme
import com.example.tipcaculator.utils.calculateTotalPerPerson
import com.example.tipcaculator.utils.calculateTotalTip
import com.example.tipcaculator.widgets.RoundedButtonWidget

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

// MY PROJECT ENTRY POINT
@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCaculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            content()
        }
    }
}

// TOP HEADER
@Composable
fun TopHeader(totalPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(size = 12.dp),
        color = Color(0xFF23454)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerson)
            Text(text = "Total per person", style = MaterialTheme.typography.h5)
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//MAIN CONTENT
@ExperimentalComposeUiApi
@Composable
fun MainContent() {
    BillForm()
}

//BILL FORM
@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier, onValueChanged: (String) -> Unit = {}) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val splitValue = remember {
        mutableStateOf(1)
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPersonState = remember {
        mutableStateOf(0.0)
    }

    Column() {
        TopHeader(totalPerson = totalPersonState.value)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(corner = CornerSize(12.dp)),
            border = BorderStroke(width = 0.4.dp, color = Color.Gray),
            elevation = 10.dp
        ) {
            Column {
                CustomTextField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValueChanged(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )

               if (validState){
                   Row(
                       modifier = Modifier
                           .padding(12.dp)
                           .fillMaxWidth(),
                       horizontalArrangement = Arrangement.SpaceBetween,

                       ) {
                       Text(
                           text = "Split",
                           modifier = Modifier.align(alignment = Alignment.CenterVertically),
                           style = MaterialTheme.typography.caption
                       )
                       Row() {
                           RoundedButtonWidget(

                               imageVector = Icons.Default.Remove, onClick = {
                                   if (splitValue.value != 1) {
                                       splitValue.value--;
                                   }
                                   totalPersonState.value = calculateTotalPerPerson(
                                       totalBill = totalBillState.value.toDouble(),
                                       tipPercentage = tipPercentage,
                                       splitBy = splitValue.value
                                   )
                               }
                           )
                           Text(
                               text = splitValue.value.toString(),
                               modifier = Modifier
                                   .padding(start = 10.dp, end = 10.dp)
                                   .align(alignment = Alignment.CenterVertically)
                           )
                           RoundedButtonWidget(

                               imageVector = Icons.Default.Add, onClick = {
                                   Log.d("Icon", "BillForm: Add")
                                   splitValue.value++;
                                   totalPersonState.value = calculateTotalPerPerson(
                                       totalBill = totalBillState.value.toDouble(),
                                       tipPercentage = tipPercentage,
                                       splitBy = splitValue.value
                                   )
                               }
                           )
                       }
                   }
                   Row(
                       modifier = Modifier
                           .padding(12.dp)
                           .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                   ) {
                       Text(
                           text = "Tip",
                           modifier = Modifier.align(alignment = Alignment.CenterVertically),
                           style = MaterialTheme.typography.caption
                       )
                       Text(
                           text = "$${tipAmountState.value}",
                           modifier = Modifier.align(alignment = Alignment.CenterVertically),
                           style = MaterialTheme.typography.caption
                       )
                   }

                   Column(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(12.dp),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally
                   ) {
                       Text(
                           text = "${tipPercentage}%",
                           style = MaterialTheme.typography.caption
                       )
                       Spacer(modifier = Modifier.height(15.dp))
                       Slider(value = sliderPositionState.value, onValueChange = { newValue ->
                           sliderPositionState.value = newValue
                           tipAmountState.value = calculateTotalTip(
                               totalBill = totalBillState.value.toDouble(),
                               tipPercentage = tipPercentage
                           )

                           totalPersonState.value = calculateTotalPerPerson(
                               totalBill = totalBillState.value.toDouble(),
                               tipPercentage = tipPercentage,
                               splitBy = splitValue.value
                           )
                       }, steps = 5)
                   }
               }else{
                   Box{}
               }
            }
        }
    }
}


@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MainContent()
    }
}