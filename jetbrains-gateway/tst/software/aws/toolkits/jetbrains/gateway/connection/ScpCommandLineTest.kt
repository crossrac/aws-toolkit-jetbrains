// Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.gateway.connection

import com.intellij.execution.util.ExecUtil
import com.intellij.testFramework.ApplicationRule
import com.intellij.util.io.readBytes
import org.apache.sshd.common.session.Session
import org.apache.sshd.scp.common.ScpTransferEventListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.util.UUID

class ScpCommandLineTest {
    @Rule
    @JvmField
    val applicationRule = ApplicationRule()

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Rule
    @JvmField
    val sshServer = SshServerRule(tempFolder)

    @Test
    fun `build command`() {
        val sut = ScpCommandLine(
            "localhost",
            "/path",
            recursive = true,
            port = 12321
        )

        sut.addLocalPaths("local1", "local2")

        assertThat(sut.constructCommandLine().commandLineString).endsWith("-P 12321 local1 local2 localhost:/path")
    }

    @Test
    fun `copies 1 file`() {
        val file = newFile()

        val sut = ScpCommandLine(
            "localhost",
            tempFolder.newFolder().absolutePath,
            recursive = false,
            port = sshServer.server.port
        )
            .addLocalPaths(file)

        val paths = sut.executeScpTest()

        assertThat(paths.size).isEqualTo(1)
        assertThat(paths.first()!!.readBytes()).isEqualTo(file.readBytes())
    }

    @Test
    fun `copies 2 files`() {
        val file1 = newFile()
        val file2 = newFile()

        val sut = ScpCommandLine(
            "localhost",
            tempFolder.newFolder().absolutePath,
            recursive = false,
            port = sshServer.server.port
        )
            .addLocalPaths(file1, file2)

        val paths = sut.executeScpTest()

        assertThat(paths.size).isEqualTo(2)
        assertThat(paths.first()!!.readBytes()).isEqualTo(file1.readBytes())
        assertThat(paths.last()!!.readBytes()).isEqualTo(file2.readBytes())
    }

    @Test
    fun `copies directory recursively`() {
        val directory = tempFolder.newFolder()
        val uuid = UUID.randomUUID().toString()
        directory.resolve(uuid).createNewFile()

        val destination = tempFolder.newFolder()
        val sut = ScpCommandLine(
            "localhost",
            destination.toString(),
            recursive = true,
            port = sshServer.server.port
        )
            .addLocalPaths(directory.toPath())

        val paths = sut.executeScpTest()

        assertThat(paths.size).isEqualTo(2)
        // creates folder because it doesn't exist
        assertThat(paths.first()!!.fileName.toString()).isEqualTo(destination.name)
        // and then the second file
        assertThat(paths.last()!!.fileName.toString()).isEqualTo(uuid)
        // ...which is nested
        assertThat(paths.last()!!.parent.fileName.toString()).isEqualTo(directory.name)
    }

    private fun ScpCommandLine.executeScpTest(): List<Path?> {
        val paths = mutableListOf<Path?>()
        attachScpListener(paths)

        ExecUtil.execAndGetOutput(
            this.knownHostsLocation(tempFolder.newFile().toPath())
                .constructCommandLine()
        )

        return paths
    }

    private fun newFile(): Path {
        val text = UUID.randomUUID()
        val file = tempFolder.newFile()
        file.writeText(text.toString())

        return file.toPath()
    }

    private fun attachScpListener(pathsCollector: MutableList<Path?>) {
        sshServer.scpCommandFactory.addEventListener(object : ScpTransferEventListener {
            override fun startFileEvent(
                session: Session?,
                op: ScpTransferEventListener.FileOperation?,
                file: Path?,
                length: Long,
                perms: MutableSet<PosixFilePermission>?
            ) {
                pathsCollector.add(file)
            }

            override fun startFolderEvent(
                session: Session?,
                op: ScpTransferEventListener.FileOperation?,
                file: Path?,
                perms: MutableSet<PosixFilePermission>?
            ) {
                pathsCollector.add(file)
            }
        })
    }
}
