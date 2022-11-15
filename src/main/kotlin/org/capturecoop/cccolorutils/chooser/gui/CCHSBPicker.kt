package org.capturecoop.cccolorutils.chooser.gui

import org.capturecoop.cccolorutils.CCColorUtils.createHSVBox
import org.capturecoop.cccolorutils.CCHSB
import org.capturecoop.cccolorutils.fill
import org.capturecoop.ccutils.math.CCVector2Float
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class CCHSBPicker(color: Color?, alwaysGrab: Boolean) : JPanel() {
    var hue = 0.0f
        set(value) {
            field = value
            dirty = true
            repaint()
        }
    var saturation = 0.0f
        set(value) {
            field = value
            dirty = true
            repaint()
        }
    var brightness = 0.0f
        set(value) {
            field = value
            dirty = true
            repaint()
        }
    private var isDragging = false
    private var buffer: BufferedImage? = null
    private var dirty = true
    private val changeListeners = ArrayList<ChangeListener>()
    val color: Color
        get() {
            return CCHSB(hue, saturation, brightness).asColor()
        }

    init {
        val hsb = CCHSB(color!!)
        hue = hsb.hue
        saturation = hsb.saturation
        brightness = hsb.brightness
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(mouseEvent: MouseEvent) {
                if (getSelectRect().contains(mouseEvent.point) && !alwaysGrab) isDragging = true
            }

            override fun mouseReleased(mouseEvent: MouseEvent) {
                isDragging = false
                if (alwaysGrab) execute(mouseEvent.x, mouseEvent.y)
            }
        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(mouseEvent: MouseEvent) {
                if (isDragging || alwaysGrab) execute(mouseEvent.x, mouseEvent.y)
            }
        })
    }

    private fun execute(x: Int, y: Int) {
        dirty = true
        val percentageX = x * 100f / width
        val percentageY = y * 100f / height
        val pointX = CCVector2Float((percentageX / 100f).toDouble(), 0.0).limitX(0f, 1f).x
        var pointY = CCVector2Float((percentageY / 100f).toDouble(), 0.0).limitX(0f, 1f).x
        pointY = (pointY - 1) * -1
        saturation = pointX
        brightness = pointY
        repaint()
        changeListeners.forEach { it.stateChanged(ChangeEvent(this)) }
    }

    fun getSelectRect(): Rectangle {
        val posX = (saturation * sizeX).toInt()
        val posY = ((brightness - 1) * -1 * sizeY).toInt()
        return Rectangle(posX, posY, MARGIN, MARGIN)
    }

    private val sizeX: Int
        get() = width - MARGIN
    private val sizeY: Int
        get() = height - MARGIN

    override fun paint(g: Graphics) {
        if (!dirty && buffer != null) {
            g.drawImage(buffer, 0, 0, this)
            return
        }
        if (buffer == null || !(buffer!!.width == width && buffer!!.height == height))
            buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        
        buffer!!.graphics.also { bufferGraphics ->
            dirty = false
            val sizeX = sizeX
            val sizeY = sizeY
            bufferGraphics.color = background
            bufferGraphics.fillRect(0, 0, width, height)
            bufferGraphics.drawImage(createHSVBox(width, height, hue), MARGIN / 2, MARGIN / 2, sizeX, sizeY, this)
            bufferGraphics.color = Color.BLACK
            bufferGraphics.drawRect(MARGIN / 2 - 1, MARGIN / 2 - 1, sizeX + 1, sizeY + 1)
            bufferGraphics.color = Color.GRAY
            getSelectRect().fill(bufferGraphics)
            bufferGraphics.dispose()
            g.drawImage(buffer, 0, 0, this)
        }
    }

    fun addChangeListener(listener: ChangeListener) {
        //These only fire on changes made by the user
        changeListeners.add(listener)
    }

    companion object {
        private const val MARGIN = 10
    }
}