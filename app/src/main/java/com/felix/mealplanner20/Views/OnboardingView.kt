package com.felix.mealplanner20.Views

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ui.theme.Lime500
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import kotlinx.coroutines.launch

private sealed class SlideIcon {
    data class Vector(val imageVector: ImageVector) : SlideIcon()
    data class Drawable(@DrawableRes val resId: Int) : SlideIcon()
}

private data class OnboardingSlide(
    val icon: SlideIcon,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)

private val slides = listOf(
    OnboardingSlide(
        icon = SlideIcon.Vector(Icons.Filled.Favorite),
        titleRes = R.string.onboarding_slide1_title,
        descriptionRes = R.string.onboarding_slide1_description
    ),
    OnboardingSlide(
        icon = SlideIcon.Drawable(R.drawable.frame),
        titleRes = R.string.onboarding_slide2_title,
        descriptionRes = R.string.onboarding_slide2_description
    ),
    OnboardingSlide(
        icon = SlideIcon.Drawable(R.drawable.mealplan_menu_item),
        titleRes = R.string.onboarding_slide3_title,
        descriptionRes = R.string.onboarding_slide3_description
    ),
    OnboardingSlide(
        icon = SlideIcon.Drawable(R.drawable.nutristats_menu_item),
        titleRes = R.string.onboarding_slide4_title,
        descriptionRes = R.string.onboarding_slide4_description
    ),
    OnboardingSlide(
        icon = SlideIcon.Drawable(R.drawable.shopping_list_item),
        titleRes = R.string.onboarding_slide5_title,
        descriptionRes = R.string.onboarding_slide5_description
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingSlideContent(slide = slides[page])
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PageIndicatorRow(
                    pageCount = slides.size,
                    currentPage = pagerState.currentPage
                )
                BottomButtonRow(
                    isLastPage = pagerState.currentPage == slides.size - 1,
                    onSkip = onFinish,
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onFinish = onFinish
                )
            }
        }
    }
}

@Composable
private fun OnboardingSlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val icon = slide.icon) {
            is SlideIcon.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = Lime500
            )
            is SlideIcon.Drawable -> Icon(
                painter = painterResource(icon.resId),
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = Lime500
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(slide.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(slide.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = Slate200,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun PageIndicatorRow(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val size by animateDpAsState(
                targetValue = if (index == currentPage) 12.dp else 8.dp,
                label = "dot_size_$index"
            )
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(if (index == currentPage) Lime500 else Slate500)
            )
        }
    }
}

@Composable
private fun BottomButtonRow(
    isLastPage: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLastPage) {
            TextButton(onClick = onSkip) {
                Text(text = stringResource(R.string.onboarding_skip), color = Slate200)
            }
        } else {
            Spacer(modifier = Modifier.width(64.dp))
        }
        Button(
            onClick = if (isLastPage) onFinish else onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime500,
                contentColor = Slate950
            )
        ) {
            Text(
                text = stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
