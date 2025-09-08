package com.joviansapps.ganymede.graphing

import androidx.compose.ui.graphics.Color
import kotlin.math.*

// Expression-based graph function evaluated via local RPN parser
class GraphFunction(val expression: String, val color: Color, val name: String) {
    private val rpn: List<String>
    init {
        val tokens = tokenize(expression)
        rpn = shuntingYard(tokens)
    }
    fun execute(xVal: Float): Float? {
        if (rpn.isEmpty()) return null
        val y = evalRPN(rpn, xVal.toDouble())
        return if (y.isFinite()) y.toFloat() else null
    }
    // --- parser helpers ---
    private fun tokenize(s: String): List<String> {
        val input = s.replace(" ", "")
        val tokens = mutableListOf<String>()
        var i = 0
        val funcs = setOf("sin","cos","tan","asin","acos","atan","exp","ln","log","sqrt","abs")
        while (i < input.length) {
            val c = input[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i]=='.')) i++
                    tokens += input.substring(start,i)
                    if (i < input.length && (input[i].isLetter() || input[i]=='(')) tokens += "*"
                    continue
                }
                c.isLetter() -> {
                    val start=i
                    while (i < input.length && input[i].isLetter()) i++
                    val name = input.substring(start,i)
                    when(name.lowercase()){
                        "pi"-> tokens += Math.PI.toString()
                        "e" -> tokens += Math.E.toString()
                        else -> tokens += name
                    }
                    if (i < input.length) {
                        val next = input[i]
                        if (next=='(') { if (name.lowercase() !in funcs) tokens += "*" } else if (next.isDigit()||next=='.') tokens += "*" }
                    continue
                }
                c==',' -> { tokens += "."; i++ }
                else -> { tokens += c.toString(); i++ }
            }
        }
        return tokens
    }
    private fun shuntingYard(tokens: List<String>): List<String> {
        val out = mutableListOf<String>()
        val ops = ArrayDeque<String>()
        val prec = mutableMapOf("+" to 2,"-" to 2,"*" to 3,"/" to 3,"^" to 4)
        prec["u-"]=5
        val rightAssoc = setOf("^","u-")
        val funcs = setOf("sin","cos","tan","asin","acos","atan","exp","ln","log","sqrt","abs")
        var prev: String? = null
        for (t in tokens) {
            when {
                t.toDoubleOrNull()!=null || t=="x" -> { out+=t; prev="value" }
                t.lowercase() in funcs -> { ops.addFirst(t.lowercase()); prev="func" }
                t=="," -> { while (ops.isNotEmpty() && ops.first()!="(") out+=ops.removeFirst(); prev="," }
                t in setOf("+","-","*","/","^") -> {
                    if (t=="-" && (prev==null||prev=="op"||prev=="("||prev==",")) {
                        val op="u-"; while (ops.isNotEmpty() && ops.first() in prec.keys){ val p1=prec[op]?:0; val p2=prec[ops.first()]?:0; if((op !in rightAssoc && p1<=p2)||(op in rightAssoc && p1<p2)) out+=ops.removeFirst() else break } ; ops.addFirst(op)
                    } else {
                        while (ops.isNotEmpty() && ops.first() in prec.keys){ val p1=prec[t]?:0; val p2=prec[ops.first()]?:0; if((t !in rightAssoc && p1<=p2)||(t in rightAssoc && p1<p2)) out+=ops.removeFirst() else break }
                        ops.addFirst(t)
                    }
                    prev="op"
                }
                t=="(" -> { ops.addFirst(t); prev="(" }
                t==")" -> { while(ops.isNotEmpty() && ops.first()!="(") out+=ops.removeFirst(); if(ops.isNotEmpty() && ops.first()=="(") ops.removeFirst(); if(ops.isNotEmpty() && ops.first() !in prec.keys && ops.first()!="(") out+=ops.removeFirst(); prev=")" }
                else -> prev=null
            }
        }
        while (ops.isNotEmpty()) out+=ops.removeFirst()
        return out
    }
    private fun evalRPN(rpn: List<String>, xVal: Double): Double {
        val stack = ArrayDeque<Double>()
        for (t in rpn) when {
            t.toDoubleOrNull()!=null -> stack.addFirst(t.toDouble())
            t=="x" -> stack.addFirst(xVal)
            t=="+" -> { val b=stack.removeFirst(); val a=stack.removeFirst(); stack.addFirst(a+b) }
            t=="-" -> { val b=stack.removeFirst(); val a=stack.removeFirst(); stack.addFirst(a-b) }
            t=="*" -> { val b=stack.removeFirst(); val a=stack.removeFirst(); stack.addFirst(a*b) }
            t=="/" -> { val b=stack.removeFirst(); val a=stack.removeFirst(); stack.addFirst(a/b) }
            t=="^" -> { val b=stack.removeFirst(); val a=stack.removeFirst(); stack.addFirst(a.pow(b)) }
            t=="u-"-> { val a=stack.removeFirst(); stack.addFirst(-a) }
            t=="sin"-> { val a=stack.removeFirst(); stack.addFirst(sin(a)) }
            t=="cos"-> { val a=stack.removeFirst(); stack.addFirst(cos(a)) }
            t=="tan"-> { val a=stack.removeFirst(); stack.addFirst(tan(a)) }
            t=="asin"-> { val a=stack.removeFirst(); stack.addFirst(asin(a)) }
            t=="acos"-> { val a=stack.removeFirst(); stack.addFirst(acos(a)) }
            t=="atan"-> { val a=stack.removeFirst(); stack.addFirst(atan(a)) }
            t=="exp" -> { val a=stack.removeFirst(); stack.addFirst(exp(a)) }
            t=="ln" || t=="log" -> { val a=stack.removeFirst(); stack.addFirst(ln(a)) }
            t=="sqrt"-> { val a=stack.removeFirst(); stack.addFirst(sqrt(a)) }
            t=="abs" -> { val a=stack.removeFirst(); stack.addFirst(abs(a)) }
            else -> {}
        }
        return if (stack.isNotEmpty()) stack.first() else Double.NaN
    }
}
