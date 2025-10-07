package com.keagan.complete.ui.features

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.keagan.complete.R
import java.util.concurrent.TimeUnit
import kotlin.math.max

class PomodoroFragment : Fragment() {

    private var timer: CountDownTimer? = null
    private var running = false
    private var remainingMs: Long = 0L

    private lateinit var textTimer: TextView
    private lateinit var btnPlayPause: Button
    private lateinit var btnReset: Button
    private lateinit var pickerH: NumberPicker
    private lateinit var pickerM: NumberPicker
    private lateinit var pickerS: NumberPicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_placeholder_pomodoro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textTimer = view.findViewById(R.id.textTimer)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        btnReset = view.findViewById(R.id.btnReset)
        pickerH = view.findViewById(R.id.pickerHours)
        pickerM = view.findViewById(R.id.pickerMinutes)
        pickerS = view.findViewById(R.id.pickerSeconds)

        // picker ranges
        pickerH.minValue = 0;  pickerH.maxValue = 12
        pickerM.minValue = 0;  pickerM.maxValue = 59
        pickerS.minValue = 0;  pickerS.maxValue = 59

        // leading zeros for M/S
        pickerM.setFormatter { v -> "%02d".format(v) }
        pickerS.setFormatter { v -> "%02d".format(v) }

        // default 25:00
        pickerH.value = 0
        pickerM.value = 25
        pickerS.value = 0
        updateFromPickers()

        val onChange = NumberPicker.OnValueChangeListener { _, _, _ ->
            if (!running) updateFromPickers()
        }
        pickerH.setOnValueChangedListener(onChange)
        pickerM.setOnValueChangedListener(onChange)
        pickerS.setOnValueChangedListener(onChange)

        btnPlayPause.setOnClickListener {
            if (running) pauseTimer() else startTimer()
        }
        btnReset.setOnClickListener {
            cancelTimer()
            updateFromPickers()
        }
    }

    private fun updateFromPickers() {
        remainingMs =
            TimeUnit.HOURS.toMillis(pickerH.value.toLong()) +
                    TimeUnit.MINUTES.toMillis(pickerM.value.toLong()) +
                    TimeUnit.SECONDS.toMillis(pickerS.value.toLong())

        // Don’t allow 0: fallback to 25:00
        if (remainingMs == 0L) {
            pickerH.value = 0; pickerM.value = 25; pickerS.value = 0
            remainingMs = TimeUnit.MINUTES.toMillis(25)
        }

        textTimer.text = formatMs(remainingMs)
        btnPlayPause.text = "Play"
        running = false
    }

    private fun startTimer() {
        if (remainingMs <= 0L) updateFromPickers()

        running = true
        btnPlayPause.text = "Pause"

        timer?.cancel()
        timer = object : CountDownTimer(remainingMs, 1_000L) {
            override fun onTick(ms: Long) {
                remainingMs = max(0L, ms)
                textTimer.text = formatMs(remainingMs)
            }
            override fun onFinish() {
                running = false
                remainingMs = 0L
                textTimer.text = "00:00:00"
                btnPlayPause.text = "Play"
            }
        }.start()
    }

    private fun pauseTimer() {
        running = false
        btnPlayPause.text = "Play"
        timer?.cancel()
        timer = null
    }

    private fun cancelTimer() {
        running = false
        timer?.cancel()
        timer = null
        btnPlayPause.text = "Play"
    }

    private fun formatMs(ms: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(ms)
        val m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val s = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }
}
