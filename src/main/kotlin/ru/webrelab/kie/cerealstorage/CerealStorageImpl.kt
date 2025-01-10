package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    /**
     * Блок инициализации класса.
     * Выполняется сразу при создании объекта
     */
    init {
        require(containerCapacity >= 0) {
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) { "Cannot add negative amount of cereal" }
        check((cereal in storage) || (storageCapacity - storage.size * containerCapacity >= containerCapacity))
        { "Cannot add another container" }
        val cerealAmount = (storage[cereal] ?: 0f) + amount
        storage[cereal] = minOf(cerealAmount, containerCapacity)
        return maxOf(cerealAmount - containerCapacity, 0f)
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount > 0) { "Cannot get negative amount" }
        val amountObtained = checkNotNull(storage[cereal]).let {
            if (it >= amount) amount else it
        }
        storage[cereal] = (storage[cereal] ?: 0f) - amountObtained
        return amountObtained
    }

    override fun removeContainer(cereal: Cereal): Boolean {
        val amount = checkNotNull(storage[cereal]) { "No such container" }
        return if (amount == 0f) (checkNotNull(storage.remove(cereal)) == 0f) else false
    }

    override fun getAmount(cereal: Cereal): Float {
        return checkNotNull(storage[cereal]) { "There is no such container" }
    }

    override fun getSpace(cereal: Cereal): Float {
        return containerCapacity - checkNotNull(storage[cereal]) { "There is no such container" }
    }

    override fun toString(): String {
        val storageContent =
            if (storage.isNotEmpty()) storage.entries.joinToString(separator = ", ") { "${it.key}: ${it.value}" }
            else "Storage is empty"
        return """Storage:
            |   container capacity: $containerCapacity
            |   storage capacity: $storageCapacity
            |   Storage contains:
            |   $storageContent"""
    }
}
