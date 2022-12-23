package aoc2022.day17

interface Shape {
    fun getPoints(): List<Pair<Int, Int>>
    fun getExterior(): List<Pair<Int, Int>>
    fun top(): Int
    fun bottom(): Int
    fun getNext(h: Int): Shape
    var x: Int
    var y: Int
    val index: Int

    fun tryMoveDown(occupiedSpaces: Map<Int, List<Int>>): Boolean {
        if (this.bottom() == 1)
            return true
        this.y--
        if (getPoints().any { occupiedSpaces.getOrDefault(it.second, listOf()).contains(it.first) }) {
            this.y++
            return true
        }
        return false
    }

    fun tryMove(mov: Int, occupiedSpaces: Map<Int, List<Int>>) {
        this.x += mov
        val ext = getPoints()
        if(ext.any{it.first == -1 || it.first == 7} || ext.any { occupiedSpaces.getOrDefault(it.second, listOf()).contains(it.first) }) {
            this.x -= mov
        }
    }
}

class DashBlock(override var x: Int, override var y: Int) : Shape {

    override val index: Int
        get() = 0

    override fun getPoints(): List<Pair<Int, Int>> {
        return (0..3).map { x + it to y }
    }

    override fun getExterior(): List<Pair<Int, Int>> {
        return getPoints()
    }

    override fun top(): Int {
        return y
    }

    override fun bottom(): Int {
        return y
    }

    override fun getNext(h: Int): Shape {
        return PlusBlock(3, h + 5)
    }
}

class PlusBlock(override var x: Int, override var y: Int) : Shape {

    override val index: Int
        get() = 1

    override fun getPoints(): List<Pair<Int, Int>> {
        return listOf(
            x to y,
            x - 1 to y,
            x + 1 to y,
            x to y - 1,
            x to y + 1
        )
    }

    override fun getExterior(): List<Pair<Int, Int>> {
        return listOf(
            x - 1 to y,
            x + 1 to y,
            x to y - 1,
            x to y + 1
        )
    }

    override fun top(): Int {
        return y + 1
    }

    override fun bottom(): Int {
        return y - 1
    }

    override fun getNext(h: Int): Shape {
        return InverseLBlock(2, h + 4)
    }
}

class InverseLBlock(override var x: Int, override var y: Int) : Shape {

    override val index: Int
        get() = 2

    override fun getPoints(): List<Pair<Int, Int>> {
        return listOf(
            x to y,
            x + 1 to y,
            x + 2 to y,
            x + 2 to y + 1,
            x + 2 to y + 2
        )
    }

    override fun getExterior(): List<Pair<Int, Int>> {
        return listOf(
            x to y,
            x + 1 to y,
            x + 2 to y,
            x + 2 to y + 1,
            x + 2 to y + 2
        )
    }

    override fun top(): Int {
        return y + 2
    }

    override fun bottom(): Int {
        return y
    }

    override fun getNext(h: Int): Shape {
        return IBlock(2, h + 4)
    }
}

class IBlock(override var x: Int, override var y: Int) : Shape {

    override val index: Int
        get() = 3

    override fun getPoints(): List<Pair<Int, Int>> {
        return (0..3).map { x to y + it }
    }

    override fun getExterior(): List<Pair<Int, Int>> {
        return getPoints()
    }

    override fun top(): Int {
        return y + 3
    }

    override fun bottom(): Int {
        return y
    }

    override fun getNext(h: Int): Shape {
        return SquareBlock(2, h + 4)
    }
}

class SquareBlock(override var x: Int, override var y: Int) : Shape {

    override val index: Int
        get() = 4

    override fun getPoints(): List<Pair<Int, Int>> {
        return listOf(
            x to y,
            x + 1 to y,
            x to y + 1,
            x + 1 to y + 1
        )
    }

    override fun getExterior(): List<Pair<Int, Int>> {
        return getPoints()
    }

    override fun top(): Int {
        return y + 1
    }

    override fun bottom(): Int {
        return y
    }

    override fun getNext(h: Int): Shape {
        return DashBlock(2, h + 4)
    }
}