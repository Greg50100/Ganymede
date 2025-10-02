package com.joviansapps.ganymede.data

/**
 * Événements émis par la calculatrice pour les effets de bord (side effects)
 * Ces événements permettent de séparer la logique métier des interactions UI
 */
sealed class CalculatorEvent {

    // === Événements de feedback utilisateur ===
    /**
     * Vibration tactile pour feedback
     */
    data class HapticFeedback(val type: HapticType = HapticType.LIGHT) : CalculatorEvent()

    /**
     * Affichage d'un message à l'utilisateur
     */
    data class ShowMessage(
        val message: String,
        val type: MessageType = MessageType.INFO,
        val duration: MessageDuration = MessageDuration.SHORT
    ) : CalculatorEvent()

    /**
     * Son de clic pour les boutons
     */
    data class PlaySound(val type: SoundType = SoundType.CLICK) : CalculatorEvent()

    // === Événements de navigation ===
    /**
     * Navigation vers l'historique
     */
    data object NavigateToHistory : CalculatorEvent()

    /**
     * Navigation vers les paramètres
     */
    data object NavigateToSettings : CalculatorEvent()

    /**
     * Partager le résultat
     */
    data class ShareResult(val expression: String, val result: String) : CalculatorEvent()

    // === Événements de persistance ===
    /**
     * Sauvegarder l'historique
     */
    data class SaveToHistory(val item: CalculationHistoryItem) : CalculatorEvent()

    /**
     * Copier dans le presse-papier
     */
    data class CopyToClipboard(val text: String, val label: String = "Calcul") : CalculatorEvent()

    // === Événements d'erreur ===
    /**
     * Erreur de calcul
     */
    data class CalculationError(
        val error: Throwable,
        val expression: String = "",
        val context: String = ""
    ) : CalculatorEvent()

    /**
     * Erreur de validation
     */
    data class ValidationError(
        val field: String,
        val message: String,
        val suggestion: String? = null
    ) : CalculatorEvent()

    // === Événements d'animation ===
    /**
     * Animation du résultat
     */
    data class AnimateResult(val animationType: AnimationType = AnimationType.FADE_IN) : CalculatorEvent()

    /**
     * Animation d'erreur
     */
    data class AnimateError(val animationType: AnimationType = AnimationType.SHAKE) : CalculatorEvent()

    // === Événements de performance ===
    /**
     * Démarrage d'une opération longue
     */
    data class StartLongOperation(val operationName: String) : CalculatorEvent()

    /**
     * Fin d'une opération longue
     */
    data class EndLongOperation(val operationName: String, val duration: Long) : CalculatorEvent()
}

/**
 * Types de retour haptique
 */
enum class HapticType {
    LIGHT,      // Vibration légère
    MEDIUM,     // Vibration moyenne
    HEAVY,      // Vibration forte
    SUCCESS,    // Vibration de succès
    WARNING,    // Vibration d'avertissement
    ERROR       // Vibration d'erreur
}

/**
 * Types de messages utilisateur
 */
enum class MessageType {
    INFO,       // Information
    SUCCESS,    // Succès
    WARNING,    // Avertissement
    ERROR       // Erreur
}

/**
 * Durées d'affichage des messages
 */
enum class MessageDuration(val milliseconds: Long) {
    SHORT(2000),
    MEDIUM(3500),
    LONG(5000),
    INDEFINITE(-1)
}

/**
 * Types de sons
 */
enum class SoundType {
    CLICK,      // Son de clic
    SUCCESS,    // Son de succès
    ERROR,      // Son d'erreur
    BEEP        // Bip simple
}

/**
 * Types d'animations
 */
enum class AnimationType {
    FADE_IN,        // Apparition en fondu
    FADE_OUT,       // Disparition en fondu
    SLIDE_IN,       // Glissement d'entrée
    SLIDE_OUT,      // Glissement de sortie
    BOUNCE,         // Rebond
    SHAKE,          // Tremblement
    PULSE,          // Pulsation
    ZOOM_IN,        // Zoom avant
    ZOOM_OUT        // Zoom arrière
}

/**
 * Gestionnaire d'événements pour traiter les effets de bord
 */
interface CalculatorEventHandler {
    /**
     * Traite un événement calculatrice
     */
    suspend fun handleEvent(event: CalculatorEvent)

    /**
     * Traite une liste d'événements
     */
    suspend fun handleEvents(events: List<CalculatorEvent>) {
        events.forEach { handleEvent(it) }
    }
}

/**
 * Contexte d'un événement pour le debugging et la télémétrie
 */
data class EventContext(
    val timestamp: Long = System.currentTimeMillis(),
    val screenName: String = "",
    val userAction: String = "",
    val sessionId: String = "",
    val additionalData: Map<String, Any> = emptyMap()
) {
    /**
     * Ajoute des données supplémentaires au contexte
     */
    fun withData(key: String, value: Any): EventContext = copy(
        additionalData = additionalData + (key to value)
    )

    /**
     * Ajoute plusieurs données au contexte
     */
    fun withData(data: Map<String, Any>): EventContext = copy(
        additionalData = additionalData + data
    )
}

/**
 * Événement enrichi avec son contexte
 */
data class ContextualEvent(
    val event: CalculatorEvent,
    val context: EventContext = EventContext()
) {
    /**
     * Crée un événement contextuel avec des données supplémentaires
     */
    companion object {
        fun create(
            event: CalculatorEvent,
            screenName: String = "",
            userAction: String = "",
            additionalData: Map<String, Any> = emptyMap()
        ): ContextualEvent = ContextualEvent(
            event = event,
            context = EventContext(
                screenName = screenName,
                userAction = userAction,
                additionalData = additionalData
            )
        )
    }
}