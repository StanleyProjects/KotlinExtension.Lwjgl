package sp.service.sample.util

import org.json.JSONObject
import sp.lwjgl.joysticks.JoystickAxis
import sp.lwjgl.joysticks.JoystickButton
import sp.lwjgl.joysticks.JoystickMapping

internal class JsonJoystickMapping(json: String) : JoystickMapping {
    private val axisMap: Map<JoystickAxis, Int>
    private val buttons: Map<JoystickButton, Int>

    init {
        val root = JSONObject(json)
        val axisObject = root.getJSONObject("axis")
        val axisMap = mutableMapOf<JoystickAxis, Int>()
        JoystickAxis.entries.forEach {
            axisMap[it] = axisObject.getInt(it.name)
        }
        this.axisMap = axisMap
        val buttonsObject = root.getJSONObject("buttons")
        val buttonsMap = mutableMapOf<JoystickButton, Int>()
        JoystickButton.entries.forEach {
            buttonsMap[it] = buttonsObject.getInt(it.name)
        }
        buttons = buttonsMap
    }

    override fun getIndex(axis: JoystickAxis): Int {
        return axisMap[axis] ?: error("No index by $axis!")
    }

    override fun getIndex(button: JoystickButton): Int {
        return buttons[button] ?: error("No index by $button!")
    }
}
