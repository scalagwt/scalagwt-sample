/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.showcase.generator

import com.google.gwt.core.ext.Generator
import com.google.gwt.core.ext.GeneratorContext
import com.google.gwt.core.ext.TreeLogger
import com.google.gwt.core.ext.UnableToCompleteException
import com.google.gwt.core.ext.typeinfo.JClassType
import com.google.gwt.core.ext.typeinfo.NotFoundException
import com.google.gwt.sample.showcase.client.ContentWidget
import com.google.gwt.sample.showcase.client.ShowcaseConstants
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseRaw
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseStyle

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.LinkedHashMap
import java.util.Map
import scala.collection.JavaConversions._

/**
 * Generate the source code, css styles, and raw source used in the Showcase
 * examples.
 */
class ShowcaseGenerator extends Generator {

  /**
   * The paths to the CSS style sheets used in Showcase. The paths are relative
   * to the root path of the {@link ClassLoader}. The variable $THEME will be
   * replaced by the current theme. An extension of "_rtl.css" will be used for
   * RTL mode.
   */
  private val SRC_CSS = List(
      "com/google/gwt/user/theme/$THEME/public/gwt/$THEME/$THEME.css",
      "com/google/gwt/sample/showcase/public/$THEME/Showcase.css")

  /**
   * The class loader used to get resources.
   */
  private var classLoader: ClassLoader = null

  /**
   * The generator context.
   */
  private var context: GeneratorContext = null

  /**
   * The {@link TreeLogger} used to log messages.
   */
  private var logger: TreeLogger = null

  @Override
  def generate(logger: TreeLogger, context: GeneratorContext, typeName: String): String = {
    this.logger = logger
    this.context = context
    this.classLoader = Thread.currentThread().getContextClassLoader()

    // Only generate files on the first permutation
    if (!isFirstPass()) {
      return null
    }

    // Get the Showcase ContentWidget subtypes to examine
    var cwType: JClassType  = null
    try {
      cwType = context.getTypeOracle().getType(classOf[ContentWidget].getName())
    } catch {
      case e: NotFoundException => {
        logger.log(TreeLogger.ERROR, "Cannot find ContentWidget class", e)
        throw new UnableToCompleteException()
      }
    }
    val types = cwType.getSubtypes()

    // Generate the source and raw source files
    for (typ <- types) {
      generateRawFiles(typ)
      generateSourceFiles(typ)
    }

    // Generate the CSS source files
    for (theme <- ShowcaseConstants.STYLE_THEMES) {
      val styleDefsLTR = getStyleDefinitions(theme, false)
      val styleDefsRTL = getStyleDefinitions(theme, true)
      val outDirLTR = ShowcaseConstants.DST_SOURCE_STYLE + theme + "/"
      val outDirRTL = ShowcaseConstants.DST_SOURCE_STYLE + theme + "_rtl/"
      for (typ <- types) {
        generateStyleFiles(typ, styleDefsLTR, outDirLTR)
        generateStyleFiles(typ, styleDefsRTL, outDirRTL)
      }
    }

    return null
  }

  /**
   * Set the full contents of a resource in the public directory.
   * 
   * @param partialPath the path to the file relative to the public directory
   * @param contents the file contents
   */
  private def createPublicResource(partialPath: String, contents: String) {
    try {
      val outStream = context.tryCreateResource(logger, partialPath)
      outStream.write(contents.getBytes())
      context.commitResource(logger, outStream)
    } catch {
      case e: UnableToCompleteException => logger.log(TreeLogger.ERROR, "Failed while writing", e)
      case e: IOException => logger.log(TreeLogger.ERROR, "Failed while writing", e)
    }
  }

  /**
   * Generate the raw files used by a {@link ContentWidget}.
   * 
   * @param type the {@link ContentWidget} subclass
   */
  private def generateRawFiles(typ: JClassType): Unit = {
    // Look for annotation
    if (!typ.isAnnotationPresent(classOf[ShowcaseRaw])) {
      return
    }

    // Get the package info
    val pkgName = typ.getPackage().getName()
    val pkgPath = pkgName.replace('.', '/') + "/"

    // Generate each raw source file
    val filenames = typ.getAnnotation(classOf[ShowcaseRaw]).value()
    for (filename <- filenames) {
      // Get the file contents
      var fileContents = getResourceContents(pkgPath + filename)

      // Make the source pretty
      fileContents = fileContents.replace("<", "&lt;")
      fileContents = fileContents.replace(">", "&gt;")
      fileContents = fileContents.replace("* \n   */\n", "*/\n")
      fileContents = "<pre>" + fileContents + "</pre>"

      // Save the raw source in the public directory
      val dstPath = ShowcaseConstants.DST_SOURCE_RAW + filename + ".html"
      createPublicResource(dstPath, fileContents)
    }
  }

  /**
   * Generate the formatted source code for a {@link ContentWidget}.
   * 
   * @param type the {@link ContentWidget} subclass
   */
  private def generateSourceFiles(typ: JClassType): Unit = {
    // Get the file contents
    val filename = typ.getQualifiedSourceName().replace('.', '/') + ".java"
    val fileContents = getResourceContents(filename)

    // Get each data code block
    var formattedSource = ""
    val dataTag = "@" + classOf[ShowcaseData].getSimpleName()
    val sourceTag = "@" + classOf[ShowcaseSource].getSimpleName()
    var dataTagIndex = fileContents.indexOf(dataTag)
    var srcTagIndex = fileContents.indexOf(sourceTag)
    while (dataTagIndex >= 0 || srcTagIndex >= 0) {
      if (dataTagIndex >= 0 && (dataTagIndex < srcTagIndex || srcTagIndex < 0)) {
        // Get the boundaries of a DATA tag
        val beginIndex = fileContents.lastIndexOf("  /*", dataTagIndex)
        val beginTagIndex = fileContents.lastIndexOf("\n", dataTagIndex) + 1
        val endTagIndex = fileContents.indexOf("\n", dataTagIndex) + 1
        val endIndex = fileContents.indexOf(";", beginIndex) + 1

        if (beginIndex < beginTagIndex && endTagIndex < endIndex) {
          // Add to the formatted source
          val srcData = fileContents.substring(beginIndex, beginTagIndex) + 
            fileContents.substring(endTagIndex, endIndex)
          formattedSource += srcData + "\n\n"
        }

        // Get next tag
        dataTagIndex = if (endIndex < dataTagIndex) -1 else fileContents.indexOf(dataTag, endIndex + 1)
      } else {
        // Get the boundaries of a SRC tag
        val beginIndex = fileContents.lastIndexOf("/*", srcTagIndex) - 2
        val beginTagIndex = fileContents.lastIndexOf("\n", srcTagIndex) + 1
        val endTagIndex = fileContents.indexOf("\n", srcTagIndex) + 1
        val endIndex = fileContents.indexOf("\n  }", beginIndex) + 4

        // Add to the formatted source
        val srcCode = fileContents.substring(beginIndex, beginTagIndex) +
          fileContents.substring(endTagIndex, endIndex)
        formattedSource += srcCode + "\n\n"

        // Get the next tag
        srcTagIndex = fileContents.indexOf(sourceTag, endIndex + 1)
      }
    }

    // Make the source pretty
    formattedSource = formattedSource.replace("<", "&lt;")
    formattedSource = formattedSource.replace(">", "&gt;")
    formattedSource = formattedSource.replace("* \n   */\n", "*/\n")
    formattedSource = """<pre class="prettyprint">""" + formattedSource + "</pre>"

    // Save the source code to a file
    val dstPath = ShowcaseConstants.DST_SOURCE_EXAMPLE +
      typ.getSimpleSourceName() + ".html"
    createPublicResource(dstPath, formattedSource)
  }

  /**
   * Generate the styles used by a {@link ContentWidget}.
   * 
   * @param type the {@link ContentWidget} subclass
   * @param styleDefs the concatenated style definitions
   * @param outDir the output directory
   */
  private def generateStyleFiles(typ: JClassType , styleDefs: String, outDir: String) {
    // Look for annotation
    if (!typ.isAnnotationPresent(classOf[ShowcaseStyle])) {
      return
    }

    // Generate a style file for each theme/RTL mode pair
    val prefixes = typ.getAnnotation(classOf[ShowcaseStyle]).value
    val matched = new LinkedHashMap[String, String]()
    for (prefix <- prefixes) {
      // Get the start location of the style code in the source file
      var foundStyle = false
      var start = styleDefs.indexOf("\n" + prefix)
      while (start >= 0) {
        // Get the cssContents string name pattern
        var end = styleDefs.indexOf("{", start)
        val matchedName = styleDefs.substring(start, end).trim()

        // Get the style code
        end = styleDefs.indexOf("}", start) + 1
        val styleDef = "<pre>" + styleDefs.substring(start, end) + "</pre>"
        matched.put(matchedName, styleDef)

        // Goto the next match
        foundStyle = true
        start = styleDefs.indexOf("\n" + prefix, end)
      }

      // No style exists
      if (!foundStyle) {
        matched.put(prefix, "<pre>" + prefix + " {\n}</pre>")
      }
    }

    // Combine all of the styles into one formatted string
    var formattedStyle = ""
    for (styleDef <- matched.values()) {
      formattedStyle += styleDef
    }

    // Save the raw source in the public directory
    val dstPath = outDir + typ.getSimpleSourceName() + ".html"
    createPublicResource(dstPath, formattedStyle)
  }

  /**
   * Get the full contents of a resource.
   * 
   * @param path the path to the resource
   * @return the contents of the resource
   */
  private def getResourceContents(path: String): String = {
    var in = classLoader.getResourceAsStream(path)
    if (in == null) {
      if (path.endsWith(".java")) {
        in = classLoader.getResourceAsStream(path.replace(".java", ".scala"))
      }
      if (in == null) {
        logger.log(TreeLogger.ERROR, "Resource not found: " + path)
        throw new UnableToCompleteException()
      }
    }

    val fileContentsBuf = new StringBuffer()
    var br: BufferedReader = null
    try {
      br = new BufferedReader(new InputStreamReader(in))
      var stop = false
      while (!stop) {
        val temp = br.readLine()
        if (temp != null) {
          fileContentsBuf.append(temp).append('\n')
        } else {
          stop = true
        }
      }
    } catch { 
      case e: IOException => {
        logger.log(TreeLogger.ERROR, "Cannot read resource", e)
        throw new UnableToCompleteException()
      }
    } finally {
      if (br != null) {
        try {
          br.close()
        } catch {
          case e: IOException =>
        }
      }
    }

    // Return the file contents as a string
    return fileContentsBuf.toString()
  }

  /**
   * Load the contents of all CSS files used in the Showcase for a given
   * theme/RTL mode, concatenated into one string.
   * 
   * @param theme the style theme
   * @param isRTL true if RTL mode
   * @return the concatenated styles
   */
  private def getStyleDefinitions(theme: String, isRTL: Boolean): String = {
    var cssContents = ""
    for (_path <- SRC_CSS) {
      var path = _path.replace("$THEME", theme)
      if (isRTL) {
        path = path.replace(".css", "_rtl.css")
      }
      cssContents += getResourceContents(path) + "\n\n"
    }
    return cssContents
  }

  /**
   * Ensure that we only generate files once by creating a placeholder file,
   * then looking for it on subsequent generates.
   * 
   * @return true if this is the first pass, false if not
   */
  private def isFirstPass(): Boolean = {
    val placeholder = ShowcaseConstants.DST_SOURCE + "generated"
    try {
      val outStream = context.tryCreateResource(logger, placeholder)
      if (outStream == null) {
        return false
      } else {
        context.commitResource(logger, outStream)
      }
    } catch {
      case e: UnableToCompleteException => {
        logger.log(TreeLogger.ERROR, "Unable to generate", e)
        return false
      }
    }
    return true
  }
}
