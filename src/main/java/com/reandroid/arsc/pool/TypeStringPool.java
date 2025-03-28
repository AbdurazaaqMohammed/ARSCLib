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
package com.reandroid.arsc.pool;

import com.reandroid.arsc.chunk.TypeBlock;
import com.reandroid.arsc.item.IntegerItem;
import com.reandroid.arsc.item.IntegerReference;
import com.reandroid.arsc.item.TypeString;
import com.reandroid.arsc.list.StringItemList;
import com.reandroid.utils.collection.CollectionUtil;

public class TypeStringPool extends StringPool<TypeString> {

    private final IntegerReference typeIdOffsetReference;

    public TypeStringPool(boolean is_utf8, IntegerItem typeIdOffsetReference) {
        super(is_utf8, false, TypeString::new);
        this.typeIdOffsetReference = typeIdOffsetReference;
    }
    
    public int getLastId() {
        int count = size();
        return toTypeId(count - 1);
    }
    public int idOf(String typeName) {
        return idOf(getByName(typeName));
    }
    
    /**
     * Resolves id of {@link TypeBlock}
     * <br /> Not recommend to use unless you are sure of proper pool
     **/
    public int idOf(TypeString typeString) {
        if (typeString == null) {
            return 0;
        }
        return (toTypeId(typeString.getIndex()));
    }
    
    /**
     * Searches string by type name
     **/
    public TypeString getByName(String name) {
        int size = size();
        for (int i = 0; i < size; i++) {
            TypeString typeString = get(i);
            if (name.equals(typeString.get())) {
                return typeString;
            }
        }
        return null;
    }
    public TypeString getById(int id) {
        return super.get(toIndex(id));
    }
    public TypeString getOrCreate(int typeId, String typeName) {
        ensureStringsSize(toIndex(typeId) + 1);
        TypeString typeString = getById(typeId);
        typeString.set(typeName);
        return typeString;
    }
    private void ensureStringsSize(int size) {
        StringItemList<TypeString> stringsArray = getStringsArray();
        int current = stringsArray.size();
        if (size > current) {
            stringsArray.setSize(size);
            for (int i = current; i < size; i++) {
                stringsArray.get(i).set("type-" + i);
            }
        }
    }
    private int toIndex(int typeId) {
        return typeId - 1 - typeIdOffsetReference.get();
    }
    private int toTypeId(int index) {
        return index + 1 + typeIdOffsetReference.get();
    }
    /**
     * Use getOrCreate(typeId, typeName)}
     **/
    @Deprecated
    @Override
    public final TypeString getOrCreate(String str) {
        TypeString typeString = CollectionUtil.getSingle(getAll(str));
        if (typeString == null) {
            throw new IllegalArgumentException("Can not create TypeString (" + str
                    +") without type id. use getOrCreate(typeId, typeName)");
        }
        return typeString;
    }
}
