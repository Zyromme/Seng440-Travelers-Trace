package com.enz.ac.uclive.zba29.travelerstrace.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey

@Composable
fun JourneyCard(journey: Journey) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            val image: Painter = painterResource(id = journey.image)
            Image(
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = image,
                alignment = Alignment.CenterStart,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = journey.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    style = typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append(journey.date)
                        append(" | ")
                        append(journey.type)
                    },
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    style = typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.Bottom) {

                    Icon(
                        imageVector = Icons.Sharp.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp, 16.dp),
                        tint = Color.Red
                    )

                    Text(
                        text = buildString {
                            append(journey.totalDistance)
                            append(" m")
                        },
                        modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                        style = typography.bodyMedium,
                    )
                }
            }

        }
    }
}

@Composable
fun customShape() = object : Shape {
    val configuration = LocalConfiguration.current
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val right = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                size.width * 9f / 10f
            }
            else -> {
                size.width * 9f / 10f
            }
        }

        return Outline.Rectangle(
            Rect(
                left = 0f,
                top = 0f,
                right = right,
                bottom = size.height
            )
        )
    }
}