package com.example.drawingapp

import android.content.res.Resources
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DrawingVieww (){
    var HistoryLines= remember { mutableStateListOf<Myline>() }
    var Lines = remember { mutableStateListOf<Myline>() }
    var color by remember { mutableStateOf(Color.Black) }
    var barSize by remember { mutableStateOf(370.dp) }
    var block by  remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
       DrawingCanvas(lines =Lines , color = color, weight =3f, addLine = {line -> Lines.add(line) ; HistoryLines.add(line)})
       BottomBar(weight = 1f, barSize=barSize, changeSize = { s-> barSize+=s  } ,selectedColor = color, onColorSelected = {c->color=c}, undo = { if(Lines.size>3){Lines.removeAt(Lines.size-1)} },redo={ if(Lines.size<HistoryLines.size){ Lines.add(HistoryLines.get(Lines.size)) } } )
    }
}


data class Myline(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokWidth: Dp = 1.dp
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColumnScope.DrawingCanvas(lines:MutableList<Myline>, color: Color, weight:Float,addLine:(Myline)->Unit){
    var myColor by remember { mutableStateOf(color) }
    DisposableEffect(color) {
        myColor = color

        onDispose {
            // Cleanup if needed when the composable is removed from the composition
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .weight(weight)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val line = Myline(
                        start = change.position - dragAmount,
                        end = change.position,
                        color = myColor
                    )

                    addLine(line)
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

@Composable
fun ColorSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = mutableListOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Magenta,
        Color.Yellow,
        Color.Blue,
        Color.Magenta,
        Color.Yellow
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        colors.forEach { color ->
            ColorCircle(
                color = color,
                isSelected = color == selectedColor,
                onClick = {
                    onColorSelected(color)
                }
            )
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(color, CircleShape)
            .clickable(onClick = onClick)
            .padding(4.dp)
            .border(
                width = if (isSelected) 4.dp else 0.dp,
                color = Color.Black,
                shape = CircleShape
            )
    )
}

@Composable
fun CircularThumb() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(Color.Blue, CircleShape)
    )
}

@Composable
fun ResizableBar(barSize: Dp,changeSize: (Dp) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically

    ) {
        Box(
            modifier = Modifier
                .width(barSize)
                .height(3.dp)
                .background(Color.Gray)
                .padding(12.dp)
        )
        Box(
            modifier = Modifier
                   .size(20.dp)
                   .background(Color.Gray, shape = CircleShape)
                   .pointerInput(Unit) {
                     detectTransformGestures { _, pan, _, _ ->
                             changeSize(pan.x.dp)
                       }
                   }
             )
      }
}


@Composable
fun ColumnScope.BottomBar(
    weight: Float,
    barSize:Dp,
    changeSize:(Dp)->Unit,
    undo:()->Unit,
    redo:()->Unit,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit){
    var block by remember { mutableStateOf("") }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .weight(weight)
            .background(color = Color.White)
    ) {
      Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.SpaceAround
      ) {
         when(block){
            "color" ->{
                ColorSelector(
                    selectedColor=selectedColor,
                    onColorSelected=onColorSelected
                )
            }
             "width" ->{
                 ResizableBar(barSize=barSize, changeSize = changeSize)
             }
             else -> false
         }
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
          ) {
              IconButton(
                  onClick = { undo() },
                  modifier = Modifier.size(50.dp)
              ) {
                  Image(
                      painter = painterResource(id = R.drawable.ic_undo_name),
                      contentDescription = null,  // Set a meaningful content description if needed
                      modifier = Modifier.size(50.dp)
                  )
              }
              IconButton(
                  onClick = { redo() },
                  modifier = Modifier.size(50.dp)
              ) {
                  Image(
                      painter = painterResource(id = R.drawable.ic_redo_name),
                      contentDescription = null,  // Set a meaningful content description if needed
                      modifier = Modifier.size(50.dp)
                  )
              }
              IconButton(
                  onClick = { block="color" },
                  modifier = Modifier.size(48.dp)
              ) {
                  Image(
                      painter = painterResource(id = R.drawable.ic_color_name),
                      contentDescription = null,  // Set a meaningful content description if needed
                      modifier = Modifier.size(50.dp)
                  )
              }
              IconButton(
                  onClick = { block="width" },
                  modifier = Modifier.size(50.dp)
              ) {
                  Image(
                      painter = painterResource(id = R.drawable.ic_width_name),
                      contentDescription = null,  // Set a meaningful content description if needed
                      modifier = Modifier.size(50.dp)
                  )
              }
          }

      }
  }
}