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
        painter = painterResource(id = R.drawable.zawitco_company_logo_1788754962037),
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

/**
 * Large Corporate Brand Hero composable for the Login Interface
 */
@Composable
fun ZawitcoHeroLogo(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Card containing the uploaded official company logo
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(18.dp))
        .background(Color.White)
        .border(1.5.dp, Slate200, RoundedCornerShape(18.dp))
        .padding(horizontal = 20.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.zawitco_company_logo_1788754962037),
        contentDescription = "Zawitco Corporate Logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .height(72.dp)
          .width(220.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Bilingual Header Titles
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "زاوية كو",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = ZawitcoBlue
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "•",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ZawitcoOrange
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "ZAWITCO",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = ZawitcoBlue,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = "ACCOMMODATION & HOUSING PORTAL",
      fontSize = 12.sp,
      fontWeight = FontWeight.ExtraBold,
      color = ZawitcoOrange,
      letterSpacing = 1.5.sp
    )

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = "بوابة إدارة سكن الموظفين والعقارات",
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = Color(0xFF64748B)
    )
  }
}
