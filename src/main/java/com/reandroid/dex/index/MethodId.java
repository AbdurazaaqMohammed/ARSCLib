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
package com.reandroid.dex.index;

import com.reandroid.arsc.base.Block;
import com.reandroid.dex.pool.DexIdPool;
import com.reandroid.dex.sections.Section;
import com.reandroid.dex.sections.SectionType;
import com.reandroid.dex.writer.SmaliWriter;

import java.io.IOException;

public class MethodId extends ItemId {

    private final ItemIndexReference<TypeId> classType;
    private final ItemIndexReference<ProtoId> proto;
    private final ItemIndexReference<StringData> name;

    public MethodId() {
        super(SIZE);
        this.classType = new ItemShortReference<>(SectionType.TYPE_ID, this, 0);
        this.proto = new ItemShortReference<>(SectionType.PROTO_ID, this, 2);
        this.name = new ItemIndexReference<>(SectionType.STRING_DATA, this, 4);
    }

    public String getName(){
        StringData stringData = getNameString();
        if(stringData != null){
            return stringData.getString();
        }
        return null;
    }
    public void setName(String name){
        Section<StringData> stringSection = getSection(SectionType.STRING_DATA);
        DexIdPool<StringData> pool = stringSection.getPool();
        StringData stringData = pool.getOrCreate(name);
        setName(stringData);
    }
    public void setName(StringData stringData){
        this.name.setItem(stringData);
    }
    public String getKey(){
        return getKey(false);
    }
    public String getKey(boolean appendReturnType){
        StringBuilder builder = new StringBuilder();
        TypeId type = getClassType();
        if(type == null){
            return null;
        }
        StringData stringData = type.getNameData();
        if(stringData == null){
            return null;
        }
        builder.append(stringData.getString());
        builder.append("->");
        builder.append(getName());
        ProtoId protoId = getProto();
        if(protoId != null){
            builder.append(protoId.getKey(appendReturnType));
        }
        return builder.toString();
    }
    public String key(){
        return key(true);
    }
    public String key(boolean appendReturnType){
        StringBuilder builder = new StringBuilder();
        builder.append("->");
        builder.append(getName());
        ProtoId protoId = getProto();
        if(protoId != null){
            builder.append(protoId.getKey(appendReturnType));
        }
        return builder.toString();
    }

    public TypeId getClassType(){
        return classType.getItem();
    }
    public StringData getNameString(){
        return name.getItem();
    }
    public ProtoId getProto(){
        return proto.getItem();
    }

    @Override
    public void refresh() {
        classType.refresh();
        proto.refresh();
        name.refresh();
    }
    @Override
    void cacheItems(){
        classType.getItem();
        proto.getItem();
        name.getItem();
    }

    @Override
    public void append(SmaliWriter writer) throws IOException {
        getClassType().append(writer);
        writer.append("->");
        writer.append(getNameString().getString());
        writer.append('(');
        getProto().append(writer);
        writer.append(')');
        getProto().getReturnTypeId().append(writer);
    }
    @Override
    public String toString() {
        return getClassType() + "->" + getNameString() + getProto();
    }

    private static final int SIZE = 8;

}
