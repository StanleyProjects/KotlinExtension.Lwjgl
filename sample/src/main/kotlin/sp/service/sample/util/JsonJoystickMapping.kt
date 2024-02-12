package sp.service.sample.util

import org.json.JSONObject
import sp.lwjgl.joysticks.JoystickAxis
import sp.lwjgl.joysticks.JoystickButton
import sp.lwjgl.joysticks.JoystickMapping

internal class JsonJoystickMapping(json: String) : JoystickMapping {
    private val axises: Map<JoystickAxis, Int>
    private val buttons: Map<JoystickButton, Int>

    init {
        val root = JSONObject(json)
        val axisObject = root.getJSONObject("axis")
        axises = JoystickAxis.entries.associateWith {
            axisObject.getInt(it.name)
        }
        val buttonsObject = root.getJSONObject("buttons")
        buttons = JoystickButton.entries.associateWith {
            buttonsObject.getInt(it.name)
        }
    }

    override fun getIndex(axis: JoystickAxis): Int {
        return axises[axis] ?: error("No index by $axis!")
    }

    override fun getIndex(button: JoystickButton): Int {
        return buttons[button] ?: error("No index by $button!")
    }
}
