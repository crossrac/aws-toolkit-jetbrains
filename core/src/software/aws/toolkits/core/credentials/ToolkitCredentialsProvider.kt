package software.aws.toolkits.core.credentials

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider

abstract class ToolkitCredentialsProvider : AwsCredentialsProvider {
    /**
     * The ID should be unique across all [ToolkitCredentialsProvider].
     * It is recommended to concatenate the factory type and the display name.
     */
    abstract val id: String

    /**
     * A user friendly display name shown in the UI.
     */
    abstract val displayName: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ToolkitCredentialsProvider

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "${this::class.simpleName}(id='$id')"
    }
}

/**
 * The class for managing [ToolkitCredentialsProvider] of the same type.
 * @property type The internal ID for this type of [ToolkitCredentialsProvider], eg 'profile' for AWS account whose credentials is stored in the profile file.
 */
abstract class ToolkitCredentialsProviderFactory(
    val type: String
) {
    private val providers = mutableMapOf<String, ToolkitCredentialsProvider>()

    protected fun add(provider: ToolkitCredentialsProvider) {
        providers[provider.id] = provider
    }

    protected fun clear() {
        providers.clear()
    }

    fun listCredentialProviders() = providers.values

    fun get(id: String) = providers[id]

    /**
     * Called when the [ToolkitCredentialsProviderManager] is shutting down to allow for resource clean up
     */
    open fun shutDown() {}
}