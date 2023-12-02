package com.evtimov.ui.widgets

import android.view.ViewGroup
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.evtimov.ui.theme.SbTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
fun SbBottomSheet(
    modifier: Modifier = Modifier,
    state: SbBottomSheetState = rememberSbBottomSheetState(),
    content: @Composable () -> Unit,
) {
    SheetsOverlay {
        SwipingSheet(
            state = state,
            modifier = modifier,
            anchors = { maxHeight, contentHeight, _ ->
                val offset = (maxHeight - contentHeight).coerceAtLeast(0)
                if (state.skipHalfExpanded) {
                    mapOf(
                        maxHeight.toFloat() to SbBottomSheetValue.Dismissed,
                        offset.toFloat() to SbBottomSheetValue.Expanded
                    )
                } else {
                    mapOf(
                        maxHeight.toFloat() to SbBottomSheetValue.Dismissed,
                        offset.coerceAtLeast(maxHeight / 2).toFloat() to SbBottomSheetValue.Collapsed,
                        offset.toFloat() to SbBottomSheetValue.Expanded
                    )
                }
            },
            overlayColor = if (state.isModalSheet) {
                Color(0x40000000).copy(alpha = 0.25f)
            } else {
                Color.Unspecified
            },
            sheetContent = content
        )
    }
}

@Composable
internal fun SheetsOverlay(content: @Composable () -> Unit) {
    val parentCompositionContext = rememberCompositionContext()
    val localView = LocalView.current
    val composeView = remember {
        ComposeView(localView.context).apply {
            compositionContext = parentCompositionContext
            setContent {
                @OptIn(ExperimentalFoundationApi::class)
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null,
                    content = content
                )
            }
        }
    }
    val contentView = localView.rootView.findViewById<ViewGroup>(android.R.id.content)
    DisposableEffect(composeView) {
        contentView.addView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        onDispose {
            contentView.removeView(composeView)
        }
    }
}

@Suppress("LongMethod")
@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
internal fun SwipingSheet(
    state: SbBottomSheetState,
    anchors: (Int, Int, SbBottomSheetState) -> Map<Float, SbBottomSheetValue>,
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Unspecified,
    alignment: Alignment.Vertical = Alignment.Bottom,
    sheetContent: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dismissed = state.targetValue.isDismissed
    val scrimColor by animateColorAsState(
        targetValue = if (dismissed) {
            Color.Transparent
        } else {
            overlayColor
        }
    )
    val shape: Shape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp
    )
    Box(Modifier.fillMaxSize()) {
        Scrim(
            color = scrimColor,
            onDismiss = {
                coroutineScope.launch { state.dismiss() }
            },
            visible = !dismissed
        )
    }
    SwipingSheetLayout(
        state = state,
        modifier = modifier
            .semantics {
                if (state.currentValue.isDismissed) {
                    expand {
                        coroutineScope.launch { state.show() }
                        true
                    }
                } else {
                    dismiss {
                        coroutineScope.launch { state.dismiss() }
                        true
                    }
                }
            }
            .fillMaxSize(),
        shape = shape,
        alignment = alignment
    ) {
        val contentMaxHeight: Int = constraints.maxHeight
        val contentHeight = remember { mutableStateOf(0) }
        val localAnchors = remember(contentMaxHeight, contentHeight.value, state) {
            val localSheetAnchors = if (contentHeight.value > 0) {
                anchors(contentMaxHeight, contentHeight.value, state)
            } else {
                emptyMap()
            }
            state.sheetAnchors = localSheetAnchors
            localSheetAnchors
        }
        val nestedScrollConnection = remember(state, alignment) {
            SbSheetsNestedScrollConnection(state = state, alignment = alignment)
        }
        val density = LocalDensity.current
        val topInset = WindowInsets.statusBars.getTop(density)
        val bottomInset = WindowInsets.navigationBars.getBottom(density)
        Layout(
            modifier = Modifier
                .clip(shape = shape)
                .nestedScroll(nestedScrollConnection)
                .swipeSheet(state, contentHeight, localAnchors)
                .onSizeChanged { contentHeight.value = it.height }
                .fillMaxWidth(),
            content = {
                SbTheme(darkTheme = false) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        sheetContent.invoke()
                        if (state.showHandle) {
                            BottomSheetHandle(
                                Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            val insetDelta = if (alignment == Alignment.Bottom) topInset else bottomInset
            // measure the sheet keeping the insets into account
            val looseConstraints =
                constraints.copy(minWidth = 0, minHeight = 0, maxHeight = constraints.maxHeight - insetDelta)
            val sheetPlaceable = measurables.firstOrNull()?.measure(looseConstraints)
            layout(constraints.maxWidth, sheetPlaceable?.height ?: 0) {
                sheetPlaceable?.place(x = 0, y = 0)
            }
        }
    }
}

@Composable
private fun BottomSheetHandle(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color = Color(0xFFDFE5F0), CircleShape)
            .size(48.dp, 5.dp)
    )
}

@Composable
@ExperimentalMaterialApi
private fun SwipingSheetLayout(
    modifier: Modifier,
    state: SbBottomSheetState,
    shape: Shape,
    alignment: Alignment.Vertical,
    content: @Composable SwipeableSheetScope.() -> Unit
) {
    var sheetOffset by remember { mutableStateOf(0) }
    val showShadow by remember(sheetOffset) { derivedStateOf { sheetOffset > 0f } }
    SubcomposeLayout(modifier = modifier) { constraints ->
        val scope = SwipeableSheetScope(constraints)
        val contentMeasurables = subcompose(slotId = LayoutSlot.Content) { scope.content() }
        val contentPlaceable: Placeable = contentMeasurables.first().measure(
            constraints.copy(minWidth = 0, minHeight = 0)
        )
        val backgroundPlaceable = if (contentPlaceable.heightOrZero != 0) {
            val background = subcompose(LayoutSlot.Background) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .conditional(
                            showShadow,
                            Modifier.customShadow(
                                color = SHADOW_COLOR_SHEET,
                                alpha = SHADOW_ALPHA,
                                cornersRadius = 24.dp,
                                shadowBlurRadius = 8.dp
                            )
                        )
                        .background(Color.White, shape)
                        .clip(shape)
                )
            }
            background.first().measure(
                constraints.copy(
                    minWidth = constraints.maxWidth,
                    minHeight = constraints.maxHeight
                )
            )
        } else {
            null
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            sheetOffset = constraints.maxHeight - state.offset.value.toInt()
            val contentOffset =
                if (state.sheetAnchors.isEmpty()) constraints.maxHeight else state.offset.value.roundToInt()

            val delta = backgroundPlaceable.heightOrZero - contentPlaceable.heightOrZero
            val backgroundOffset =
                if (alignment == Alignment.Bottom) contentOffset else state.offset.value.roundToInt() - delta

            val effectiveBackgroundOffset =
                if (state.sheetAnchors.isEmpty()) constraints.maxHeight else backgroundOffset

            backgroundPlaceable?.placeRelative(x = 0, y = effectiveBackgroundOffset)
            contentPlaceable.placeRelative(
                x = 0,
                y = contentOffset
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun <T : SbBottomSheetValue> Modifier.swipeSheet(
    state: SbBottomSheetState,
    contentHeight: State<Int>,
    anchors: Map<Float, T>
): Modifier {
    val swipeableModifier = if (contentHeight.value != 0) {
        Modifier.swipeable(
            state = state,
            anchors = anchors,
            orientation = Orientation.Vertical,
            resistance = null
        )
    } else {
        Modifier
    }

    return this.then(swipeableModifier)
}

@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

private class SwipeableSheetScope(
    val constraints: Constraints
)

private enum class LayoutSlot {
    Content,
    Background
}

internal val Placeable?.heightOrZero: Int get() = this?.height ?: 0

private val SHADOW_COLOR_SHEET = Color(0x1A000000)
private const val SHADOW_ALPHA = 0.1f

@Stable
@OptIn(ExperimentalMaterialApi::class)
class SbBottomSheetState internal constructor(
    initialValue: SbBottomSheetValue,
    internal val showHandle: Boolean = true,
    internal val skipHalfExpanded: Boolean = false,
    internal val isModalSheet: Boolean = false,
    internal val animSpec: AnimationSpec<Float> = tween()
) : SwipeableState<SbBottomSheetValue>(
    initialValue,
    confirmStateChange = { true }
) {

    internal var sheetAnchors by mutableStateOf(emptyMap<Float, SbBottomSheetValue>())

    suspend fun show() {
        val targetValue =
            if (hasState(SbBottomSheetValue.Collapsed)) SbBottomSheetValue.Collapsed else SbBottomSheetValue.Expanded
        animateTo(targetValue, animSpec)
    }

    suspend fun expand() {
        val nextState = if ((currentValue.isDismissed) && !skipHalfExpanded) {
            SbBottomSheetValue.Collapsed
        } else {
            SbBottomSheetValue.Expanded
        }
        val targetValue = if (hasState(nextState)) nextState else SbBottomSheetValue.Expanded
        animateTo(targetValue, animSpec)
    }

    suspend fun collapse() {
        val nextState = if (currentValue == SbBottomSheetValue.Expanded && !skipHalfExpanded) {
            SbBottomSheetValue.Collapsed
        } else {
            SbBottomSheetValue.Dismissed
        }
        val targetValue = if (hasState(nextState)) nextState else SbBottomSheetValue.Dismissed
        animateTo(targetValue, animSpec)
    }

    private fun hasState(state: SbBottomSheetValue): Boolean = sheetAnchors.values.contains(state)

    suspend fun dismiss() {
        animateTo(SbBottomSheetValue.Dismissed, animSpec)
    }
}

enum class SbBottomSheetValue(val isDismissed: Boolean) {
    Expanded(isDismissed = false),
    Collapsed(false),
    Dismissed(true)
}

@Composable
fun rememberSbBottomSheetState(
    initialValue: SbBottomSheetValue = SbBottomSheetValue.Dismissed,
    showHandle: Boolean = true,
    skipHalfExpanded: Boolean = false,
    animSpec: AnimationSpec<Float> = tween()
): SbBottomSheetState {
    return remember {
        SbBottomSheetState(
            initialValue = initialValue,
            isModalSheet = true,
            showHandle = showHandle,
            skipHalfExpanded = skipHalfExpanded,
            animSpec = animSpec
        )
    }
}

internal class SbSheetsNestedScrollConnection(
    private val state: SbBottomSheetState,
    private val alignment: Alignment.Vertical,
) : NestedScrollConnection {
    var scrollOffset: Offset by mutableStateOf(Offset.Zero)

    private val minBound: Float get() = state.sheetAnchors.keys.minOrNull() ?: 0f
    private val maxBound: Float get() = state.sheetAnchors.keys.maxOrNull() ?: 0f

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        val bottomAlignmentCheck = alignment == Alignment.Bottom && delta < 0
        val topAlignmentCheck = alignment == Alignment.Top && delta > 0
        return if ((bottomAlignmentCheck || topAlignmentCheck) && source == NestedScrollSource.Drag) {
            state.performDrag(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        scrollOffset += consumed
        return if (source == NestedScrollSource.Drag) {
            state.performDrag(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = Offset(available.x, available.y).toFloat()
        val bottomAlignmentCheck = alignment == Alignment.Bottom && toFling < 0 && state.offset.value > minBound
        val topAlignmentCheck = alignment == Alignment.Top && toFling > 0 && state.offset.value < maxBound
        return if (bottomAlignmentCheck || topAlignmentCheck) {
            state.performFling(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.performFling(velocity = Offset(available.x, available.y).toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(0f, this)

    private fun Offset.toFloat(): Float = this.y
}

fun Modifier.conditional(enabled: Boolean, modifier: Modifier): Modifier {
    return if (enabled) this.then(modifier) else this
}

fun Modifier.customShadow(
    color: Color = shadowColor,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}

private val shadowColor = Color(0xFF0C1E40)
