package com.example.mobiledisco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.ui.theme.MobileDiscoTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MobileDiscoTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    MobileDiscoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}


@Composable
fun MobileDiscoScreen(
    modifier: Modifier = Modifier
) {

    Column(

        modifier = modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {


        Text(

            text = "Mobile Disco",

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        Text(
            text = "Nenhuma música selecionada"
        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        Button(
            onClick = {

            }
        ) {

            Text("▶ Play")

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Row {


            Button(
                onClick = {

                }
            ) {

                Text("⏮")

            }


            Spacer(
                modifier = Modifier.width(30.dp)
            )


            Button(
                onClick = {

                }
            ) {

                Text("⏭")

            }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun MobileDiscoPreview() {

    MobileDiscoTheme {

        MobileDiscoScreen()

    }
}