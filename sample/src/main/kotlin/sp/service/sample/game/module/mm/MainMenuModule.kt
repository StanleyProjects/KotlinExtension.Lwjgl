package sp.service.sample.game.module.mm

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.implementation.entity.geometry.pointOf
import sp.service.sample.util.FontInfoUtil

class MainMenuModule(private val engine: Engine, private val broadcast: (Broadcast) -> Unit) {
    sealed interface Broadcast {
        class OnItem(val value: MainMenuItem) : Broadcast
    }

    private fun MainMenuItem.getTitle(): String {
        return when (this) {
            MainMenuItem.NEW_GAME -> "New game"
            MainMenuItem.SETTINGS -> "Settings"
            MainMenuItem.EXIT -> "Exit"
        }
    }

    private var position = 0

    fun onRender(canvas: Canvas) {
        val textHeight = 16f
        val h1 = textHeight * 2.0
        val p = 4.0
        val h = MainMenuItem.values().size * h1 + MainMenuItem.values().lastIndex * p
        val y = engine.property.pictureSize.height / 2 - h / 2
        MainMenuItem.values().forEachIndexed { index, item ->
            canvas.drawText(
                info = FontInfoUtil.getFontInfo(height = textHeight),
                pointTopLeft = pointOf(x = h1, y = y + index * (h1 + p)),
                color = Color.GREEN,
                text = item.getTitle()
            )
        }
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = textHeight),
            pointTopLeft = pointOf(x = h1 / 2, y = y + position * (h1 + p)),
            color = Color.YELLOW,
            text = ">"
        )
    }

    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
        when (button) {
            KeyboardButton.ENTER -> {
                if (isPressed) {
                    broadcast(Broadcast.OnItem(MainMenuItem.values()[position]))
                }
            }
            KeyboardButton.W -> {
                if (isPressed) {
                    if (position == 0) {
                        position = MainMenuItem.values().lastIndex
                    } else {
                        position--
                    }
                }
            }
            KeyboardButton.S -> {
                if (isPressed) {
                    if (position == MainMenuItem.values().lastIndex) {
                        position = 0
                    } else {
                        position++
                    }
                }
            }
        }
    }
}
