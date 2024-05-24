/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.textparser

object DocumentUriPathParser {


    /**
     * Resolve StorageVolume id from Document Content Uri
     * @return StorageVolume id (null if broken input)
     */
    @JvmStatic
    fun storageVolumeId(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        return parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_DOCUMENT)
            ?: parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_TREE)
    }


    /**
     * @param segments system Document Uri path segment (`content://com.android.externalstorage.documents/document/<StorageVolume>:<BasePath>#...`)
     * @return base path (relative file path from _the root of a storage volume_)
     * @see documentTreeUriBasePath
     * @see documentUriBasePath
     * @see childDocumentUriBasePath
     */
    @JvmStatic
    fun documentUriBasePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        return parseBasePath(map, DOCUMENT_PROVIDER_PATH_DOCUMENT)
    }

    /**
     * @param segments system Tree Document Uri path segment (`content://com.android.externalstorage.documents/tree/<StorageVolume>:<BasePath>#...`)
     * @return base path (relative file path from _the root of a storage volume_)
     * @see documentTreeUriBasePath
     * @see documentUriBasePath
     * @see childDocumentUriBasePath
     */
    @JvmStatic
    fun documentTreeUriBasePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        return parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE)
    }

    /**
     * @param segments system Child Document Uri path segment (`content://com.android.externalstorage.documents/tree/<StorageVolume>:<BasePath>/document/<StorageVolume>:<BasePath>#...`)
     * @return base path (relative file path from _the root of a storage volume_)
     * @see documentTreeUriBasePath
     * @see documentUriBasePath
     * @see childDocumentUriBasePath
     */
    @JvmStatic
    fun childDocumentUriBasePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        val document = parseBasePath(map, DOCUMENT_PROVIDER_PATH_DOCUMENT)
        val tree = parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE)
        return document ?: tree
    }

    /**
     * Resolve BasePath id from Document Content Uri
     * @return BasePath (relative path from _the root of a storage volume_)
     */
    @JvmStatic
    fun basePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        return parseBasePath(map, DOCUMENT_PROVIDER_PATH_DOCUMENT)
            ?: parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE)
    }


    /**
     * @param segments system Child Document Uri path segment (`content://com.android.externalstorage.documents/document/<StorageVolume>:<BasePath>#...`)
     * @return absolute path
     * @see documentTreeUriAbsolutePath
     * @see documentUriAbsolutePath
     * @see childDocumentUriAbsolutePath
     */
    @JvmStatic
    fun documentUriAbsolutePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        val basePath = parseBasePath(map, DOCUMENT_PROVIDER_PATH_DOCUMENT) ?: return null
        val storageVolumeId = parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_DOCUMENT) ?: return null
        return buildAbsolutePath(storageVolumeId, basePath)
    }

    /**
     * @param segments system Child Document Uri path segment (`content://com.android.externalstorage.documents/tree/<StorageVolume>:<BasePath>#...`)
     * @return absolute path
     * @see documentTreeUriAbsolutePath
     * @see documentUriAbsolutePath
     * @see childDocumentUriAbsolutePath
     */
    @JvmStatic
    fun documentTreeUriAbsolutePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        val basePath = parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE) ?: return null
        val storageVolumeId = parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_TREE) ?: return null
        return buildAbsolutePath(storageVolumeId, basePath)
    }

    /**
     * @param segments system Child Document Uri path segment (`content://com.android.externalstorage.documents/tree/<StorageVolume>:<BasePath>/document/<StorageVolume>:<BasePath>#...`)
     * @return absolute path
     * @see documentTreeUriAbsolutePath
     * @see documentUriAbsolutePath
     * @see childDocumentUriAbsolutePath
     */
    @JvmStatic
    fun childDocumentUriAbsolutePath(segments: List<String>): String? {
        val map = groupPathSegment(segments)
        val treeBasePath = parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE)
        val documentBasePath = parseBasePath(map, DOCUMENT_PROVIDER_PATH_TREE)
        val storageVolumeId =
            parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_TREE)
                ?: parseStorageVolumeId(map, DOCUMENT_PROVIDER_PATH_DOCUMENT) ?: return null
        return buildAbsolutePath(storageVolumeId, documentBasePath ?: treeBasePath ?: "")
    }


    @JvmStatic
    internal fun parseStorageVolumeId(resolvedPath: Map<String, String>, key: String): String? {
        return resolvedPath.getOrElse(key) { return null }
            .substringBefore(':', "")
            .takeIf { it.isNotEmpty() }
    }
    @JvmStatic
    internal fun parseBasePath(resolvedPath: Map<String, String>, key: String): String? {
        return resolvedPath.getOrElse(key) { return null }
            .substringAfter(':', "")
            .takeIf { it.isNotEmpty() }
    }

    @JvmStatic
    internal fun groupPathSegment(pathSegments: List<String>): Map<String, String> =
        if (pathSegments.size % 2 == 0) {
            val groups = pathSegments.chunked(2)
            groups.associate { it[0] to it[1] }
        } else {
            emptyMap()
        }
    
    @JvmStatic
    internal fun buildAbsolutePath(storageVolumeId: String, basePath: String): String {
        return if (storageVolumeId == STORAGE_VOLUME_PRIMARY) {
            "${ExternalFilePathParser.primaryExternalStoragePath}/$basePath"
        } else {
            "/storage/$storageVolumeId/$basePath"
        }
    }
}