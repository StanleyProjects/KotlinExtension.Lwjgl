package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineInputState
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.engine.input.Joystick
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.Point
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.entity.point
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.entity.updated
import sp.kx.lwjgl.util.EngineUtil
import sp.kx.lwjgl.util.drawCircle
import sp.kx.lwjgl.util.drawRectangle
import sp.service.sample.util.ResourceUtil
import sp.service.sample.util.XBoxSeriesJoystickMapping
import java.io.InputStream
import java.util.concurrent.TimeUnit

/*
private object SampleEngineLogic : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit
	override val inputCallback = object : EngineInputCallback {
		override fun onKey(key: Key, state: KeyState) {
			// ignored
		}
	}

	override fun shouldEngineStop(): Boolean {
		return ::shouldEngineStopUnit.isInitialized
	}

	override fun onRender(
		canvas: Canvas,
		inputState: EngineInputState,
		property: EngineProperty
	) {
		canvas.drawPoint(
			color = Color.GREEN,
			point = point(
				x = property.pictureSize.width / 2,
				y = property.pictureSize.height / 2
			)
		)
	}
}
*/

class InputEngineLogic(private val engine: Engine) : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit

	private fun getFontInfo(name: String, height: Float): FontInfo {
		return object : FontInfo {
			override val id: String = "${name}_${height}"
			override val height: Float = height

			override fun getInputStream(): InputStream {
				return ResourceUtil.requireResourceAsStream(name)
			}
		}
	}

	override val joystickMapper: JoystickMapper = object : JoystickMapper {
		override fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping? {
			when (guid) {
				"030000005e040000130b000013050000" -> {
					if (buttons.size == 20 && axes.size == 6) {
						return XBoxSeriesJoystickMapping
					}
				}
			}
			return null
		}
	}

	override val inputCallback = object : EngineInputCallback {
		override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
			when (button) {
				KeyboardButton.ESCAPE -> {
					if (!isPressed) {
						shouldEngineStopUnit = Unit
					}
				}
			}
		}

		override fun onJoystickButton(button: JoystickButton, isPressed: Boolean) {
			when (button) {
				JoystickButton.B -> {
					if (!isPressed) {
//						shouldEngineStopUnit = Unit // todo
					}
				}
			}
		}
	}

	override fun shouldEngineStop(): Boolean {
		return ::shouldEngineStopUnit.isInitialized
	}

	private var w = -1.0
	private fun Canvas.drawJoystickButton(pointCenter: Point, text: String, isPressed: Boolean) {
		val color = if (isPressed) Color.YELLOW else Color.GREEN
		drawCircle(
			color = color,
			pointCenter = pointCenter,
			radius = 16.0,
			edgeCount = 16,
			lineWidth = 2f
		)
		val textHeight = 16f
		val info = getFontInfo("font.ttf", height = textHeight)
		val width = engine.fontAgent.getTextWidth(info, text)
		if (width != w) {
			println("before: $w | after: $width")
			w = width // todo wrong width!
		}
		drawText(
			color = color,
			pointTopLeft = pointCenter.updated(dX = - width / 2, dY = - textHeight / 2.0),
			info = info,
			text = text
		)
	}

	private fun test(canvas: Canvas, y: Double, height: Double, text: CharSequence) {
		val info = getFontInfo("font.ttf", height = height.toFloat())
		val width = engine.fontAgent.getTextWidth(info, text)
		val pointTopLeft = point(x = 25.0, y = y)
		canvas.drawRectangle(
			color = Color.GREEN,
			pointTopLeft = pointTopLeft,
			size = size(width = width, height = height),
			lineWidth = 2f
		)
		canvas.drawText(
			color = Color.GREEN,
			pointTopLeft = pointTopLeft,
			info = info,
			text = text
		)
	}

	override fun onRender(canvas: Canvas) {
		/*
//		setOf(8, 16, 32, 64).forEachIndexed { index, height ->
//			canvas.drawText(
//				info = getFontInfo("font.ttf", height = height.toFloat()),
//				pointTopLeft = point(x = 0, y = 32 * index),
//				color = Color.GREEN,
//				text = "$height) 0az9AZ!\""
//			)
//		}
		val first = 500 * 0
		val last = if (first + 500 >= Char.MAX_VALUE.code) Char.MAX_VALUE.code - 1 else first + 500
		val height = 16
//		val map = (Char.MIN_VALUE until Char.MAX_VALUE)
		val map = (first.toChar() until last.toChar())
//		val map = ('А'..'я')
//		val map = ('!'..'z')
			.mapIndexed { index, char -> (index / 20) to char }
			.groupBy { (k, _) -> k }
			.mapValues { (_, v) -> v.map { (_, char) -> char } }
			.toMap()
		map.forEach { (k, list) ->
			list.forEachIndexed { index, char ->
				val x = index * height * 2.0
				val y = k * height * 1.5
				canvas.drawText(
					info = getFontInfo("font.ttf", height = height.toFloat() / 2),
					pointTopLeft = point(x = x, y = y),
					color = Color.GREEN,
					text = String.format("%03d", index + k * 20)
				)
				canvas.drawText(
					info = getFontInfo("font.ttf", height = height.toFloat()),
					pointTopLeft = point(x = x, y = y + height * 0.5),
					color = Color.GREEN,
					text = "$char"
				)
			}
		}
		*/
//		"xyzABC123".also { text ->
//			val height = 16.0
//			for (i in text.indices) {
//				test(canvas, y = 128.0 + height * i, height = height, text = text.substring(0..i))
//			}
//		}
//		return // todo
		val fps = TimeUnit.SECONDS.toNanos(1).toDouble() / (engine.property.timeNow - engine.property.timeLast)
		canvas.drawText(
			info = getFontInfo("font.ttf", height = 16f),
			pointTopLeft = point(x = 0, y = 0),
			color = Color.GREEN,
			text = String.format("%.2f", fps)
		)
		val joystick = engine.input.joysticks[0]
		if (joystick != null) {
			canvas.drawJoystickButton(
				pointCenter = point(x = 25.0 + 25 * 2, y = 25.0 + 25 * 6),
				text = "A",
				isPressed = joystick.isPressed(JoystickButton.A)
			)
			canvas.drawJoystickButton(
				pointCenter = point(x = 25.0 + 25 * 3, y = 25.0 + 25 * 5),
				text = "B",
				isPressed = joystick.isPressed(JoystickButton.B)
			)
			canvas.drawJoystickButton(
				pointCenter = point(x = 25.0 + 25 * 1, y = 25.0 + 25 * 5),
				text = "X",
				isPressed = joystick.isPressed(JoystickButton.X)
			)
			canvas.drawJoystickButton(
				pointCenter = point(x = 25.0 + 25 * 2, y = 25.0 + 25 * 4),
				text = "Y",
				isPressed = joystick.isPressed(JoystickButton.Y)
			)
			// todo
		}
		setOf(
			setOf(KeyboardButton.Q, KeyboardButton.W, KeyboardButton.E, KeyboardButton.R, KeyboardButton.T, KeyboardButton.Y, KeyboardButton.U, KeyboardButton.I, KeyboardButton.O, KeyboardButton.P),
			setOf(KeyboardButton.A, KeyboardButton.S, KeyboardButton.D, KeyboardButton.F, KeyboardButton.G, KeyboardButton.H, KeyboardButton.J, KeyboardButton.K, KeyboardButton.L),
			setOf(KeyboardButton.Z, KeyboardButton.X, KeyboardButton.C, KeyboardButton.V, KeyboardButton.B, KeyboardButton.N, KeyboardButton.M)
		).forEachIndexed { y, keys ->
			keys.forEachIndexed { x, button ->
				val isPressed = engine.input.keyboard.isPressed(button)
				canvas.drawText(
					info = getFontInfo("font.ttf", height = 16f),
					color = if (isPressed) Color.YELLOW else Color.GREEN,
					pointTopLeft = point(25 + 25 * x, 25 + 25 * y),
					text = button.name
				)
			}
		}
	}
}

fun main() {
//	val logic: EngineLogic = SampleEngineLogic
	EngineUtil.run(::InputEngineLogic)
}
