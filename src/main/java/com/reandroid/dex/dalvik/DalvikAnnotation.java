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
package com.reandroid.dex.dalvik;

import com.reandroid.dex.program.AnnotatedProgram;
import com.reandroid.dex.key.AnnotationItemKey;
import com.reandroid.dex.key.Key;
import com.reandroid.dex.key.TypeKey;

public class DalvikAnnotation {

    private final AnnotatedProgram annotatedProgram;
    private final TypeKey annotationType;

    protected DalvikAnnotation(AnnotatedProgram annotatedProgram, TypeKey annotationType) {
        this.annotatedProgram = annotatedProgram;
        this.annotationType = annotationType;
    }

    public AnnotationItemKey getKey() {
        return getAnnotatedProgram().getAnnotation(getAnnotationType());
    }
    public void setKey(AnnotationItemKey key) {
        if (!getAnnotationType().equals(key.getType())) {
            throw new IllegalArgumentException("Different annotation type: "
                    + getAnnotationType() + ", " + key.getType());
        }
        getAnnotatedProgram().addAnnotation(key);
    }
    public TypeKey getAnnotationType() {
        return annotationType;
    }
    public AnnotatedProgram getAnnotatedProgram() {
        return annotatedProgram;
    }

    Key readValue(String name) {
        return getKey().getValue(name);
    }
    void writeValue(String name, Key value) {
        setKey(getKey().add(name, value));
    }

    @Override
    public String toString() {
        return getKey().toString();
    }
}
