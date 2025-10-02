package com.joviansapps.ganymede.core.common

/**
 * Classe sealed pour représenter les différents états de résultat d'opérations asynchrones
 *
 * @param T le type de données en cas de succès
 */
sealed class Result<out T> {
    /**
     * Représente un résultat réussi avec des données
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Représente un échec avec une exception
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Représente un état de chargement
     */
    data object Loading : Result<Nothing>()

    /**
     * Indique si le résultat est un succès
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Indique si le résultat est une erreur
     */
    val isError: Boolean get() = this is Error

    /**
     * Indique si le résultat est en chargement
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Récupère les données ou null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Récupère l'exception ou null
     */
    fun exceptionOrNull(): Throwable? = when (this) {
        is Error -> exception
        else -> null
    }
}

/**
 * Extensions utiles pour Result
 */
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (exception: Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

/**
 * Transforme une exception en Result.Error
 */
fun <T> Throwable.asError(): Result<T> = Result.Error(this)

/**
 * Transforme des données en Result.Success
 */
fun <T> T.asSuccess(): Result<T> = Result.Success(this)

/**
 * Fonction d'aide pour capturer les exceptions et les convertir en Result
 */
inline fun <T> resultOf(action: () -> T): Result<T> = try {
    Result.Success(action())
} catch (e: Throwable) {
    Result.Error(e)
}

/**
 * Transforme un Result<T> en Result<R>
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> resultOf { transform(data) }
    is Result.Error -> this
    is Result.Loading -> this
}

/**
 * Combine deux Results
 */
inline fun <T, R, V> Result<T>.combine(
    other: Result<R>,
    transform: (T, R) -> V
): Result<V> = when {
    this is Result.Success && other is Result.Success ->
        resultOf { transform(data, other.data) }
    this is Result.Error -> this
    other is Result.Error -> other
    else -> Result.Loading
}

/**
 * Types d'erreurs spécifiques à l'application
 */
sealed class GanymedeError : Exception() {
    object NetworkError : GanymedeError()
    object DatabaseError : GanymedeError()
    data class ValidationError(override val message: String) : GanymedeError()
    data class CalculationError(override val message: String) : GanymedeError()
    object UnknownError : GanymedeError()
}
