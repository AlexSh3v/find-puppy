package com.alexsh3v.findpuppy.game

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class Tile(type: Type = Type.Neutral) : java.io.Serializable {
    private var _i: MutableStateFlow<Int>
    val i: Flow<Int>
        get() = _i
    private var _j: MutableStateFlow<Int>
    val j: Flow<Int>
        get() = _j

    fun getPositionPair(): Pair<Int, Int> = Pair(_i.value, _j.value)

    private var _state = MutableStateFlow(State.Hidden)
    val state: Flow<State>
        get() = _state

    fun changeState(state: State) {
        _state.value = state
    }


    private var _type: MutableStateFlow<Type>
    val type: Flow<Type>
        get() = _type

    fun changeType(type: Type) {
        _type.value = type
    }

    fun getType(): Type = _type.value

    init {
        this._type = MutableStateFlow(type)
        this._i = MutableStateFlow(0)
        this._j = MutableStateFlow(0)
        this._state = MutableStateFlow(State.Hidden)
    }

    companion object {
        const val ENEMY_NUMBER = 2
        const val DECORATION_NUMBER = 6
    }

    enum class Type {
        Neutral, WithPuppy,
        WithEnemyMan, WithEnemyWoman,
        Decoration, Dirt, LonelyTree, Bush1, Bush2, Bush3, Bush4, TribeOfTrees,
        WithItems
    }
    fun isDecoration(): Boolean {
        val t = getType()
        return t == Type.LonelyTree || t == Type.Bush1 || t == Type.Bush2
                || t == Type.Bush3 || t == Type.Bush4 || t == Type.TribeOfTrees
    }

    enum class State {
        Hidden, Shown
    }


    fun bindPosition(i: Int, j: Int) {
        this._i.value = i
        this._j.value = j
    }

    fun isEmpty(): Boolean {
        return _type.value == Type.Neutral
    }

    fun isEnemy(): Boolean {
        return _type.value in listOf(Type.WithEnemyMan, Type.WithEnemyWoman)
    }

}
