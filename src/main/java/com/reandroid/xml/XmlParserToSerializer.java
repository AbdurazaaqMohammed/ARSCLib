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
package com.reandroid.xml;

import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.Closeable;
import java.io.IOException;

public class XmlParserToSerializer {

    private final XmlPullParser parser;
    private XmlSerializer serializer;
    private int depth = 0;
    private boolean enableIndent;
    boolean processNamespace;
    boolean reportNamespaceAttrs;

    public XmlParserToSerializer(XmlPullParser parser, XmlSerializer serializer) {
        this.parser = parser;
        this.serializer = XmlIndentingSerializer.create(serializer);
        this.enableIndent = true;
        XMLUtil.setFeatureSafe(parser, XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        XMLUtil.setFeatureSafe(parser, XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES, true);
    }

    public void setEnableIndent(boolean enableIndent) {
        if (enableIndent == this.enableIndent) {
            return;
        }
        this.enableIndent = enableIndent;
        if (enableIndent) {
            this.serializer = XmlIndentingSerializer.create(this.serializer);
        } else if (this.serializer instanceof XmlIndentingSerializer) {
            this.serializer = ((XmlIndentingSerializer) this.serializer).getBaseSerializer();
        }
    }

    public void write() throws IOException, XmlPullParserException {
        XmlPullParser parser = this.parser;

        this.processNamespace = XMLUtil.getFeatureSafe(parser,
                XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        this.reportNamespaceAttrs = XMLUtil.getFeatureSafe(parser,
                XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES, false);

        int event = parser.nextToken();
        while (nextEvent(event)) {
            event = parser.nextToken();
        }
        this.serializer.flush();
        close();
    }
    private void close() throws IOException {
        XmlPullParser parser = this.parser;
        if (parser instanceof Closeable) {
            ((Closeable)parser).close();
        }
        XMLUtil.close(serializer);
    }
    private boolean nextEvent(int event) throws IOException, XmlPullParserException {
        boolean hasNext = event >= 0;
        switch (event) {
            case XmlResourceParser.START_DOCUMENT:
                onStartDocument();
                depth ++;
                break;
            case XmlResourceParser.START_TAG:
                onStartTag();
                depth ++;
                break;
            case XmlResourceParser.TEXT:
                onText();
                break;
            case XmlResourceParser.ENTITY_REF:
                onEntityRef();
                break;
            case XmlResourceParser.CDSECT:
                onCdsect();
                break;
            case XmlResourceParser.COMMENT:
                onComment();
                break;
            case XmlResourceParser.IGNORABLE_WHITESPACE:
                onIgnorableWhitespace();
                break;
            case XmlResourceParser.DOCDECL:
                onDocDecl();
                break;
            case XmlResourceParser.PROCESSING_INSTRUCTION:
                onProcessingInstruction();
                break;
            case XmlResourceParser.END_TAG:
                onEndTag();
                depth --;
                hasNext = depth != 0;
                break;
            case XmlResourceParser.END_DOCUMENT:
                onEndDocument();
                depth --;
                hasNext = depth != 0;
                break;
        }
        return hasNext;
    }

    private void onStartDocument() throws IOException {
        serializer.startDocument(parser.getInputEncoding(), 
                (Boolean) XMLUtil.getPropertySafe(parser, XMLUtil.PROPERTY_XMLDECL_STANDALONE));
    }
    private void onStartTag() throws IOException, XmlPullParserException {
        XmlPullParser parser = this.parser;
        XmlSerializer serializer = this.serializer;

        boolean processNamespace = this.processNamespace;
        boolean countNamespaceAsAttribute = processNamespace && reportNamespaceAttrs;

        if (!countNamespaceAsAttribute) {
            int nsCount = parser.getNamespaceCount(parser.getDepth());
            for (int i = 0; i < nsCount; i++) {
                String prefix = parser.getNamespacePrefix(i);
                String namespace = parser.getNamespaceUri(i);
                serializer.setPrefix(prefix, namespace);
            }
        }
        serializer.startTag(parser.getNamespace(), parser.getName());
        int attrCount = parser.getAttributeCount();
        for (int i=0; i<attrCount; i++) {
            String namespace = processNamespace ?
                    parser.getAttributeNamespace(i) : null;

            serializer.attribute(namespace,
                    parser.getAttributeName(i),
                    parser.getAttributeValue(i));
        }
    }
    private void onText() throws IOException{
        serializer.text(parser.getText());
    }
    private void onEntityRef() throws IOException{
        serializer.entityRef(parser.getName());
    }
    private void onCdsect() throws IOException{
        serializer.cdsect(parser.getText());
    }
    private void onComment() throws IOException{
        serializer.comment(parser.getText());
    }
    private void onEndTag() throws IOException{
        serializer.endTag(parser.getNamespace(), parser.getName());
    }
    private void onEndDocument() throws IOException{
        serializer.endDocument();
    }
    private void onDocDecl() throws IOException{
        serializer.docdecl(parser.getText());
    }
    private void onIgnorableWhitespace() throws IOException{
        serializer.ignorableWhitespace(parser.getText());
    }
    private void onProcessingInstruction() throws IOException{
        serializer.processingInstruction(parser.getText());
    }
}
