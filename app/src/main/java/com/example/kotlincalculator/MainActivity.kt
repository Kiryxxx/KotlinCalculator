package com.example.kotlincalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    // TextView отоброжение ввода и вывода
    lateinit var txtInput: TextView

    // Определяет что последняя нажатая клавиша числовая
    private var lastNumeric: Boolean = false

    // Represent that current state is in error or not
    var stateError: Boolean = false

    // If true, do not allow to add another DOT
    var lastDot: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtInput = findViewById(R.id.txtInput)
    }


    fun plusMinus(view: View) {
        if (lastNumeric && !stateError) {
            var lastNum = (txtInput.text.split("+", "-", "*", "/").last().toString())
            var beforeLast = txtInput.text.toString().substringBeforeLast("$lastNum") + "(-"
            txtInput.text = beforeLast + lastNum
        } else if (!lastNumeric && !stateError && !lastDot) {
            txtInput.text = txtInput.text.toString() + "(-"
            lastNumeric = false
            lastDot = false
        }
    }


    fun onDigit(view: View) {
        if (stateError) {
            // If current state is Error, replace the error message
            txtInput.text = (view as Button).text
            stateError = false
        } else {
            // If not, already there is a valid expression so append to it
            txtInput.append((view as Button).text)
        }
        // Set the flag
        lastNumeric = true
    }

    fun onBack(view: View) {
        this.txtInput.text = this.txtInput.text.dropLast(1)
        stateError = false
        lastDot = false
    }


    fun percentage(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            var lastNum = txtInput.text.split("+", "-", "*", "/", "(", ")").last()
            var beforeLast = txtInput.text.toString().substringBeforeLast("$lastNum")
            if (beforeLast.last() == '-' || beforeLast.last() == '+') {
                txtInput.text = beforeLast + (((txtInput.text.split("+", "-", "*", "/", "(", ")")
                    .let { it[it.size - 2] }).toDouble() * lastNum.toDouble()) / 100)
            } else {
                txtInput.text = beforeLast + (lastNum.toDouble()) / 100
            }
        }
    }


    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            txtInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }


    fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            txtInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false    // Reset the DOT flag

        }
    }

    fun onOperator1(view: View) {
        if (!lastNumeric && !stateError && !lastDot) {
            txtInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false    // Reset the DOT flag
        }
    }

    fun onOperator2(view: View) {
        val cont1 = txtInput.text.count{ "(".contains(it) }
        val cont2 = txtInput.text.count{ ")".contains(it)  }
        if (lastNumeric && !stateError && (cont1 - cont2 > 0)) {
            txtInput.append((view as Button).text)
        }
    }


    fun onClear(view: View) {
        this.txtInput.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }


    fun onEqual(view: View) {
        val cont1 = txtInput.text.count{ "(".contains(it) }
        val cont2 = txtInput.text.count{ ")".contains(it)  }
        val close: StringBuilder = StringBuilder().apply { repeat(cont1-cont2) {append(")")} }
        val close2: StringBuilder = StringBuilder().apply { repeat(cont2-cont1) {append("(")} }
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumeric && !stateError && !txtInput.text.contains('(')) {
            // Read the expression
            val txt = txtInput.text.toString()
            // Create an Expression (A class from exp4j library)
            val expression = ExpressionBuilder(txt).build()
            try {
                // Calculate the result and display
                val result = expression.evaluate()
                txtInput.text = result.toBigDecimal().setScale(6, BigDecimal.ROUND_HALF_DOWN).toDouble().toString()
                lastDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                txtInput.text = "Error"
                stateError = true
                lastNumeric = false
            }
        } else if (lastNumeric && !stateError && txtInput.text.contains('(') && (cont1-cont2 > 0)
            )
         {
            // Read the expression
            val txt = txtInput.text.toString() + close
            // Create an Expression (A class from exp4j library)
            val expression = ExpressionBuilder(txt).build()
            try {
                // Calculate the result and display
                val result = expression.evaluate()
                txtInput.text = result.toString()
                lastDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                txtInput.text = "Error"
                stateError = true
                lastNumeric = false
            }
        } else if (lastNumeric && !stateError && txtInput.text.contains('(') && txtInput.text.contains(
                ')')
            )
         {
            // Read the expression
            val txt = txtInput.text.toString()
            // Create an Expression (A class from exp4j library)
            val expression = ExpressionBuilder(txt).build()
            try {
                // Calculate the result and display
                val result = expression.evaluate()
                txtInput.text = result.toString()
                lastDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                txtInput.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }
}