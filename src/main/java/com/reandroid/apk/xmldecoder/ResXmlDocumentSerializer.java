/*
  *  Copyright (C) 2022 github.com/REAndroid
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.reandroid.apk.xmldecoder;

import com.reandroid.apk.ApkModule;
import com.reandroid.archive.InputSource;
import com.reandroid.arsc.chunk.PackageBlock;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.chunk.xml.ResXmlDocument;
import com.reandroid.arsc.chunk.xml.ResXmlPullParser;
import com.reandroid.xml.XMLFactory;
import com.reandroid.xml.XmlParserToSerializer;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResXmlDocumentSerializer implements ResXmlPullParser.DocumentLoadedListener{
    private final Object mLock = new Object();
    private final ResXmlPullParser parser;
    private final XmlSerializer serializer;
    private final XmlParserToSerializer parserToSerializer;
    private boolean fixAttributesNamespace;
    private String mCurrentPath;
    public ResXmlDocumentSerializer(ResXmlPullParser parser){
        this.parser = parser;
        this.serializer = XMLFactory.newSerializer();
        this.parserToSerializer = new XmlParserToSerializer(parser, serializer);
        this.parser.setDocumentLoadedListener(this);
    }
    public ResXmlDocumentSerializer(PackageBlock currentPackage){
        this(new ResXmlPullParser(currentPackage));
    }
    public ResXmlDocumentSerializer(ApkModule apkModule){
        this(pickPackage(apkModule));
    }
    public void setCurrentPackage(PackageBlock packageBlock){
        this.parser.setCurrentPackage(packageBlock);
    }

    public void write(InputSource inputSource, File file)
            throws IOException, XmlPullParserException {
        write(inputSource.openStream(), file);
    }
    public void write(InputSource inputSource, OutputStream outputStream)
            throws IOException, XmlPullParserException {
        write(inputSource.openStream(), outputStream);
        inputSource.disposeInputSource();
    }
    public void write(InputStream inputStream, OutputStream outputStream)
            throws IOException, XmlPullParserException {
        synchronized (mLock){
            this.parser.setInput(inputStream, null);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            this.serializer.setOutput(writer);
            try{
                this.parserToSerializer.write();
            }catch (Exception ex){
                throw getError(ex);
            }
            writer.close();
            outputStream.close();
            mCurrentPath = null;
        }
    }
    public void write(InputStream inputStream, File file)
            throws IOException, XmlPullParserException {
        File dir = file.getParentFile();
        if(dir != null && !dir.exists()){
            dir.mkdirs();
        }
        mCurrentPath = String.valueOf(file);
        FileOutputStream outputStream = new FileOutputStream(file);
        write(inputStream, outputStream);
    }
    public void write(ResXmlDocument xmlDocument, File file)
            throws IOException, XmlPullParserException {
        mCurrentPath = String.valueOf(file);
        File dir = file.getParentFile();
        if(dir != null && !dir.exists()){
            dir.mkdirs();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        write(xmlDocument, outputStream);
    }
    public void write(ResXmlDocument xmlDocument, OutputStream outputStream)
            throws IOException, XmlPullParserException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        write(xmlDocument, writer);
        writer.close();
        outputStream.close();
    }
    public void write(ResXmlDocument xmlDocument, Writer writer)
            throws IOException, XmlPullParserException {
        synchronized (mLock){
            this.parser.setResXmlDocument(xmlDocument);
            this.serializer.setOutput(writer);
            this.parserToSerializer.write();
            writer.flush();
        }
    }

    public void setFixAttributesNamespace(boolean fixAttributesNamespace) {
        this.fixAttributesNamespace = fixAttributesNamespace;
    }
    @Override
    public ResXmlDocument onDocumentLoaded(ResXmlDocument resXmlDocument) {
        if(fixAttributesNamespace){
            resXmlDocument.autoSetAttributeNamespaces();
        }
        return resXmlDocument;
    }
    private IOException getError(Exception exception){
        String path = mCurrentPath;
        if(exception instanceof IOException){
            String msg = path + ":" + exception.getMessage();
            IOException ioException = new  IOException(msg);
            ioException.setStackTrace(exception.getStackTrace());
            Throwable cause = ioException.getCause();
            if(cause != null){
                ioException.initCause(cause);
            }
            return ioException;
        }
        String msg = path + ":" + exception.getClass() + ":" + exception.getMessage();
        IOException otherException = new IOException(msg);
        otherException.setStackTrace(exception.getStackTrace());
        Throwable cause = otherException.getCause();
        if(cause != null){
            otherException.initCause(cause);
        }
        return otherException;
    }

    private static PackageBlock pickPackage(ApkModule apkModule){
        TableBlock tableBlock = apkModule.getTableBlock();
        if(tableBlock == null){
            throw new NullPointerException("Does not have resource table");
        }
        if(tableBlock.countPackages() == 0){
            // Empty resource table, create dummy package
            tableBlock = new TableBlock();
            return tableBlock.newPackage(0, null);
        }
        return tableBlock.pickOne();
    }
}
