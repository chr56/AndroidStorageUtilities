/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.textparser.ExternalFilePathParser
import java.io.File

/**
 * relative file path from _the root of a storage volume_
 */
fun File.getBasePath(): String = externalFileBashPath(absolutePath)

@Deprecated(
    "Moved to ExternalFilePathParser",
    ReplaceWith("ExternalFilePathParser.bashPath(absolutePath)", "lib.storage.textparser.ExternalFilePathParser")
)
fun externalFileBashPath(absolutePath: String): String = ExternalFilePathParser.bashPath(absolutePath)
    ?: throw IllegalArgumentException("Unsupported Path: $absolutePath")