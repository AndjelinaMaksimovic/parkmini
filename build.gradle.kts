// Plugin versions of all modules live here, the modules only apply them.
plugins {
    kotlin("jvm") version "2.3.21" apply false
    kotlin("multiplatform") version "2.3.21" apply false
    kotlin("plugin.serialization") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
    id("com.android.application") version "8.13.1" apply false
    id("com.android.kotlin.multiplatform.library") version "8.13.1" apply false
}
