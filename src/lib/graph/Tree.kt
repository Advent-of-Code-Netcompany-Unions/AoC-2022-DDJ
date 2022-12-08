package lib.graph

interface Tree<T> {
    val contents: T
    val children: MutableList<Tree<T>>
    val parent: Tree<T>?
    val root: Root<T>
    val nodes: Sequence<Tree<T>>

    fun isLeaf(): Boolean

    fun computeNodes(): Sequence<Tree<T>> {
        val self = this
        return sequence {
            yield(self)
            yieldAll(children.flatMap { it.nodes })
        }
    }
}

class Root<T>(override val contents: T, override val children: MutableList<Tree<T>>) : Tree<T> {

    override val parent: Tree<T>?
        get() = null

    override val root: Root<T>
        get() = this

    override fun isLeaf(): Boolean {
        return this.children.any()
    }

    override val nodes: Sequence<Tree<T>> by lazy { computeNodes() }
}

class Leaf<T>(
    override val contents: T, override val parent: Tree<T>
) : Tree<T> {

    override val root: Root<T> by lazy { parent.root }

    override val children: MutableList<Tree<T>>
        get() = mutableListOf()

    override val nodes: Sequence<Tree<T>>
        get() = sequenceOf(this)

    override fun isLeaf(): Boolean {
        return true
    }
}

class Node<T>(
    override val contents: T,
    override val children: MutableList<Tree<T>>,
    override val parent: Tree<T>,
) : Tree<T> {

    override val root: Root<T> by lazy { parent.root }
    override val nodes: Sequence<Tree<T>> by lazy { computeNodes() }

    override fun isLeaf(): Boolean {
        return false
    }
}