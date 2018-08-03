package desgin

import java.util.HashMap

abstract class Element {
    var name: String = "" //菜名
    var price: Float = 0f //价格
    var weight: Int = 0    //份量

    abstract fun 供给(visitor: Visitor)
}

interface Visitor {
    fun 选菜(element: Element)
}

class Cabbage() : Element() {

    constructor(weight: Int) : this() {
        this.weight = weight
        this.price = price * weight
    }

    init {
        this.name = "卷心菜"
        this.price = 2f
        this.weight = 1
    }

    override fun 供给(visitor: Visitor) {
        visitor.选菜(this)
    }

}

class Meal() : Element() {
    constructor(weight: Int) : this() {
        this.weight = weight
        this.price = price * weight
    }

    init {
        this.name = "饭"
        this.price = 2f
        this.weight = 1
    }

    override fun 供给(visitor: Visitor) {
        visitor.选菜(this)
    }
}

class NormalVisitor : Visitor {
    override fun 选菜(element: Element) {
        println(element.name + element.weight + "份!")
    }
}

class Lunchbox {
    private var elements: HashMap<String, Element> = HashMap()
    private var allPrice = 0f

    fun Attach(element: Element) {
        elements[element.name] = element
    }

    fun Detach(element: Element) {
        this.elements.remove(element.name)
    }

    fun getElemente(name: String): Element? {
        return elements.get(name)
    }

    fun Accept(visitor: Visitor) {
        for (e in elements.values) {
            e.供给(visitor)
            allPrice += e.price * e.weight
        }
        print("总价:$allPrice")
    }
}

fun main(args: Array<String>) {
    val lunchbox = Lunchbox()

    lunchbox.Attach(Cabbage(1))
    lunchbox.Attach(Meal(2))

    lunchbox.Accept(NormalVisitor())
}

