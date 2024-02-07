package com.example.drawingapp

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.drawingapp.ui.theme.DrawingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           DrawingAppTheme() {
                // A surface container using the 'background' color from the theme
                DrawingVieww()
            }
        }
    }
}

@Composable
fun BottomBarView(){
    BottomAppBar(
        backgroundColor = Color.Green,
        content = {
            TextButton(onClick = { /* Handle action */ }) {
                Text("Action 1", color = Color.White)
            }
            TextButton(onClick = { /* Handle action */ }) {
                Text("Action 2", color = Color.White)
            }
        }
    )
}



@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun drawFunction(){
    var path by remember { mutableStateOf(Path()) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(it.x, it.y)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(it.x, it.y)

                    true
                }
                else -> false
            }
        }
    ) {
       /* withTransform({
            //scale(scaleX = 2f,2f)
            //rotate(degrees = 5F)
            //translate(left = size.width/5f,-300f)
        }){

        }*/
        drawPath(path,Color.Black)

    }
}

data class Line(
    val start:Offset,
    val end:Offset,
    val color:Color=Color.Black,
    val strokWidth: Dp = 1.dp
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingCanvas() {
    var lines=remember{ mutableStateListOf<Line>() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val line = Line(
                        start = change.position - dragAmount,
                        end = change.position
                    )
                    lines.add(line)

                }
            }
    ) {
        lines.forEach {
            line ->  drawLine(
              color= line.color,
              end = line.end,
              start = line.start,
              strokeWidth = line.strokWidth.toPx(),
              cap = StrokeCap.Round
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DrawingAppTheme {
        Greeting("Android")
    }
}