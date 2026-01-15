package com.felix.mealplanner20.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.felix.mealplanner20.R

// Set of Material typography styles to start with
val poppinsFontFamily = FontFamily(
    Font(R.font.poppins_blackitalic,FontWeight.Black),
    Font(R.font.poppins_bold,FontWeight.Bold),
    Font(R.font.poppins_semibold,FontWeight.SemiBold),
    Font(R.font.poppins_medium,FontWeight.Medium),
    Font(R.font.poppins_regular,FontWeight.Normal),
    Font(R.font.poppins_lightitalic,FontWeight.Light)
)
val monaSansFamily = FontFamily(
    Font(R.font.monasans_semibold,FontWeight.SemiBold),
    Font(R.font.monasans_medium,FontWeight.Medium),
    Font(R.font.monasans_italic,FontWeight.Thin),
    Font(R.font.monasans_regular,FontWeight.Normal),
    Font(R.font.monasans_light,FontWeight.Light)
    )

val Typography: Typography
    get() = Typography(

        titleLarge = TextStyle(
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Slate950
        ),
        titleMedium = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Slate950
        ),
        titleSmall = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.Thin,
            fontSize = 12.sp,
            color = Slate950
        ),
        labelSmall = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Slate950
        ),
        labelLarge = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Slate950
        ),
        bodyMedium = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Slate950
        ),
        labelMedium = TextStyle( //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = Slate950
        ),
         bodySmall =  TextStyle( //TODO dont change!
             fontFamily = monaSansFamily,
             fontWeight = FontWeight.Normal,
             fontSize = 14.sp,
             color = Slate950
         ),
        bodyLarge = TextStyle(  //TODO dont change!
            fontFamily = monaSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Slate950,
            lineHeight = 28.sp,
            letterSpacing = 1.5.sp),

      displayMedium = TextStyle(
          fontFamily = monaSansFamily,
          fontWeight = FontWeight.Light,
          fontSize = 64.sp
      ),
        headlineSmall = TextStyle(
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal, // Etwas schwerer als TitleLarge für Betonung
            fontSize = 24.sp,                 // 2.sp Größer als titleLarge
            lineHeight = 32.sp,               // 4.sp mehr lineHeight als titleLarge
            letterSpacing = 0.sp
        )
)
