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


    enum class Type {
        Neutral, WithPuppy, WithEnemy, WithItems, Decoration, Empty
    }

    enum class State {
        HiddenActive, Hidden, ShownActive, Shown
    }


    fun bindPosition(i: Int, j: Int) {
        this._i.value = i
        this._j.value = j
    }


    fun open() {

    }

    fun isEmpty(): Boolean {
        return _type.value == Type.Neutral
    }


}
