package com.jetbrains.micropython.devices

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.jetbrains.micropython.run.MicroPythonRunConfiguration
import com.jetbrains.micropython.settings.MicroPythonFacet
import com.jetbrains.micropython.settings.MicroPythonTypeHints
import com.jetbrains.micropython.settings.MicroPythonUsbId
import com.jetbrains.micropython.settings.microPythonFacet
import com.jetbrains.python.packaging.PyRequirement

/**
 * @author vlan
 */
class WemosD1MiniDeviceProvider : MicroPythonDeviceProvider {
  override val persistentName: String
    get() = "WEMOS D1 mini"

  override val documentationURL: String
    get() = "https://github.com/vlasovskikh/intellij-micropython/wiki/WEMOS-D1-mini"

  override val usbId: MicroPythonUsbId?
    get() = MicroPythonUsbId(0x1A86, 0x7523)

  override val typeHints: MicroPythonTypeHints by lazy {
    MicroPythonTypeHints("esp8266/latest")
  }

  override val packageRequirements: List<PyRequirement> by lazy {
    PyRequirement.fromText("""pyserial>=3.3,<3.4""")
  }

  override fun getRunCommandLineState(configuration: MicroPythonRunConfiguration,
                                      environment: ExecutionEnvironment): CommandLineState? {
    val facet = configuration.module?.microPythonFacet ?: return null
    val pythonPath = facet.pythonPath ?: return null
    val devicePath = facet.devicePath ?: return null
    val rootPath = configuration.project.basePath ?: return null
    return object : CommandLineState(environment) {
      override fun startProcess() =
          OSProcessHandler(GeneralCommandLine(pythonPath,
                                              "${MicroPythonFacet.scriptsPath}/microupload.py",
                                              "-C",
                                              rootPath,
                                              "-v",
                                              devicePath,
                                              configuration.path))
    }
  }
}