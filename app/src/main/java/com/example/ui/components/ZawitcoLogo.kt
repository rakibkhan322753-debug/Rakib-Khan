package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Slate200
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun ZawitcoCompanyLogo(
  modifier: Modifier = Modifier,
  height: Dp = 42.dp,
  showSubtext: Boolean = true
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Official Logo Image Container
    Box(
      modifier = Modifier
        .height(height)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .border(1.dp, Slate200.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
        .padding(horizontal = 4.dp, vertical = 2.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.zawitco_logo_header),
        contentDescription = "Zawitco Company Logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier.height(height - 4.dp)
      )
    }

    if (showSubtext) {
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "ZAWITCO",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = ZawitcoBlue,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "زاوية كو",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoBlue
          )
        }
        Text(
          text = "ACCOMMODATION & HOUSING",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = ZawitcoOrange,
          letterSpacing = 1.1.sp
        )
      }
    }
  }
}
