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
package com.reandroid.dex.smali.model;

import com.reandroid.dex.key.AnnotationElementKey;
import com.reandroid.dex.key.Key;
import com.reandroid.dex.key.KeyReference;
import com.reandroid.dex.smali.SmaliParseException;
import com.reandroid.dex.smali.SmaliReader;
import com.reandroid.dex.smali.SmaliWriter;

import java.io.IOException;

public class SmaliAnnotationElement extends Smali implements KeyReference {

    private String name;
    private SmaliValue value;

    public SmaliAnnotationElement() {
        super();
    }

    @Override
    public AnnotationElementKey getKey() {
        return AnnotationElementKey.create(getName(), getValueKey());
    }
    @Override
    public void setKey(Key key) {
        AnnotationElementKey elementKey = (AnnotationElementKey) key;
        setName(elementKey.getName());
        setValue(elementKey.getValue());
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Key getValueKey() {
        SmaliValue value = getValue();
        if (value != null) {
            return value.getKey();
        }
        return null;
    }
    public SmaliValue getValue() {
        return value;
    }

    public void setValue(Key key) {
        setValue(SmaliValueFactory.createForValue(key));
    }
    public void setValue(SmaliValue value) {
        this.value = value;
        if (value != null) {
            value.setParent(this);
        }
    }

    @Override
    public void append(SmaliWriter writer) throws IOException {
        AnnotationElementKey key = getKey();
        if (key == null) {
            writer.append("# error");
        } else {
            key.append(writer);
        }
    }

    @Override
    public void parse(SmaliReader reader) throws IOException{
        reader.skipWhitespaces();
        int i1 = reader.indexOfWhiteSpace();
        int i2 = reader.indexOf('=');
        int i;
        if(i1 < i2){
            i = i1;
        }else {
            i = i2;
        }
        int length = i - reader.position();
        setName(reader.readString(length));
        reader.skipWhitespaces();
        SmaliParseException.expect(reader, '=');
        reader.skipWhitespaces();
        SmaliValue smaliValue = SmaliValueFactory.create(reader);
        setValue(smaliValue);
        smaliValue.parse(reader);
    }
}
